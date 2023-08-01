package com.artemoons.jiramate.component;

import com.artemoons.jiramate.service.ActionWrapper;
import com.artemoons.jiramate.service.BotInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;

/**
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
     * Конструктор.
     *
     * @param actionWrapper обёртка над исполнителем действий
     */
    @Autowired
    public BotRegistrarComponent(final ActionWrapper actionWrapper) {
        this.wrapper = actionWrapper;
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
            log.info("Bot registered!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
