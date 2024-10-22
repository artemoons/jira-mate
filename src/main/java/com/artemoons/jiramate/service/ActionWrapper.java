package com.artemoons.jiramate.service;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Интерфейс для работы с обёрткой над действиями.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
public interface ActionWrapper {

    /**
     * Метод обработки события.
     *
     * @param update событие (сообщние)
     */
    void processUpdate(Update update);

}
