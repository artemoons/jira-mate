package com.artemoons.jiramate.config;

import com.artemoons.jiramate.service.JiraQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

/**
 * Scheduler configuration.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Slf4j
@Component
@EnableScheduling
public class ScheduleRunnerConfiguration {

    /**
     * Every day CRON.
     */
    @Value("${application.cron-time.daily}")
    private String dailyCron;

    /**
     * Every week CRON.
     */
    @Value("${application.cron-time.weekly}")
    private String weeklyCron;

    /**
     * Jira service.
     */
    private final JiraQueryService service;

    /**
     * Constructor.
     *
     * @param queryService Jira service
     */
    @Autowired
    public ScheduleRunnerConfiguration(final JiraQueryService queryService) {
        this.service = queryService;
    }

    /**
     * Method for everyday run.
     */
//    @Scheduled(cron = "${application.cron-time.daily}")
    public final void runDaily() {
        log.info("Run daily CRON schedule: {}", dailyCron);
        service.getDailyReport();
    }

    /**
     * Method for every week run.
     */
//    @Scheduled(cron = "${application.cron-time.weekly}")
    public final void runWeekly() {
        log.info("Run weekly CRON schedule: {}", weeklyCron);
        service.getWeeklyReport();
    }
}
