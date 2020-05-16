package com.dylantjohnson.mkvuploader;

import com.dylantjohnson.json.*;
import com.dylantjohnson.mkvuploader.gdrive.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public class Runner {
    public static void main(String[] args) throws Exception {
        var appDirectory = getLocalAppDirectory();
        var apiCredentials = getApiCredentials(appDirectory);
        var token = getOauthToken(appDirectory, apiCredentials);
        System.out.println(token.getAccessToken());
        if (token.getRefreshToken().isPresent()) {
            var refreshTokenFile = new File(appDirectory, "refresh_token");
            Files.writeString(refreshTokenFile.toPath(), token.getRefreshToken().get());
        }
    }

    private static File getLocalAppDirectory() throws Exception {
        // LOCALAPPDATA is Windows-specific. Can change later.
        var appDir = new File(System.getenv("LOCALAPPDATA"), "com.dylantjohnson.mkvuploader");
        if (!appDir.exists() && !appDir.mkdir()) {
            throw new Exception(String.format("Failed to create application directory: %s", appDir.getAbsolutePath()));
        }
        return appDir;
    }

    private static ApiCredentials getApiCredentials(File appDir) throws Exception {
        var clientIdFile = new File(appDir, "client_id.json");
        if (!clientIdFile.exists()) {
            throw new Exception(String.format("API credentials not present: %s", clientIdFile.getAbsolutePath()));
        }
        try (var clientIdStream = new FileInputStream(clientIdFile)) {
            return new ApiCredentials(Parser.parse(clientIdStream));
        }
    }

    private static OauthToken getOauthToken(File appDir, ApiCredentials creds) throws Exception {
        var refreshTokenFile = new File(appDir, "refresh_token");
        var refreshToken = refreshTokenFile.exists() ? Files.readString(refreshTokenFile.toPath()) : null;
        if (refreshToken != null) {
            return getOauthTokenRefresh(refreshToken, creds);
        } else {
            return getOauthTokenInteractive(creds, ApiConstants.SCOPE_DRIVE_FILE);
        }
    }

    private static OauthToken getOauthTokenInteractive(ApiCredentials creds, String...scopes) throws Exception {
        var config = new OpenIdConfiguration(RestClient.get(new URL(ApiConstants.OPENID_CONFIGURATION_URL)));
        var deviceAuthBody = Map.of(ApiConstants.CLIENT_ID_KEY, creds.getClientId(), ApiConstants.SCOPE_KEY, String.join(" ", scopes));
        var device = new DeviceAuthorization(RestClient.post(config.getDeviceAuthEndpoint(), deviceAuthBody, Optional.empty()));
        System.out.println(String.format("Navigate To %s. Your device code is %s", device.getVerificationUrl(), device.getUserCode()));
        var body = Map.of(ApiConstants.CLIENT_ID_KEY, creds.getClientId(), ApiConstants.CLIENT_SECRET_KEY, creds.getClientSecret(), ApiConstants.DEVICE_CODE_KEY, device.getDeviceCode(), ApiConstants.GRANT_TYPE_KEY, ApiConstants.GRANT_TYPE_INTERACTIVE_VALUE);
        var timeout = System.currentTimeMillis() + (device.getExpiresInSeconds().longValue() * 1000);
        while (System.currentTimeMillis() < timeout) {
            try {
                return new OauthToken(RestClient.post(new URL(ApiConstants.AUTHORIZATION_POLL_URL), body, Optional.empty()));
            } catch (Exception e) {
                Thread.sleep(device.getIntervalSeconds().longValue() * 1000);
            }
        }
        throw new Exception("Timed out waiting for OAuth");
    }

    private static OauthToken getOauthTokenRefresh(String refresh, ApiCredentials creds) throws Exception {
        var body = Map.of(ApiConstants.CLIENT_ID_KEY, creds.getClientId(), ApiConstants.CLIENT_SECRET_KEY, creds.getClientSecret(), ApiConstants.REFRESH_TOKEN_KEY, refresh, ApiConstants.GRANT_TYPE_KEY, ApiConstants.GRANT_TYPE_REFRESH_VALUE);
        try {
            return new OauthToken(RestClient.post(new URL(ApiConstants.AUTHORIZATION_POLL_URL), body, Optional.empty()));
        } catch (Exception e) {
            return getOauthTokenInteractive(creds, ApiConstants.SCOPE_DRIVE_FILE);
        }
    }
}
