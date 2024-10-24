package com.artemoons.jiramate.service;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Interface for actions wrapper.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
public interface ActionWrapper {

    /**
     * Event update processor.
     *
     * @param update event (message sent to bot)
     */
    void processUpdate(Update update);

}
