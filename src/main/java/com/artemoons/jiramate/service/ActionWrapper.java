package com.artemoons.jiramate.service;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
public interface ActionWrapper {

    void processUpdate(Update update);

}
