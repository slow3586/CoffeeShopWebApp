package com.slow3586.drinkshop.mainservice.telegram;

import com.slow3586.drinkshop.api.GetQrCodeResponse;
import com.slow3586.drinkshop.api.TelegramProcessUpdateResponse;
import com.slow3586.drinkshop.mainservice.customer.CustomerEntity;
import com.slow3586.drinkshop.mainservice.customer.CustomerRepository;
import com.slow3586.drinkshop.mainservice.customer.CustomerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

@RestController
@RequestMapping("api/telegram")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class TelegramService {
    static String GET_ALL_DEALS = "Акции";
    static String GET_MY_VIRTUAL_CARD = "Виртуальная карта";
    static String GET_MY_POINTS = "Баллы";
    CustomerService customerService;
    CustomerRepository customerRepository;

    @PostMapping
    public TelegramProcessUpdateResponse process(@RequestBody Update update) {
        final TelegramProcessUpdateResponse response = new TelegramProcessUpdateResponse();
        if (!update.hasMessage()) {return response;}

        final User telegramUser = update.getMessage().getFrom();
        final String messageText = update.getMessage().getText();

        CustomerEntity customer = customerRepository.getByTelegramId(telegramUser.getId().toString());

        //region NEW
        if (customer == null) {
            customer = customerRepository.save(
                CustomerEntity.builder()
                    .telegramId(telegramUser.getId().toString())
                    .build());
        }
        //endregion

        //region REGISTRATION
        if (customer.getPhoneNumber() == null) {
            if (update.getMessage().hasContact()) {
                final Contact contact = update.getMessage().getContact();
                final String phoneNumber = contact.getPhoneNumber();
                final String name = contact.getFirstName();
                customer.setPhoneNumber(phoneNumber);
                customer.setName(name);

                if (phoneNumber.startsWith("+7") && phoneNumber.length() == 12) {
                    response.setSendText("Приятно познакомиться, " + name + "! Регистрация прошла успешно.");
                } else {
                    customer.setBlockedReason("BAD_PHONE_NUMBER");
                    response.setSendText("Извини, но для использования приложения необходим российский номер телефона.");
                }

                customer = customerRepository.save(customer);
            } else {
                response.setSendText("Привет! Для завершения регистрации необходимо поделиться своим контактом.");
                response.getTags().add("REQUEST_CONTACT");
            }
        }
        //endregion

        //region LOGIC
        if (customer.getPhoneNumber() != null && customer.getBlockedReason() == null) {
            if (GET_MY_VIRTUAL_CARD.equals(messageText)) {
                GetQrCodeResponse qrCode = customerService.getQrCode(customer.getTelegramId());

                response.setSendImageName("qrcode.png");
                response.setSendImageBytes(qrCode.getImage());
                response.setSendText("Временный код - "
                    + qrCode.getCode().substring(0, 2)
                    + " " + qrCode.getCode().substring(2, 4)
                    + " " + qrCode.getCode().substring(4, 6));
            } else if (GET_MY_POINTS.equals(messageText)) {
                double points = customer.getPoints();
                if (points == 0) {
                    response.setSendText("У тебя пока ещё нет баллов.");
                } else {
                    response.setSendText("Твои баллы - " + points);
                }
            } else if (GET_ALL_DEALS.equals(messageText)) {
                response.setSendText("В данный момент нет акций.");
            } else if ("/start".equals(messageText)) {
                response.setSendText("Добро пожаловать обратно, " + customer.getName() + "!");
            }

            if (response.getSendText() == null) {
                response.setSendText("Перевожу тебя в главное меню.");
            }
            response.setSendTextKeyboard(List.of(List.of(
                GET_MY_VIRTUAL_CARD, GET_MY_POINTS, GET_ALL_DEALS)));
        }
        //endregion

        return response;
    }
}
