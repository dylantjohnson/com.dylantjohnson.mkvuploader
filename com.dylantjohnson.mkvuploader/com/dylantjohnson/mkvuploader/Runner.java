package com.dylantjohnson.mkvuploader;

import com.dylantjohnson.json.*;
import com.dylantjohnson.mkvuploader.gdrive.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Runner {
    public static void main(String[] args) throws Exception {
        var apiCredentials = getApiCredentials();
        var openIdConfig = new OpenIdConfiguration(RestClient.get(new URL(ApiConstants.OPENID_CONFIGURATION_URL)));
        authorizeDevice(openIdConfig, apiCredentials.getClientId(), ApiConstants.SCOPE);
        // WIP
    }

    private static File getLocalAppDirectory() throws Exception {
        // LOCALAPPDATA is Windows-specific. Can change later.
        var appDir = new File(System.getenv("LOCALAPPDATA"), "com.dylantjohnson.mkvuploader");
        if (!appDir.exists() && !appDir.mkdir()) {
            throw new Exception(String.format("Failed to create application directory: %s", appDir.getAbsolutePath()));
        }
        return appDir;
    }

    private static ApiCredentials getApiCredentials() throws Exception {
        var clientIdFile = new File(getLocalAppDirectory(), "client_id.json");
        if (!clientIdFile.exists()) {
            throw new Exception(String.format("API credentials not present: %s", clientIdFile.getAbsolutePath()));
        }
        try (var clientIdStream = new FileInputStream(clientIdFile)) {
            return new ApiCredentials(Parser.parse(clientIdStream));
        }
    }

    private static void authorizeDevice(OpenIdConfiguration config, String clientId, String scope) throws Exception {
        var body = Map.of(ApiConstants.CLIENT_ID_KEY, clientId, ApiConstants.SCOPE_KEY, scope);
        System.out.println(RestClient.post(config.getDeviceAuthEndpoint(), body));
    }
}
