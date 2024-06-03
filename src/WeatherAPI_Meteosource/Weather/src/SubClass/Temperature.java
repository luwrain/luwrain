package SubClass;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Temperature extends WeatherObject {
    public static final ObjectMapper mapper = new ObjectMapper();

    public Temperature(String apiKey) {
        super(apiKey);
    }

    @Override
    public String parseDataFromResponseByHour(String response, LocalDate date, int nexthours) throws IOException {
        JsonNode root = mapper.readTree(response);
        JsonNode hourlyData = root.path("hourly").path("data");

        if (hourlyData.isMissingNode()) {
            return "No hourly field in data";
        }

        JsonNode neededData = hourlyData.get(nexthours - 1);
        if (neededData.isMissingNode())
            return "No data for the specified in " + nexthours + "hours";

        JsonNode temperature = neededData.path("temperature");
        if (temperature.isMissingNode()) {
            return "No temperature data available";
        }
        return temperature.asText();

    }

    @Override
    public String parseDataFromResponseByDate(String response, LocalDate date) throws IOException {
        JsonNode root = mapper.readTree(response);
        JsonNode dailyData = root.path("daily").path("data");

        if (dailyData.isMissingNode()) {
            return "No daily field in data";
        }

        String dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE);

        for (JsonNode dayData : dailyData) {
            String dataDate = dayData.path("day").asText();
            if (dataDate.equals(dateString)) {
                double temperature = dayData.path("all_day").path("temperature").asDouble();
                return "" + temperature;

            }
        }

        return "No data for the specified date: " + dateString;
    }
}
