package com.artemoons.jiramate.service.impl;

import com.artemoons.jiramate.service.MessageComposer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Slf4j
@Component
public class MessageComposerImpl implements MessageComposer {

    public static final Double TO_HOURS = 3_600.0;
    public static final Double EIGHT_HOURS_IN_MS = 28_800.0;
    public static final Double FOURTY_HOURS_IN_MS = 144_000.0;

    @Override
    public String prepareDailyMessage(Map<String, Double> worklogData) {

        final String[] userWorklogInfo = {""};

        worklogData.forEach((key, value) -> {
            Double timeInHours = value / TO_HOURS;
            log.info("{} - {}", key, timeInHours);
            String emojiIcon = "✅";
            if (value == 0 || value < EIGHT_HOURS_IN_MS) {
                emojiIcon = "❌";
            }
            userWorklogInfo[0] = userWorklogInfo[0].concat(String.format(emojiIcon + " %s \\- %.2f ч\\. \n", key, timeInHours));
        });

        return "🤖 Сводка по записям о работе *за сегодня*:\n\n"
                + userWorklogInfo[0]
                + "\n↪ [Перейти в Jira](https://jira.cbr.ru/secure/Dashboard.jspa)";
    }

    @Override
    public String prepareWeeklyMessage(Map<String, Double> worklogData) {

        final String[] userWorklogInfo = {""};
        worklogData.forEach((key, value) -> {
            Double timeInHours = value / TO_HOURS;
            log.info("{} - {}", key, timeInHours);

            String emojiIcon = "✅";
            if (value == 0 || value < FOURTY_HOURS_IN_MS) {
                emojiIcon = "❌";
            }
            userWorklogInfo[0] = userWorklogInfo[0].concat(String.format(emojiIcon + " %s \\- %.2f ч\\. \n", key, timeInHours));

        });

        return "🤖 Сводка по записям о работе *за неделю*:\n\n"
                + userWorklogInfo[0]
                + "\n↪ [Перейти в Jira](https://jira.cbr.ru/secure/Dashboard.jspa)";

    }
}
