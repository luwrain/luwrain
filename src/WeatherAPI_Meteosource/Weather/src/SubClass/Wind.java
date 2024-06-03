package SubClass;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static SubClass.Temperature.mapper;

public class Wind extends WeatherObject {
    public Wind(String apiKey) {
        super(apiKey);
    }

    // @Override
    // public String parseDataFromResponseByHour(String response, LocalDate date,
    // int hour) throws IOException {
    // JsonNode root = mapper.readTree(response);
    // JsonNode hourlyData = root.path("hourly").path("data");

    // if (hourlyData.isMissingNode()) {
    // return "No hourly field in data";
    // }

    // String dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    // String hourString = String.format("%sT%02d:00:00", dateString, hour);

    // for (JsonNode hourData : hourlyData) {
    // String dataDate = hourData.path("date").asText();
    // if (dataDate.equals(hourString)) {
    // JsonNode windNode = hourData.path("wind").path("speed");
    // if (windNode.isMissingNode()) {
    // return "No wind speed data available";
    // }
    // return windNode.asText();
    // }
    // }
    // return "No data for the specified hour: " + hour;
    // }

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

        JsonNode wind = neededData.path("wind").path("speed");
        if (wind.isMissingNode()) {
            return "No wind data available";
        }
        return wind.asText();

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
                JsonNode windNode = dayData.path("all_day").path("wind").path("speed");
                if (windNode.isMissingNode()) {
                    return "No wind speed data available";
                }
                return windNode.asText();
            }
        }
        return "No data for the specified date: " + dateString;
    }
}
