package com.win.sumsub.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@RestController
public class SubmitTransaction {

    private static final String SUMSUB_API_URL = "https://api.sumsub.com/resources/applicants/67c590dafa06707def346e15/kyt/txns/-/data";
    private static final String APP_TOKEN = "your_app_token";
    private static final String SECRET_KEY = "your_secret_key";

    @PostMapping("/submitTransaction")
    public String submitTransaction() {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-App-Token", APP_TOKEN);

            // Generate timestamp
            long timestamp = Instant.now().getEpochSecond();
            headers.set("X-App-Access-Ts", String.valueOf(timestamp));

            // Generate signature
            String signature = generateSignature(timestamp, "POST", "/resources/applicants/{applicantId}/kyt/txns/-/data", "");
            headers.set("X-App-Access-Sig", signature);

            // Create request entity
            String requestBody = "{ \"txnId\": \"your_txn_id\", \"applicant\": { \"externalUserId\": \"31699\", \"uniqueRemitterId\" } }";
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            // Send request
            return restTemplate.exchange(SUMSUB_API_URL, HttpMethod.POST, entity, String.class).getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    private String generateSignature(long timestamp, String method, String uri, String body) throws Exception {
        String dataToSign = timestamp + method + uri + body;
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] rawHmac = mac.doFinal(dataToSign.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(rawHmac);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}