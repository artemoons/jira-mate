package com.artemoons.jiramate.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Slf4j
public class BotInitializer extends TelegramLongPollingBot {

    /**
     * Обёртка над исполнителем действий.
     */
    private final ActionWrapper actionWrapper;

    /**
     * Имя Telegram бота.
     */
    private final String telegramBotName;

    public BotInitializer(final String token, final String name, final ActionWrapper actionWrapper) {
        super(token);
        this.telegramBotName = name;
        this.actionWrapper = actionWrapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdateReceived(final Update update) {
        if (update.hasMessage()) {
            actionWrapper.processUpdate(update);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBotUsername() {
        return telegramBotName;
    }

}
