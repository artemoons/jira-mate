package com.artemoons.jiramate.service.impl;

import com.artemoons.jiramate.dto.JiraIssueInfo;
import com.artemoons.jiramate.service.JiraQueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Slf4j
@Service
public class JiraQueryServiceImpl implements JiraQueryService {

    @Value("${integration.jira.user-list}")
    private List<String> userList;

    @Value("${integration.jira.api-url}")
    private String apiUrl;


    @Override
    public void getDailyReport() {

        Calendar calendar = Calendar.getInstance();

        Map<String, Integer> worklogData = new Hashtable<>();
        userList.forEach(userName -> worklogData.put(userName, 0));


        RestTemplate template = new RestTemplate();

        String plainCreds = "69utkinak:ArtemyWentPro000";
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Basic " + base64Creds);


        int day = getDay(calendar);
        int month = getMonth(calendar);
        int year = getYear(calendar);


        if (day != Calendar.SATURDAY || day != Calendar.SUNDAY) {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("from", year + "-" + month + "-" + day);
            jsonObject.put("to", year + "-" + month + "-" + day);
            jsonObject.put("worker", userList);


            HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), headers);
            JiraIssueInfo[] response = template.postForObject(apiUrl, request, JiraIssueInfo[].class);
            JiraIssueInfo[] body = response;


            for (JiraIssueInfo item : body) {
                String worklogAuthor = item.getWorklogAuthor();
                if (worklogData.containsKey(worklogAuthor)) {
                    Integer previousWorklog = worklogData.get(worklogAuthor);
                    worklogData.put(worklogAuthor, previousWorklog + item.getTimeSpentSeconds());
                } else {
                    worklogData.put(worklogAuthor, item.getTimeSpentSeconds());
                }
            }


            worklogData.forEach((key, value) -> {
                log.info("{} - {}", key, value / 3600);
                if (value != 0 && value < 28800) {
                    log.warn("TIME SPENT LESS THAN 8 HOURS");
                }
            });


            int sum = Arrays.stream(body).mapToInt(JiraIssueInfo::getTimeSpentSeconds).sum() / 3600;

            log.info("total hours: " + sum);
            getWeeklyReport();

        }

    }

    private int getYear(Calendar calendar) {
        return calendar.get(Calendar.YEAR);
    }

    private int getMonth(Calendar calendar) {
        return calendar.get(Calendar.MONTH) + 1;
    }

    private int getDay(Calendar calendar) {
        return calendar.get(Calendar.DATE);
    }

    @Override
    public void getWeeklyReport() {

        Map<String, Integer> worklogData = new Hashtable<>();
        userList.forEach(userName -> worklogData.put(userName, 0));

        LocalDate today = LocalDate.now();

        if (today.getDayOfWeek() != DayOfWeek.FRIDAY) {


            RestTemplate template = new RestTemplate();

            String plainCreds = "69utkinak:ArtemyWentPro000";
            byte[] plainCredsBytes = plainCreds.getBytes();
            byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
            String base64Creds = new String(base64CredsBytes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Basic " + base64Creds);


            LocalDate monday = today;
            while (monday.getDayOfWeek() != DayOfWeek.MONDAY) {
                monday = monday.minusDays(1);
            }

            int mondayDay = monday.getDayOfMonth();
            int mondayMonth = monday.getMonthValue();
            int mondayYear = monday.getYear();

            int day = today.getDayOfMonth();
            int month = today.getMonthValue();
            int year = today.getYear();


            JSONObject jsonObject = new JSONObject();
            jsonObject.put("from", mondayYear + "-" + mondayMonth + "-" + mondayDay);
            jsonObject.put("to", year + "-" + month + "-" + day);
            jsonObject.put("worker", userList);

            log.info(" == WEEKLY REPORT == ");
            log.info("FROM {}-{}-{} TO {}-{}-{}", mondayYear, mondayMonth, mondayDay, year, month, day);


            HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), headers);
            JiraIssueInfo[] response = template.postForObject(apiUrl, request, JiraIssueInfo[].class);
            JiraIssueInfo[] body = response;


            for (JiraIssueInfo item : body) {
                String worklogAuthor = item.getWorklogAuthor();
                if (worklogData.containsKey(worklogAuthor)) {
                    Integer previousWorklog = worklogData.get(worklogAuthor);
                    worklogData.put(worklogAuthor, previousWorklog + item.getTimeSpentSeconds());
                } else {
                    worklogData.put(worklogAuthor, item.getTimeSpentSeconds());
                }
            }


            worklogData.forEach((key, value) -> {
                log.info("{} - {}", key, value / 3600);
                if (value != 0 && value < 144000) {
                    log.warn("TIME SPENT LESS THAN 40 HOURS");
                }
            });


        }


    }
}
