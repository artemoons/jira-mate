package com.artemoons.jiramate.service;

import com.artemoons.jiramate.config.BotConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Message sender service.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Slf4j
@Service
public class MessageSender extends DefaultAbsSender {

    /**
     * Bot configuration.
     */
    private final BotConfiguration botConfiguration;

    /**
     * Constructor.
     *
     * @param config bot configuration
     */
    @Autowired
    public MessageSender(final BotConfiguration config) {
        super(config.getBotOptions(), config.getBotToken());
        this.botConfiguration = config;
    }

    /**
     * Method for message send.
     *
     * @param textMessage message text
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
