package com.artemoons.jiramate.service;

import java.util.Map;

/**
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
public interface MessageComposer {

    String prepareDailyMessage(Map<String, Double> worklogData);

    String prepareWeeklyMessage(Map<String, Double> worklogData);

}
