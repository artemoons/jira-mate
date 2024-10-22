package com.artemoons.jiramate.service;

import com.artemoons.jiramate.config.BotConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Сервис отправки сообщений.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Slf4j
@Service
public class MessageSender extends DefaultAbsSender {

    /**
     * Конфигурация бота.
     */
    private final BotConfiguration botConfiguration;

    /**
     * Конструктор.
     *
     * @param config конфигурация бота
     */
    @Autowired
    public MessageSender(final BotConfiguration config) {
        super(config.getBotOptions(), config.getBotToken());
        this.botConfiguration = config;
    }

    /**
     * Метод отправки сообщения.
     *
     * @param textMessage текст сообщения
     */
    public void sendMessage(final String textMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(botConfiguration.getChatId());
        message.setText(textMessage);
        message.enableMarkdownV2(true);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Can't send message to the chat, more information in stacktrace below:");
            e.printStackTrace();
        }
    }

}
