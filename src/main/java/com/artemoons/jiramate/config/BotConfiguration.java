package com.artemoons.jiramate.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;

/**
 * Конфигурация бота.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Getter
@Configuration
public class BotConfiguration {

    /**
     * Токен.
     */
    @Value("${integration.telegram.bot-token}")
    private String botToken;

    /**
     * Имя бота.
     */
    @Value("${integration.telegram.bot-name}")
    private String botName;

    /**
     * Идентификатор чата.
     */
    @Value("${integration.telegram.chat-id}")
    private Long chatId;

    /**
     * Опции.
     */
    private final DefaultBotOptions botOptions = new DefaultBotOptions();

}
