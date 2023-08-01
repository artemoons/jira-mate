package com.artemoons.jiramate.service;

import com.artemoons.jiramate.config.BotConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

/**
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Service
public class MessageSender extends DefaultAbsSender {

    BotConfiguration botConfiguration;

    @Autowired
    public MessageSender(BotConfiguration botConfiguration) {
        super(botConfiguration.getBotOptions(), botConfiguration.getBotToken());
        this.botConfiguration = botConfiguration;
    }

    public void sendMessage(final String textMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(botConfiguration.getChatId());
        message.setText(textMessage);
        message.enableMarkdownV2(true);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
