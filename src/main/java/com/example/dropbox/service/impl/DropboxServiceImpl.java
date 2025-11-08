package com.example.dropbox.service.impl;

import com.example.dropbox.service.DropboxService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DropboxServiceImpl implements DropboxService {

    @Value("${dropbox.client-id}")
    private String clientId;

    @Value("${dropbox.client-secret}")
    private String clientSecret;

    @Value("${dropbox.redirect-uri}")
    private String redirectUri;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final MediaType JSON = MediaType.get("application/json");

    // Generates the Dropbox OAuth2 authorization URL
    @Override
    public String getAuthorizeUrl() {
        return String.format(
                "https://www.dropbox.com/oauth2/authorize?client_id=%s&response_type=code&redirect_uri=%s",
                clientId, redirectUri
        );
    }

    // Exchanges authorization code for access token
    @Override
    public String exchangeCodeForToken(String code) throws IOException {
        RequestBody body = new FormBody.Builder()
                .add("code", code)
                .add("grant_type", "authorization_code")
                .add("redirect_uri", redirectUri)
                .build();

        Request request = new Request.Builder()
                .url("https://api.dropboxapi.com/oauth2/token")
                .header("Authorization", Credentials.basic(clientId, clientSecret))
                .post(body)
                .build();

        return executeRequest(request, "Token request failed");
    }

    // Retrieves Dropbox team information
    @Override
    public String getTeamInfo(String accessToken) throws IOException {
        Request request = buildPostRequest(
                "https://api.dropboxapi.com/2/team/get_info",
                accessToken,
                "null"
        );

        return executeRequest(request, "Failed to get team info");
    }

    // Retrieves plan and license details from Dropbox team info
    @Override
    public String getPlanAndLicense(String accessToken) throws IOException {
        String response = getTeamInfo(accessToken);

        JsonNode root = objectMapper.readTree(response);

        String teamName = root.path("name").path("display_name").asText("N/A");

        int teamMemberLimit = root.path("num_licensed_users").asInt(0);
        int usedLicenses = root.path("num_used_licenses").asInt(0);

        String planType = "Business";
        String licenseType = "Team";

        return String.format("Team Name: %s, Plan Type: %s, Team Member Limit: %d, Used Licenses: %d, License Type: %s",
                teamName, planType, teamMemberLimit, usedLicenses, licenseType);
    }


    // Retrieves all team members (up to 100 by default)
    @Override
    public String getAllUsers(String accessToken) throws IOException {
        Request request = buildPostRequest(
                "https://api.dropboxapi.com/2/team/members/list",
                accessToken,
                "{\"limit\":100}"
        );

        return executeRequest(request, "Failed to get team members");
    }

    // Retrieves sign-in events for the team
    @Override
    public String getSignInEvents(String accessToken) throws IOException {
        Request request = buildPostRequest(
                "https://api.dropboxapi.com/2/team_log/get_events",
                accessToken,
                "{\"limit\":100}"
        );

        return executeRequest(request, "Failed to get sign-in events");
    }

    // Helper method to execute HTTP requests and handle errors consistently
    private String executeRequest(Request request, String errorMessage) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                throw new RuntimeException(errorMessage + ": " + response.code() + " " + responseBody);
            }
            return responseBody;
        }
    }

    // Helper method to build a POST request with JSON body
    private Request buildPostRequest(String url, String accessToken, String jsonBody) {
        return new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(jsonBody, JSON))
                .build();
    }
}
