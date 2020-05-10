package com.dylantjohnson.mkvuploader;

import com.dylantjohnson.json.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class RestClient {
    public static JsonValue get(URL url) throws IOException, ParseException {
        try (var urlStream = url.openStream()) {
            return Parser.parse(urlStream);
        }
    }

    public static JsonValue post(URL url, Map<String, String> body) throws IOException, ParseException {
        var request = (HttpURLConnection) url.openConnection();
        request.setRequestMethod("POST");
        request.setDoOutput(true);
        var encodedBody = new StringJoiner("&");
        for (var entry : body.entrySet()) {
            encodedBody.add(String.format("%s=%s", URLEncoder.encode(entry.getKey(), "UTF-8"), URLEncoder.encode(entry.getValue(), "UTF-8")));
        }
        var encodedBodyBytes = encodedBody.toString().getBytes();
        request.setFixedLengthStreamingMode(encodedBodyBytes.length);
        request.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        request.connect();
        try (var bodyStream = new ByteArrayInputStream(encodedBodyBytes); var outputStream = request.getOutputStream()) {
            bodyStream.transferTo(outputStream);
        }
        try (var inputStream = request.getInputStream()) {
            return Parser.parse(inputStream);
        }
    }
}
