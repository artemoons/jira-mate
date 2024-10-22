package com.artemoons.jiramate.component;

import com.artemoons.jiramate.service.ActionWrapper;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Вспомогательный класс для инициализации бота.
 *
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

    /**
     * Конструктор.
     *
     * @param token   токен
     * @param name    имя бота
     * @param wrapper обёртка над исполнителем действий
     */
    public BotInitializer(final String token, final String name, final ActionWrapper wrapper) {
        super(token);
        this.telegramBotName = name;
        this.actionWrapper = wrapper;
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
