package com.dylantjohnson.mkvuploader.gdrive;

import com.dylantjohnson.json.*;

public class ApiCredentials {
    private String clientId;
    private String clientSecret;

    public ApiCredentials(JsonValue json) {
        var installed = json.getObjectValue().get().getValue(ApiConstants.INSTALLED_KEY).get().getObjectValue().get();
        clientId = installed.getValue(ApiConstants.CLIENT_ID_KEY).get().getStringValue().get();
        clientSecret = installed.getValue(ApiConstants.CLIENT_SECRET_KEY).get().getStringValue().get();
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
