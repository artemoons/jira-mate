package com.artemoons.jiramate.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;

/**
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Configuration
@Getter
public class BotConfiguration {

    @Value("${integration.telegram.bot-token}")
    private String botToken;

    @Value("${integration.telegram.bot-name}")
    private String botName;

    @Value("${integration.telegram.chat-id}")
    private Long chatId;

    private final DefaultBotOptions botOptions = new DefaultBotOptions();

}
