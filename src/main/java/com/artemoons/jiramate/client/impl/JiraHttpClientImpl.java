package com.artemoons.jiramate.client.impl;

import com.artemoons.jiramate.client.JiraHttpClient;
import com.artemoons.jiramate.dto.JiraResponse;
import com.artemoons.jiramate.dto.WorktimeResponse;
import com.artemoons.jiramate.util.SSLUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * HTTP-клиент для работы с Jira.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Slf4j
@Service
public class JiraHttpClientImpl implements JiraHttpClient {

    /**
     * RestTemplate.
     */
    private final RestTemplate template = new RestTemplate();
    /**
     * API поиска worklog.
     */
    @Value("${integration.jira.search-api-url}")
    private String searchApiUrl;
    /**
     * API поиска доступного времени работы.
     */
    @Value("${integration.jira.worktime-api-url}")
    private String worktimeApiUrl;
    /**
     * Логин Jira.
     */
    @Value("${integration.jira.user-login}")
    private String userLogin;
    /**
     * Пароль Jira.
     */
    @Value("${integration.jira.user-password}")
    private String userPassword;

    /**
     * {@inheritDoc}
     */
    public final JiraResponse[] getWorklogs(final JSONObject payload) {
        JiraResponse[] jiraResponse = new JiraResponse[]{};
        try {
            SSLUtil.turnOffSslCheck();
            jiraResponse = template.postForObject(searchApiUrl,
                    new HttpEntity<>(payload.toString(), getHeaders()), JiraResponse[].class);
            SSLUtil.turnOnSslChecking();
        } catch (NoSuchAlgorithmException | KeyManagementException ex) {
            log.error("Can't disable/enable SSL verification");
        } catch (RestClientException ex) {
            throw new RuntimeException("Error occurred when connecting to Jira, check credentials first!");
        }
        return jiraResponse;
    }

    /**
     * {@inheritDoc}
     */
    public final WorktimeResponse getRequiredTimeForPeriod(final JSONObject payload) {
        String payloadUrl = String.format("?from=%s&to=%s", payload.get("from"), payload.get("to"));
        WorktimeResponse jiraResponse = new WorktimeResponse();
        try {
            SSLUtil.turnOffSslCheck();
            jiraResponse = template.exchange(worktimeApiUrl + payloadUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(payload.toString(), getHeaders()),
                    WorktimeResponse.class,
                    new HashMap<>()).getBody();
            SSLUtil.turnOnSslChecking();
        } catch (NoSuchAlgorithmException | KeyManagementException ex) {
            log.error("Can't disable/enable SSL verification");
        } catch (RestClientException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException("Error occurred when connecting to Jira, check credentials first!");
        }
        return jiraResponse;
    }

    /**
     * Вспомогательный метод для подготовки заголовков обращения к Jira.
     *
     * @return заголовки
     */
    private HttpHeaders getHeaders() {
        String plainCreds = userLogin + ":" + userPassword;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Basic " + new String(base64CredsBytes));
        return headers;
    }
}
