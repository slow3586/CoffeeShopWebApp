package com.slow3586.drinkshop.telegrambot;

import com.slow3586.drinkshop.api.mainservice.TelegramProcessResponse;
import com.slow3586.drinkshop.api.mainservice.TelegramServiceClient;
import com.slow3586.drinkshop.api.telegrambot.TelegramProcessRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.ByteArrayInputStream;
import java.util.List;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@ComponentScan(value = {"com.slow3586.drinkshop.*"})
@EnableFeignClients(basePackages = "com.slow3586.drinkshop.*")
@Slf4j
public class TelegramBotApplication {
    @Value("${app.bot.token}")
    @NonFinal
    String botToken;
    @Value("${app.bot.name}")
    @NonFinal
    String botName;
    TelegramServiceClient telegramServiceClient;

    public static void main(String[] args) {
        SpringApplication.run(TelegramBotApplication.class, args);
    }

    @Bean
    public TelegramLongPollingBot bot() {
        return new TelegramLongPollingBot(botToken) {
            @Override
            public String getBotUsername() {
                return botName;
            }

            @Override
            public void onUpdateReceived(Update update) {
                try {
                    final Message message = update.getMessage();
                    if (message == null) {return;}

                    TelegramProcessRequest telegramProcessRequest = new TelegramProcessRequest()
                        .setCustomerId(String.valueOf(message.getChatId()))
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
        };
    }

    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        final TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot());
        return telegramBotsApi;
    }
}
