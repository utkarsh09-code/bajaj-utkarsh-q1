package com.bfhl;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class BfhlApplication {

    private final RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {
        SpringApplication.run(BfhlApplication.class, args);
    }

    @PostConstruct
    public void runOnStartup() {
        try {
            // The SQL query to get the highest salary employee information
            String finalQuery =
                "SELECT " +
                "P.AMOUNT AS SALARY, " +
                "CONCAT(E.FIRST_NAME, ' ', E.LAST_NAME) AS NAME, " +
                "TIMESTAMPDIFF(YEAR, E.DOB, CURDATE()) AS AGE, " +
                "D.DEPARTMENT_NAME " +
                "FROM PAYMENTS P " +
                "JOIN EMPLOYEE E ON P.EMP_ID = E.EMP_ID " +
                "JOIN DEPARTMENT D ON E.DEPARTMENT = D.DEPARTMENT_ID " +
                "WHERE DAY(P.PAYMENT_TIME) != 1 " +
                "AND P.AMOUNT = (" +
                    "SELECT MAX(AMOUNT) " +
                    "FROM PAYMENTS " +
                    "WHERE DAY(P.PAYMENT_TIME) != 1" +
                ");";

            // Your access token (JWT) for the Authorization header
            String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJyZWdObyI6IlJFRzEyMzQ3IiwibmFtZSI6IkpvaG4gRG9lIiwiZW1haWwiOiJqb2huQGV4YW1wbGUuY29tIiwic3ViIjoid2ViaG9vay11c2VyIiwiaWF0IjoxNzQ2OTYxNjAxLCJleHAiOjE3NDY5NjI1MDF9.LsYVeisBDYqRBshPbcjGqBvwxXINWn1_mWYwfBKQ-Cg";

            // Webhook URL for the API call
            String webhookUrl = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

            // Prepare the body for the request
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("finalQuery", finalQuery);

            // Set the headers for the request (Authorization and Content-Type)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);  // Set JWT in the Authorization header

            // Wrap the body and headers into the HttpEntity
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            // Execute the POST request to the API
            ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, request, String.class);

            // Handle the response
            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("SQL query submitted successfully.");
                System.out.println("Response: " + response.getBody());
            } else {
                System.err.println("Submission failed: " + response.getStatusCode());
                System.err.println("Response: " + response.getBody());
            }

        } catch (Exception e) {
            System.err.println("Exception occurred:");
            e.printStackTrace();
        }
    }
}
