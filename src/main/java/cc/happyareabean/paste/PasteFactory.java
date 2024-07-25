package cc.happyareabean.paste;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

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

    public String find(String id) throws PasteException {
        return this.find(id, null);
    }

    public String find(String id, @Nullable String contentType) throws PasteException {
        HttpURLConnection connection;
        StringBuilder content = new StringBuilder();

        try {
            connection = (HttpURLConnection) this.url(id).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);

            defaultRequestProperty(connection);

            if (contentType != null)
                setContentType(connection, contentType);

            int response = connection.getResponseCode();

            if (response == 200) {

                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                }

            } else {
                throw new PasteException("Failed to connect into the server! response code: " + response + " " + connection.getResponseMessage());
            }

        } catch (IOException exception) {
            throw new PasteException("Exception occurs while attempting to open connection", exception);
        }

        return content.toString();
    }

    public String write(String content) throws PasteException {
        return this.write(content, null);
    }

    public String write(String content, String contentType) throws PasteException {
        HttpURLConnection connection;
        StringBuilder key = new StringBuilder();

        try {
            connection = (HttpURLConnection) this.url("post").openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            defaultRequestProperty(connection);

            if (contentType != null)
                setContentType(connection, contentType);

            try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(connection.getOutputStream())) {
                gzipOutputStream.write(content.getBytes());
            } catch (IOException exception) {
                removeContentEncoding(connection);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = content.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                throw new PasteException("Exception occurs while attempting to gzip content", exception);
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
                throw new PasteException("Failed to connect into the server! response code: " + response);
            }

        } catch (IOException exception) {
            throw new PasteException("Exception occurs while attempting to open connection", exception);
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

    private void defaultRequestProperty(HttpURLConnection connection) {
        connection.addRequestProperty("Content-Encoding", "gzip");
        connection.addRequestProperty("Content-Type", "text/pain");
        connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
    }

    private void removeContentEncoding(HttpURLConnection connection) {
        connection.setRequestProperty("Content-Encoding", "");
    }

    private void setContentType(HttpURLConnection connection, String contentType) {
        connection.setRequestProperty("Content-Type", contentType);
    }

}
