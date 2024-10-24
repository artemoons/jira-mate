package com.artemoons.jiramate.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;

/**
 * Bot configuration.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Getter
@Configuration
public class BotConfiguration {

    /**
     * Token.
     */
    @Value("${integration.telegram.bot-token}")
    private String botToken;

    /**
     * Bot name.
     */
    @Value("${integration.telegram.bot-name}")
    private String botName;

    /**
     * Chat identifier.
     */
    @Value("${integration.telegram.chat-id}")
    private Long chatId;

    /**
     * Bot options.
     */
    private final DefaultBotOptions botOptions = new DefaultBotOptions();

}
