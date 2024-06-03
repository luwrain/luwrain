import SubClass.Cloud;
import SubClass.Temperature;
import SubClass.WeatherObject;
import SubClass.Wind;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDate;

public class Library {
    private String apiKey;

    public Library(String key) {
        this.apiKey = key;
    }

    private WeatherObject forecaster;

    private String findPlaceId(WeatherObject forecaster, String placeName) throws IOException, InterruptedException {
        String url = "https://www.meteosource.com/api/v1/free/find_places";
        String response = forecaster.getResponse(url, "text", placeName);
        JsonNode root = new ObjectMapper().readTree(response);
        if (root.isArray() && root.isEmpty()) {
            return "unknown";
        }
        return root.get(0).path("place_id").asText();
    }

    public String GetTemperatureByDate(String placeName, LocalDate date) {
        try {
            this.forecaster = new Temperature(apiKey);
            String placeId = findPlaceId(forecaster, placeName);
            if (placeId == "unknown") {
                return "Can not find place";
            }
            String response = forecaster.getResponse("https://www.meteosource.com/api/v1/free/point", "place_id",
                    placeId + "&sections=daily");
            return forecaster.parseDataFromResponseByDate(response, date);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error fetching data";
        }
    }

    public String GetTemperatureByHour(String placeName, LocalDate date, int hour) {
        try {
            this.forecaster = new Temperature(apiKey);
            String placeId = findPlaceId(forecaster, placeName);
            if (placeId == "unknown") {
                return "Can not find place";
            }
            String response = forecaster.getResponse("https://www.meteosource.com/api/v1/free/point", "place_id",
                    placeId + "&sections=hourly");
            return forecaster.parseDataFromResponseByHour(response, date, hour);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error fetching data";
        }
    }

    public String GetCloudByHour(String placeName, LocalDate date, int hour) {
        try {
            this.forecaster = new Cloud(apiKey);
            String placeId = findPlaceId(forecaster, placeName);
            if (placeId == "unknown") {
                return "Can not find place";
            }
            String response = forecaster.getResponse("https://www.meteosource.com/api/v1/free/point", "place_id",
                    placeId + "&sections=hourly");
            return forecaster.parseDataFromResponseByHour(response, date, hour);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error fetching data";
        }
    }

    public String GetCloudByDate(String placeName, LocalDate date) {
        try {
            this.forecaster = new Cloud(apiKey);
            String placeId = findPlaceId(forecaster, placeName);
            if (placeId == "unknown") {
                return "Can not find place";
            }
            String response = forecaster.getResponse("https://www.meteosource.com/api/v1/free/point", "place_id",
                    placeId + "&sections=daily");
            return forecaster.parseDataFromResponseByDate(response, date);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error fetching data";
        }
    }

    public String GetWindByHour(String placeName, LocalDate date, int hour) {
        try {
            this.forecaster = new Wind(apiKey);
            String placeId = findPlaceId(forecaster, placeName);
            if (placeId == "unknown") {
                return "Can not find place";
            }
            String response = forecaster.getResponse("https://www.meteosource.com/api/v1/free/point", "place_id",
                    placeId + "&sections=hourly");
            return forecaster.parseDataFromResponseByHour(response, date, hour);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error fetching data";
        }
    }

    public String GetWindByDate(String placeName, LocalDate date) {
        try {
            this.forecaster = new Wind(apiKey);
            String placeId = findPlaceId(forecaster, placeName);
            if (placeId == "unknown") {
                return "Can not find place";
            }
            String response = forecaster.getResponse("https://www.meteosource.com/api/v1/free/point", "place_id",
                    placeId + "&sections=daily");
            return forecaster.parseDataFromResponseByDate(response, date);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error fetching data";
        }
    }
    // Similar methods for Cloud and Wind
}
