package com.example.dropbox.service;

public interface DropboxService {

    public String getAuthorizeUrl() ;

    public String exchangeCodeForToken(String code) throws Exception;

    public String getTeamInfo(String accessToken) throws Exception ;

    public String getPlanAndLicense(String accessToken) throws Exception;

    public String getAllUsers(String accessToken) throws Exception;

    public String getSignInEvents(String accessToken) throws Exception;
}
