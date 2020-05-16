package com.dylantjohnson.mkvuploader.gdrive;

import com.dylantjohnson.json.*;
import java.math.*;
import java.net.*;

public class DeviceAuthorization {
    private String deviceCode;
    private BigInteger expiresIn;
    private BigInteger interval;
    private String userCode;
    private URL verificationUrl;

    public DeviceAuthorization(JsonValue json) {
        var root = json.getObjectValue().get();
        deviceCode = root.getValue(ApiConstants.DEVICE_CODE_KEY).get().getStringValue().get();
        expiresIn = root.getValue(ApiConstants.EXPIRES_IN_KEY).get().getNumberValue().get().toBigInteger();
        interval = root.getValue(ApiConstants.INTERVAL_KEY).get().getNumberValue().get().toBigInteger();
        userCode = root.getValue(ApiConstants.USER_CODE_KEY).get().getStringValue().get();
        
        var verificationUrlString = root.getValue(ApiConstants.VERIFICATION_URL_KEY).get().getStringValue().get();
        try {
            verificationUrl = new URL(verificationUrlString);
        } catch (Exception e) {
            throw new NullPointerException(String.format("Failed to initialize verificationUrl %s: %s", verificationUrlString, e.getMessage()));
        }
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public BigInteger getExpiresInSeconds() {
        return expiresIn;
    }

    public BigInteger getIntervalSeconds() {
        return interval;
    }

    public String getUserCode() {
        return userCode;
    }

    public URL getVerificationUrl() {
        return verificationUrl;
    }
}
