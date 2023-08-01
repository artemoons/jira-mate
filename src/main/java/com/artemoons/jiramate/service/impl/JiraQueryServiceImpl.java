package com.artemoons.jiramate.service.impl;

import com.artemoons.jiramate.config.BotConfiguration;
import com.artemoons.jiramate.dto.JiraResponse;
import com.artemoons.jiramate.dto.TodayDate;
import com.artemoons.jiramate.service.JiraQueryService;
import com.artemoons.jiramate.service.MessageComposer;
import com.artemoons.jiramate.service.MessageSender;
import com.artemoons.jiramate.util.SSLUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.DayOfWeek;
import java.time.LocalDate;
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

    @Value("${integration.jira.user-login}")
    private String userLogin;

    @Value("${integration.jira.user-password}")
    private String userPassword;

    RestTemplate template = new RestTemplate();

    MessageSender messageSender;

    MessageComposer messageComposer;

    BotConfiguration botConfiguration;

    @Autowired
    public JiraQueryServiceImpl(final MessageComposer messageComposer,
                                final BotConfiguration botConfiguration) {
        this.botConfiguration = botConfiguration;
        this.messageComposer = messageComposer;
    }

    @Override
    public void getDailyReport() {

        messageSender = new MessageSender(botConfiguration);

        TodayDate today = getCurrentDate();
        DayOfWeek dayOfWeek = today.getDayObject().getDayOfWeek();

        if (dayOfWeek != DayOfWeek.SATURDAY
                || dayOfWeek != DayOfWeek.SUNDAY) {

            Map<String, Double> worklogData = prepareWorklogMap();
            HttpEntity<String> request = prepareRequest(today, false);

            JiraResponse[] response = search(request);

            processWorklogData(worklogData, response);

            String textMessage = messageComposer.prepareDailyMessage(worklogData);
            messageSender.sendMessage(textMessage);
            log.info(" == END OF DAILY REPORT == ");
        }
    }

    @Override
    public void getWeeklyReport() {

        TodayDate today = getCurrentDate();
        DayOfWeek dayOfWeek = today.getDayObject().getDayOfWeek();

        if (dayOfWeek == DayOfWeek.FRIDAY) {
            Map<String, Double> worklogData = prepareWorklogMap();
            HttpEntity<String> request = prepareRequest(today, true);

            JiraResponse[] response = search(request);

            processWorklogData(worklogData, response);
            String textMessage = messageComposer.prepareWeeklyMessage(worklogData);
            messageSender.sendMessage(textMessage);
            log.info(" == END OF WEEKLY REPORT == ");
        }
    }

    private HttpEntity<String> prepareRequest(TodayDate today, boolean fromWeekStart) {
        JSONObject jsonObject = preparePayload(today, fromWeekStart);
        String credentials = prepareCredentials();
        HttpHeaders headers = prepareHeaders(credentials);
        return prepareSearchRequest(jsonObject, headers);
    }

    private void processWorklogData(Map<String, Double> worklogData, JiraResponse[] response) {
        for (JiraResponse item : response) {
            String worklogAuthor = item.getWorklogAuthor();
            if (worklogData.containsKey(worklogAuthor)) {
                Double previousWorklog = worklogData.get(worklogAuthor);
                worklogData.put(worklogAuthor, previousWorklog + item.getTimeSpentSeconds());
            } else {
                worklogData.put(worklogAuthor, item.getTimeSpentSeconds());
            }
        }
    }

    private JiraResponse[] search(HttpEntity<String> request) {
        JiraResponse[] jiraResponse = new JiraResponse[]{};
        try {
            SSLUtil.turnOffSslChecking();
            jiraResponse = template.postForObject(apiUrl, request, JiraResponse[].class);
            SSLUtil.turnOnSslChecking();
        } catch (NoSuchAlgorithmException | KeyManagementException ex) {
            log.error("Can't disable/enable SSL verification");
        } catch (RestClientException ex) {
            throw new RuntimeException("Error occured when connecting to Jira");
        }
        return jiraResponse;
    }

    private HttpEntity<String> prepareSearchRequest(JSONObject jsonObject, HttpHeaders headers) {
        return new HttpEntity<>(jsonObject.toString(), headers);
    }

    private JSONObject preparePayload(TodayDate today, boolean fromWeekStart) {
        int year = today.getYear();
        int month = today.getMonth();
        int day = today.getDay();
        JSONObject jsonObject = new JSONObject();
        if (fromWeekStart) {
            LocalDate monday = today.getDayObject();
            while (monday.getDayOfWeek() != DayOfWeek.MONDAY) {
                monday = monday.minusDays(1);
            }

            int mondayDay = monday.getDayOfMonth();
            int mondayMonth = monday.getMonthValue();
            int mondayYear = monday.getYear();

            jsonObject.put("from", mondayYear + "-" + mondayMonth + "-" + mondayDay);
            jsonObject.put("to", year + "-" + month + "-" + day);
            jsonObject.put("worker", userList);
            log.info(" == WEEKLY REPORT == ");
            log.info("FROM {}-{}-{} TO {}-{}-{}", mondayDay, mondayMonth, mondayYear, day, month, year);
        } else {
            jsonObject.put("from", year + "-" + month + "-" + day);
            jsonObject.put("to", year + "-" + month + "-" + day);
            jsonObject.put("worker", userList);
            log.info(" == DAILY REPORT == ");
            log.info("DATE: {}-{}-{}", day, month, year);
        }
        return jsonObject;
    }

    private Map<String, Double> prepareWorklogMap() {
        Map<String, Double> worklogData = new Hashtable<>();
        userList.forEach(userName -> worklogData.put(userName, 0.0));
        return worklogData;
    }

    private HttpHeaders prepareHeaders(final String credentials) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Basic " + credentials);
        return headers;
    }

    private String prepareCredentials() {
        String plainCreds = userLogin + ":" + userPassword;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        return new String(base64CredsBytes);
    }

    private TodayDate getCurrentDate() {
        LocalDate today = LocalDate.now();
        return TodayDate.builder()
                .day(today.getDayOfMonth())
                .month(today.getMonthValue())
                .year(today.getYear())
                .dayObject(today)
                .build();
    }
}
