package com.artemoons.jiramate.client.impl;

import com.artemoons.jiramate.client.JiraHttpClient;
import com.artemoons.jiramate.dto.JiraResponse;
import com.artemoons.jiramate.dto.WorktimeResponse;
import com.artemoons.jiramate.util.SSLUtil;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Base64;
import java.util.HashMap;

/**
 * Jira HTTP client.
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
     * Worklog Jira API.
     */
    @Value("${integration.jira.search-api-url}")
    private String searchApiUrl;
    /**
     * Tempo Jira API.
     */
    @Value("${integration.jira.worktime-api-url}")
    private String worktimeApiUrl;
    /**
     * Jira login.
     */
    @Value("${integration.jira.user-login}")
    private String userLogin;
    /**
     * Jira password.
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
     * Auxiliary method for preparing Jira HTTP headers.
     *
     * @return headers
     */
    private HttpHeaders getHeaders() {
        String plainCreds = userLogin + ":" + userPassword;
        String base64Creds = Base64.getEncoder().encodeToString(plainCreds.getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Basic " + base64Creds);
        return headers;
    }
}
