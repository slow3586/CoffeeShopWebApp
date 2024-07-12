package com.slow3586.drinkshop.telegrambot;

import com.slow3586.drinkshop.api.mainservice.TelegramProcessUpdateResponse;
import com.slow3586.drinkshop.api.mainservice.TelegramPublishTransaction;
import com.slow3586.drinkshop.api.mainservice.TelegramServiceClient;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SpringBootApplication
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@ComponentScan(value = {"com.slow3586.drinkshop.*"})
@EnableFeignClients(basePackages = "com.slow3586.drinkshop.*")
@EnableKafkaStreams
@Slf4j
@EnableKafka
public class TelegramBotApplication {
    @Value("${app.bot.token}")
    @NonFinal
    String botToken;
    @Value("${app.bot.name}")
    @NonFinal
    String botName;
    TelegramServiceClient telegramServiceClient;
    StreamsBuilder streamsBuilder;

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
                    final TelegramProcessUpdateResponse process = telegramServiceClient.process(update);

                    if (process.getSendImageBytes() != null) {
                        this.execute(SendPhoto.builder()
                            .chatId(update.getMessage().getChatId())
                            .photo(new InputFile(new ByteArrayInputStream(process.getSendImageBytes()),
                                process.getSendImageName()))
                            .build());
                    }

                    if (process.getSendText() != null) {
                        SendMessage.SendMessageBuilder reply = SendMessage
                            .builder()
                            .chatId(update.getMessage().getChatId())
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

    @Bean
    public NewTopic telegramPublishTransactionTopic() {
        return TopicBuilder.name("telegram_publish_transaction")
            .replicas(1)
            .partitions(1)
            .compact()
            .build();
    }

    @PostConstruct
    public void telegramPublishTransactionStream() {
        streamsBuilder.stream("telegram_publish_transaction", Consumed.with(Serdes.String(), new JsonSerde<TelegramPublishTransaction>()))
            .filter((k, op) -> op.getSent() == null)
            .mapValues((k, operation) -> {
                try {
                    bot().execute(SendMessage.builder()
                        .chatId(operation.getCustomer().getTelegramId())
                        .text(operation.getText())
                        .build());
                } catch (Exception e) {
                    log.error("#telegramPublishTransactionStream", e);
                    return operation.setSent(false);
                }
                return operation.setSent(true);
            }).to("telegram_publish_transaction");
    }
}
