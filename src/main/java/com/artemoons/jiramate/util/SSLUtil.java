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
 * Утилита для включения/отключения проверки SSL соединения.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Slf4j
@Component
public final class SSLUtil {

    /**
     * Private конструктор.
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
     * Метод для отключения проверки SSL.
     *
     * @throws NoSuchAlgorithmException исключение
     * @throws KeyManagementException   исключение
     */
    public static void turnOffSslCheck() throws NoSuchAlgorithmException, KeyManagementException {
        final SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, UNQUESTIONING_TRUST_MANAGER, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        log.info("SSL verification: DISABLED");
    }

    /**
     * Метод для включения проверки SSL.
     *
     * @throws KeyManagementException   исключение
     * @throws NoSuchAlgorithmException исключение
     */
    public static void turnOnSslChecking() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext.getInstance("SSL").init(null, null, null);
        log.info("SSL verification: ENABLED");
    }

}
