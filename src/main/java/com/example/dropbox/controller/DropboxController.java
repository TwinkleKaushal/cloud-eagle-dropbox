package com.example.dropbox.controller;

import com.example.dropbox.service.DropboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth")
public class DropboxController {

    @Autowired
    private DropboxService dropboxService;

    /**
     * Generates the Dropbox authorization URL for the client to visit.
     *
     * @return A message containing the URL to visit for Dropbox authorization.
     */
    @GetMapping("/authorize")
    public String authorize() {
        return "Visit this URL to authorize:\n" + dropboxService.getAuthorizeUrl();
    }

    /**
     * Handles the OAuth2 callback from Dropbox after the user authorizes the application.
     *
     * @param code The authorization code received from Dropbox.
     * @return The access token response as a JSON string.
     * @throws Exception if token exchange fails.
     */
    @GetMapping("/callback")
    public String callback(@RequestParam String code) throws Exception {
        String tokenResponse = dropboxService.exchangeCodeForToken(code);
        return "Access Token Response:\n" + tokenResponse;
    }

    /**
     * Retrieves team information from Dropbox.
     *
     * @param accessToken The Bearer access token for Dropbox API authorization.
     * @return JSON string containing Dropbox team information.
     * @throws Exception if the API request fails.
     */
    @GetMapping("/team-info")
    public String getTeamInfo(@RequestHeader("Authorization") String accessToken) throws Exception {
        return dropboxService.getTeamInfo(accessToken.replace("Bearer ", ""));
    }

    /**
     * Retrieves plan and license details for the Dropbox team.
     *
     * @param accessToken The Bearer access token for Dropbox API authorization.
     * @return A formatted string containing plan type, team member limit, and license type.
     * @throws Exception if the API request fails.
     */
    @GetMapping("/plan-license")
    public String getPlanAndLicense(@RequestHeader("Authorization") String accessToken) throws Exception {
        return dropboxService.getPlanAndLicense(accessToken.replace("Bearer ", ""));
    }

    /**
     * Retrieves the list of all users in the Dropbox team (up to 100 by default).
     *
     * @param accessToken The Bearer access token for Dropbox API authorization.
     * @return JSON string containing team members.
     * @throws Exception if the API request fails.
     */
    @GetMapping("/users")
    public String getAllUsers(@RequestHeader("Authorization") String accessToken) throws Exception {
        return dropboxService.getAllUsers(accessToken.replace("Bearer ", ""));
    }

    /**
     * Retrieves the latest sign-in events for the Dropbox team.
     *
     * @param accessToken The Bearer access token for Dropbox API authorization.
     * @return JSON string containing sign-in events.
     * @throws Exception if the API request fails.
     */
    @GetMapping("/sign-in-events")
    public String getSignInEvents(@RequestHeader("Authorization") String accessToken) throws Exception {
        return dropboxService.getSignInEvents(accessToken.replace("Bearer ", ""));
    }

}