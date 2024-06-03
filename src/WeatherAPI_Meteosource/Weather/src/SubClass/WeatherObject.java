package SubClass;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

public abstract class WeatherObject {
    protected final String apiKey;
    protected static final HttpClient client = HttpClient.newHttpClient();

    public WeatherObject(String apiKey) {
        this.apiKey = apiKey;
    }

    protected HttpRequest buildRequest(String url, String queryParameter, String value) {

        String fullUrl = url + "?" + queryParameter + "=" + value + "&key=" + this.apiKey;
        // System.out.println("Request URL: " + fullUrl); // print URL to test
        return HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .GET()
                .build();
    }

    public String getResponse(String url, String queryParameter, String value)
            throws IOException, InterruptedException {
        HttpRequest request = buildRequest(url, queryParameter, value);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Failed to get data: " + response.body());
        }

        return response.body();
    }

    public abstract String parseDataFromResponseByHour(String response, LocalDate date, int hour) throws IOException;

    public abstract String parseDataFromResponseByDate(String response, LocalDate date) throws IOException;
}
