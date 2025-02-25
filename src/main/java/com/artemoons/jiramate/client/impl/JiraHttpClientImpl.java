package com.artemoons.jiramate.client.impl;

import com.artemoons.jiramate.client.JiraHttpClient;
import com.artemoons.jiramate.dto.JiraPayload;
import com.artemoons.jiramate.dto.JiraResponse;
import com.artemoons.jiramate.dto.WorktimeResponse;
import com.artemoons.jiramate.util.SSLUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
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
    public final JiraResponse[] getWorklogs(final JiraPayload payload) {
        ResponseEntity<JiraResponse[]> response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        ObjectMapper mapper = new ObjectMapper();
        try {
            SSLUtil.turnOffSslCheck();
            response = template.exchange(searchApiUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(mapper.writeValueAsString(payload), getHeaders()),
                    JiraResponse[].class);
            SSLUtil.turnOnSslChecking();
        } catch (NoSuchAlgorithmException | KeyManagementException ex) {
            log.error("Can't disable/enable SSL verification");
        } catch (HttpServerErrorException ex) {
            log.error("Jira API returned error, code: {}", ex.getStatusCode().value());
            throw new RuntimeException("Detailed stacktrace: " + ex.getResponseBodyAsString());
        } catch (JsonProcessingException ex) {
            log.error("Error occurred when trying to make JSON from object {}", ex.getMessage());
            throw new RuntimeException(ex);
        }
        return response.getBody();
    }

    /**
     * {@inheritDoc}
     */
    public final WorktimeResponse getRequiredTimeForPeriod(final JiraPayload payload) {
        String payloadUrl = String.format("?from=%s&to=%s", payload.getFromDate(), payload.getToDate());
        ResponseEntity<WorktimeResponse> response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        ObjectMapper mapper = new ObjectMapper();
        try {
            SSLUtil.turnOffSslCheck();
            response = template.exchange(worktimeApiUrl + payloadUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(mapper.writeValueAsString(payload), getHeaders()),
                    WorktimeResponse.class,
                    new HashMap<>());
            SSLUtil.turnOnSslChecking();
        } catch (NoSuchAlgorithmException | KeyManagementException ex) {
            log.error("Can't disable/enable SSL verification");
        } catch (HttpServerErrorException ex) {
            log.error("Jira API returned error, code: {}", ex.getStatusCode().value());
            throw new RuntimeException("Detailed stacktrace: " + ex.getResponseBodyAsString());
        } catch (JsonProcessingException ex) {
            log.error("Error occurred when trying to make JSON from object {}", ex.getMessage());
            throw new RuntimeException(ex);
        }
        return response.getBody();
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
