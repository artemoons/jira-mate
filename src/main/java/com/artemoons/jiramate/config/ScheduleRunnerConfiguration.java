package com.artemoons.jiramate.config;

import com.artemoons.jiramate.service.JiraQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

/**
 * Конфигурация планировщика.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Slf4j
@Component
@EnableScheduling
public class ScheduleRunnerConfiguration {

    /**
     * CRON для ежедневного выполнения.
     */
    @Value("${application.cron-time.daily}")
    private String dailyCron;

    /**
     * CRON для еженедельного выполнения.
     */
    @Value("${application.cron-time.weekly}")
    private String weeklyCron;

    /**
     * Сервис для работы с Jira.
     */
    private final JiraQueryService service;

    /**
     * Конструктор.
     *
     * @param queryService сервис для работы с Jira
     */
    @Autowired
    public ScheduleRunnerConfiguration(final JiraQueryService queryService) {
        this.service = queryService;
    }

    /**
     * Метод для ежедневного запуска.
     */
//    @Scheduled(cron = "${application.cron-time.daily}")
    public final void runDaily() {
        log.info("Run daily CRON schedule: {}", dailyCron);
        service.getDailyReport();
    }

    /**
     * Метод для еженедельного запуска.
     */
//    @Scheduled(cron = "${application.cron-time.weekly}")
    public final void runWeekly() {
        log.info("Run weekly CRON schedule: {}", weeklyCron);
        service.getWeeklyReport();
    }
}
