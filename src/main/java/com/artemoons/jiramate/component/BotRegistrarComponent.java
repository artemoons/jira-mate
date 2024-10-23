package com.artemoons.jiramate.component;

import com.artemoons.jiramate.config.BotConfiguration;
import com.artemoons.jiramate.service.ActionWrapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Вспомогательный класс для регистрации бота.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Slf4j
@Component
public class BotRegistrarComponent {
    /**
     * Обёртка над исполнителем действий.
     */
    private final ActionWrapper wrapper;
    /**
     * Токен доступа бота Telegram.
     */
    @Value("${integration.telegram.bot-token}")
    private String telegramBotToken;
    /**
     * Имя Telegram бота.
     */
    @Value("${integration.telegram.bot-name}")
    private String telegramBotName;

    /**
     * Конфигурация бота.
     */
    private final BotConfiguration botConfiguration;

    /**
     * Конструктор.
     *
     * @param actionWrapper обёртка над исполнителем действий
     * @param config        конфигурация бота
     */
    @Autowired
    public BotRegistrarComponent(final ActionWrapper actionWrapper, final BotConfiguration config) {
        this.wrapper = actionWrapper;
        this.botConfiguration = config;
    }

    /**
     * Метод для регистрации бота.
     */
    @PostConstruct
    private void registerBot() {
        log.info("Trying to register bot...");
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new BotInitializer(telegramBotToken, telegramBotName, wrapper));
            log.info("Success! Bot registered!");
            log.info("Parameters:");
            log.info("Token: " + botConfiguration.getBotToken());
            log.info("Name: " + botConfiguration.getBotName());
            log.info("Chat ID: " + botConfiguration.getChatId().toString());
        } catch (TelegramApiException e) {
            log.error("Failure! Error occurred when trying to register bot!");
            e.printStackTrace();
        }
    }
}
