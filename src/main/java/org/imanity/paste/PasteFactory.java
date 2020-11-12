package org.imanity.paste;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Getter
public class PasteFactory {

    public static PasteFactory create(String serverUrl) {
        return PasteFactory.create(serverUrl, new Gson());
    }

    public static PasteFactory create(String serverUrl, Gson gson) {
        return new PasteFactory(serverUrl, gson);
    }

    private final String serverUrl;
    private final Gson gson;

    private PasteFactory(String serverUrl, Gson gson) {
        this.serverUrl = serverUrl;
        this.gson = gson;
    }

    public String find(String id) throws PasteConnectException {
        HttpURLConnection connection;
        StringBuilder content = new StringBuilder();

        try {
            connection = (HttpURLConnection) this.url(id).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Encoding", "gzip");
            connection.setRequestProperty("Content-Type", "text/plain");
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");

            int response = connection.getResponseCode();

            if (response == 200) {

                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                }

            } else {
                throw new PasteConnectException("Failed to connect into the server! response code: " + response + " " + connection.getResponseMessage());
            }

        } catch (IOException exception) {
            throw new RuntimeException("Exception occurs while attempting to open connection", exception);
        }

        return content.toString();
    }

    public String write(String content) throws PasteConnectException {
        HttpURLConnection connection;
        StringBuilder key = new StringBuilder();

        try {
            connection = (HttpURLConnection) this.url("post").openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Encoding", "gzip");
            connection.setRequestProperty("Content-Type", "text/plain");
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");

            try(OutputStream os = connection.getOutputStream()) {
                byte[] input = content.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int response = connection.getResponseCode();

            if (response == 201) {

                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        key.append(line).append("\n");
                    }
                }

            } else {
                throw new PasteConnectException("Failed to connect into the server! response code: " + response);
            }

        } catch (IOException exception) {
            throw new RuntimeException("Exception occurs while attempting to open connection", exception);
        }

        JsonObject jsonObject = this.gson.fromJson(key.toString(), JsonObject.class);
        if (jsonObject.has("key")) {
            return jsonObject.get("key").getAsString();
        }

        return "unknown";
    }

    private URL url(String address) {
        String toSearch;

        if (!this.serverUrl.endsWith("/") && !address.startsWith("/")) {
            toSearch = this.serverUrl + "/" + address;
        } else if (this.serverUrl.endsWith("/") && address.startsWith("/")) {
            toSearch = this.serverUrl + address.substring(1);
        } else {
            toSearch = this.serverUrl + address;
        }

        try {
            return new URL(toSearch);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("The address " + toSearch + " is not a valid address!", e);
        }
    }

}
