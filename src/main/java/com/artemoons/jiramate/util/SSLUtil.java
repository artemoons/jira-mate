package com.artemoons.jiramate.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 * Utility for enabling/disabling SSL connection check.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Slf4j
@Component
public final class SSLUtil {

    /**
     * Private constructor.
     */
    private SSLUtil() {
    }

    /**
     * Trust manager.
     */
    private static final TrustManager[] UNQUESTIONING_TRUST_MANAGER = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(final X509Certificate[] certs, final String authType) {
                }

                public void checkServerTrusted(final X509Certificate[] certs, final String authType) {
                }
            }
    };

    /**
     * Method for disabling SSL connection check.
     *
     * @throws NoSuchAlgorithmException exception
     * @throws KeyManagementException   exception
     */
    public static void turnOffSslCheck() throws NoSuchAlgorithmException, KeyManagementException {
        final SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, UNQUESTIONING_TRUST_MANAGER, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        log.info("SSL verification: DISABLED");
    }

    /**
     * Method for enabling SSL connection check.
     *
     * @throws NoSuchAlgorithmException exception
     * @throws KeyManagementException   exception
     */
    public static void turnOnSslChecking() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext.getInstance("SSL").init(null, null, null);
        log.info("SSL verification: ENABLED");
    }

}
