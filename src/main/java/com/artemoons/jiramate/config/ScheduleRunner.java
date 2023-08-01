package com.artemoons.jiramate.config;

import com.artemoons.jiramate.service.JiraQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Slf4j
@Component
@EnableScheduling
public class ScheduleRunner {

    @Value("${application.cron-time.daily}")
    private String dailyCron;

    @Value("${application.cron-time.weekly}")
    private String weeklyCron;

    JiraQueryService service;

    @Autowired
    public ScheduleRunner(JiraQueryService service) {
        this.service = service;
    }

    @Scheduled(cron = "${application.cron-time.daily}")
    public final void runDaily() {
        log.info("Run daily CRON schedule: {}", dailyCron);
        service.getDailyReport();
    }

    @Scheduled(cron = "${application.cron-time.weekly}")
    public final void runWeekly() {
        log.info("Run weekly CRON schedule: {}", weeklyCron);
        service.getWeeklyReport();
    }
}
