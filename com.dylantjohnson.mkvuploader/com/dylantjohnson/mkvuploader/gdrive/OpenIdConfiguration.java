package com.dylantjohnson.mkvuploader.gdrive;

import com.dylantjohnson.json.*;
import java.net.*;

public class OpenIdConfiguration {
    private URL deviceAuthEndpoint;

    public OpenIdConfiguration(JsonValue json) {
        var config = json.getObjectValue().get();
        
        var deviceAuthEndpointString = config.getValue(ApiConstants.DEVICE_AUTH_ENDPOINT_KEY).get().getStringValue().get();
        try {
            deviceAuthEndpoint = new URL(deviceAuthEndpointString);
        } catch (Exception e) {
            throw new NullPointerException(String.format("Failed to initialize deviceAuthEndpoint %s: %s", deviceAuthEndpointString, e.getMessage()));
        }
    }

    public URL getDeviceAuthEndpoint() {
        return deviceAuthEndpoint;
    }
}
