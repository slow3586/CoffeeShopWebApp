package com.slow3586.drinkshop.telegrambot;

import com.slow3586.drinkshop.api.mainservice.TelegramServiceClient;
import com.slow3586.drinkshop.api.mainservice.dto.TelegramProcessResponse;
import com.slow3586.drinkshop.api.telegrambot.TelegramProcessRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.ByteArrayInputStream;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Value("${app.bot.name}")
    String name;
    TelegramServiceClient telegramServiceClient;

    public TelegramBot(@Value("${app.bot.token}") String token) {
        super(token);
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            final Message message = update.getMessage();
            if (message == null) {return;}

            TelegramProcessRequest telegramProcessRequest = new TelegramProcessRequest()
                .setTelegramId(String.valueOf(message.getChatId()))
                .setText(message.getText());
            if (message.hasContact()) {
                telegramProcessRequest.setPhone(message.getContact().getPhoneNumber())
                    .setName(message.getContact().getFirstName());
            }
            final TelegramProcessResponse process =
                telegramServiceClient.process(telegramProcessRequest);

            if (process.getSendImageBytes() != null) {
                this.execute(SendPhoto.builder()
                    .chatId(message.getChatId())
                    .photo(new InputFile(new ByteArrayInputStream(process.getSendImageBytes()),
                        process.getSendImageName()))
                    .build());
            }

            if (process.getSendText() != null) {
                SendMessage.SendMessageBuilder reply = SendMessage
                    .builder()
                    .chatId(message.getChatId())
                    .text(process.getSendText());

                if (process.getSendTextKeyboard() != null && !process.getSendTextKeyboard().isEmpty()) {
                    reply.replyMarkup(new ReplyKeyboardMarkup(
                        process.getSendTextKeyboard()
                            .stream()
                            .map(l -> new KeyboardRow(l.stream()
                                .map(KeyboardButton::new)
                                .toList()))
                            .toList()));
                }

                if (process.getTags() != null && process.getTags().contains("REQUEST_CONTACT")) {
                    final KeyboardButton keyboardButton =
                        new KeyboardButton("Поделиться номером телефона");
                    keyboardButton.setRequestContact(true);
                    reply.replyMarkup(new ReplyKeyboardMarkup(
                        List.of(new KeyboardRow(List.of(keyboardButton)))));
                }

                this.execute(reply.build());
            }
        } catch (Exception e) {
            throw new RuntimeException("#onUpdateReceived: " + e.getMessage(), e);
        }
    }
}
