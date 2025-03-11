package com.win.sumsub.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class AppTokenJavaExample {
    // The description of the authorization method is available here: https://docs.sumsub.com/reference/authentication
    private static final String SUMSUB_SECRET_KEY = "hnwGV08m6FrxdHrVptDnFHV0nGcpFFT6"; // Example: Hej2ch71kG2kTd1iIUDZFNsO5C1lh5Gq
    private static final String SUMSUB_APP_TOKEN = "prd:zwdwXLVqVnQWtod6oXbKoq2D.nq9snqfJDjpCx2mJpp3mACKxQEIPhqzY"; // Example: sbx:uY0CgwELmgUAEyl4hNWxLngb.0WSeQeiYny4WEqmAALEAiK2qTC96fBad
    private static final String SUMSUB_TEST_BASE_URL = "https://api.sumsub.com";
    //Please don't forget to change token and secret key values to production ones when switching to production

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        // The description of the flow can be found here: https://docs.sumsub.com/reference/get-started-with-api

        // Such actions are presented below:
        // 1) Creating an applicant
        // 2) Adding a document to the applicant
        // 3) Getting applicant status
        // 4) Getting access token

        String externalUserId = "31699";
        String applicantId = "67c590dafa06707def346e15";
        System.out.println("The applicant (" + externalUserId + ") was successfully created: " + applicantId);

        String accessTokenStr = "zwdwXLVqVnQWtod6oXbKoq2D.nq9snqfJDjpCx2mJpp3mACKxQEIPhqzY";
        System.out.println("Access token (json string): " + accessTokenStr);

        String root = "https://api.sumsub.com";
        String url = "/resources/applicants/67c590dafa06707def346e15/kyt/txns/-/data";
        long ts = Instant.now().getEpochSecond();
        ObjectMapper objectMapper = new ObjectMapper();
        RequestBody requestBody1 = createRequestBody(url);

        Response response = sendPost(root+url, requestBody1);
        System.out.println("Response "+ response.toString());

        if (response.isSuccessful()) {
            String responseBody = response.body().string();
            System.out.println("Response Body: " + responseBody);

            try {
                ObjectMapper objectMapper1 = new ObjectMapper();
                JsonNode jsonNode = objectMapper1.readTree(responseBody);
                if (jsonNode.has("correlationId")) {
                    String correlationId = jsonNode.get("correlationId").asText();
                    System.out.println("Correlation ID: " + correlationId);
                } else {
                    System.out.println("Correlation ID not found in response.");
                }
            } catch (IOException e) {
                System.err.println("Error parsing JSON response: " + e.getMessage());
            }
        } else {
            System.err.println("Request failed with code: " + response.code());
            if(response.body() != null){
                System.err.println("Error Body: " + response.body().string());
            }
        }



}



    private static Response sendPost(String url, RequestBody requestBody) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        long ts = Instant.now().getEpochSecond();

        Request request = new Request.Builder()
                .url( url)
                .header("X-App-Token", SUMSUB_APP_TOKEN)
                .header("X-App-Access-Sig", createSignature(ts, HttpMethod.POST, url, requestBodyToBytes(requestBody)))
                .header("X-App-Access-Ts", String.valueOf(ts))
                .post(requestBody)
                .build();
        System.out.println("Req Headers "+request.toString());
        Response response = new OkHttpClient().newCall(request).execute();

        if (response.code() != 200 && response.code() != 201) {
            // https://docs.sumsub.com/reference/review-api-health
            // If an unsuccessful answer is received, please log the value of the "correlationId" parameter.
            // Then perhaps you should throw the exception. (depends on the logic of your code)
        }
        return response;
    }

//    private static Response sendGet(String url) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
//        long ts = Instant.now().getEpochSecond();
//
//        Request request = new Request.Builder()
//                .url(SUMSUB_TEST_BASE_URL + url)
//                .header("X-App-Token", SUMSUB_APP_TOKEN)
//                .header("X-App-Access-Sig", createSignature(ts, HttpMethod.GET, url, null))
//                .header("X-App-Access-Ts", String.valueOf(ts))
//                .get()
//                .build();
//
//        Response response = new OkHttpClient().newCall(request).execute();
//
//        if (response.code() != 200 && response.code() != 201) {
//            // https://docs.sumsub.com/reference/review-api-health
//            // If an unsuccessful answer is received, please log the value of the "correlationId" parameter.
//            // Then perhaps you should throw the exception. (depends on the logic of your code)
//        }
//        return response;
//    }

    private static String createSignature(long ts, HttpMethod httpMethod, String path, byte[] body) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        hmacSha256.init(new SecretKeySpec(SUMSUB_SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        hmacSha256.update((ts + httpMethod.name() + path).getBytes(StandardCharsets.UTF_8));
        byte[] bytes = body == null ? hmacSha256.doFinal() : hmacSha256.doFinal(body);
        return Hex.encodeHexString(bytes);
    }

    public static byte[] requestBodyToBytes(RequestBody requestBody) throws IOException {
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        return buffer.readByteArray();
    }

    static RequestBody createRequestBody(String url){

        OkHttpClient client = new OkHttpClient();

        JSONObject requestBodyJson = new JSONObject();

        // Dynamic Request Body Creation
        String txnId = UUID.randomUUID().toString();
        OffsetDateTime utcTime = OffsetDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX");
        String formattedUtc = utcTime.format(formatter);

        requestBodyJson.put("txnId", txnId);
        requestBodyJson.put("txnDate", formattedUtc);

        JSONObject info = new JSONObject();
        info.put("direction", "out");
        info.put("amount", 1000);
        info.put("currencyCode", "EUR");
        requestBodyJson.put("info", info);

        JSONObject applicant = new JSONObject();
        JSONObject applicantAddress = new JSONObject();
        applicantAddress.put("country", "ZAF");
        applicantAddress.put("formattedAddress", "Some Address, South Africa");
        applicant.put("address", applicantAddress);
        applicant.put("externalUserId", "31699");
        applicant.put("id", "67c590dafa06707def346e15");
        applicant.put("fullName", "Winjit South Africa Pty Ltd,");


        JSONObject paymentMethodApplicant = new JSONObject();
        paymentMethodApplicant.put("type", "card");
        paymentMethodApplicant.put("accountId", "eg_hash_of_credit_card_number_ZA");
        paymentMethodApplicant.put("issuingCountry", "ZAF");
        applicant.put("paymentMethod", paymentMethodApplicant);

        JSONObject device = new JSONObject();
        JSONObject ipInfo = new JSONObject();
        ipInfo.put("ip", "192.168.1.1");
        ipInfo.put("countryCode3", "ZAF");
        ipInfo.put("city", "Cape Town");
        ipInfo.put("lat", -33.9249);
        ipInfo.put("lon", 18.4241);
        ipInfo.put("riskyAsn", false);
        device.put("ipInfo", ipInfo);

        JSONObject userAgentInfo = new JSONObject();
        userAgentInfo.put("userAgent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
        device.put("userAgentInfo", userAgentInfo);
        device.put("fingerprint","some_fingerprint");
        applicant.put("device",device);

        JSONObject institutionInfoApplicant = new JSONObject();
        institutionInfoApplicant.put("code", "ZABANKCODE");
        institutionInfoApplicant.put("name", "Some Bank");
        applicant.put("institutionInfo", institutionInfoApplicant);
        requestBodyJson.put("applicant", applicant);

        JSONObject counterparty = new JSONObject();
        counterparty.put("fullName", "Some Name");
        counterparty.put("type", "individual");

        JSONObject paymentMethodCounterparty = new JSONObject();
        paymentMethodCounterparty.put("type", "account");
        paymentMethodCounterparty.put("accountId", "eg_hash_of_iban_CH");
        paymentMethodCounterparty.put("issuingCountry", "CHE");
        counterparty.put("paymentMethod", paymentMethodCounterparty);

        JSONObject institutionInfoCounterparty = new JSONObject();
        institutionInfoCounterparty.put("name", "Credit Swiss (Schweiz)");
        institutionInfoCounterparty.put("code", "CRESCHZZXXX");
        counterparty.put("institutionInfo", institutionInfoCounterparty);

        requestBodyJson.put("counterparty", counterparty);

        JSONObject props = new JSONObject();
        props.put("customProperty", "Custom value that can be used in rules");
        props.put("dailyOutLimit", "10000");
        requestBodyJson.put("props", props);

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(requestBodyJson.toString(), mediaType);
        System.out.println("ReqBody "+requestBodyJson.toString());

       return body;
    }

}