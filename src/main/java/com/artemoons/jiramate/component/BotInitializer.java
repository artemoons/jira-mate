package com.artemoons.jiramate.component;

import com.artemoons.jiramate.service.ActionWrapper;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Auxiliary class for bot initialization.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Slf4j
public class BotInitializer extends TelegramLongPollingBot {

    /**
     * Action wrapper.
     */
    private final ActionWrapper actionWrapper;

    /**
     * Telegram bot name.
     */
    private final String telegramBotName;

    /**
     * Constructor.
     *
     * @param token   token
     * @param name    bot name
     * @param wrapper action wrapper
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
