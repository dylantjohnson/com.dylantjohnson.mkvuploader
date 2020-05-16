package com.dylantjohnson.mkvuploader.gdrive;

import com.dylantjohnson.json.*;
import java.math.*;
import java.util.*;

public class OauthToken {
    private String accessToken;
    private BigInteger expiresIn;
    private String refreshToken = null;
    private List<String> scopes;
    private String tokenType;

    public OauthToken(JsonValue json) {
        var root = json.getObjectValue().get();
        accessToken = root.getValue(ApiConstants.ACCESS_TOKEN_KEY).get().getStringValue().get();
        expiresIn = root.getValue(ApiConstants.EXPIRES_IN_KEY).get().getNumberValue().get().toBigInteger();
        scopes = List.of(root.getValue(ApiConstants.SCOPE_KEY).get().getStringValue().get().split(" "));
        tokenType = root.getValue(ApiConstants.TOKEN_TYPE_KEY).get().getStringValue().get();

        var refreshTokenOptional = root.getValue(ApiConstants.REFRESH_TOKEN_KEY);
        if (refreshTokenOptional.isPresent()) {
            refreshToken = refreshTokenOptional.get().getStringValue().get();
        }
    }

    public String getAccessToken() {
        return accessToken;
    }

    public BigInteger getExpiresInSeconds() {
        return expiresIn;
    }

    public Optional<String> getRefreshToken() {
        return Optional.ofNullable(refreshToken);
    }

    public List<String> getScopes() {
        return scopes;
    }

    public String getTokenType() {
        return tokenType;
    }
}
