import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Main {
    public static final String REMOTE_SERVICE_URI =
            "https://api.nasa.gov/planetary/apod?api_key=KzAkSPnunf5FJiRQtsbYcHNcL3VdYuS6dE9f8lpS";

    public static void main(String[] args) {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();

        HttpGet request = new HttpGet(REMOTE_SERVICE_URI);

        try {
            CloseableHttpResponse response = httpClient.execute(request);

            String body = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            Entity entitie = mapper.readValue(body, Entity.class);
            openFile(saveFile(entitie.getUrl()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String saveFile(String url) {
        String[] nameFile = url.split("/");

        try (BufferedInputStream inputStream = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fileOS = new FileOutputStream(nameFile[nameFile.length - 1])) {
            byte data[] = new byte[1024];
            int byteContent;
            while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                fileOS.write(data, 0, byteContent);
            }
            return nameFile[nameFile.length - 1];
        } catch (IOException e) {
            return null;
        }
    }

    private static void openFile(String path) {
        if (!path.equals("") && path != null) {
            Desktop desktop = null;
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
            }
            try {
                desktop.open(new File(path));
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
