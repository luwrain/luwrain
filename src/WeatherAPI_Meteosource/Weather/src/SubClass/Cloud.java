package SubClass;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static SubClass.Temperature.mapper;

public class Cloud extends WeatherObject {
    public Cloud(String apiKey) {
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

        JsonNode cloud = neededData.path("cloud_cover").path("total");
        if (cloud.isMissingNode()) {
            return "No cloud data available";
        }
        return cloud.asText();

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
                JsonNode cloudNode = dayData.path("all_day").path("cloud_cover").path("total");
                if (cloudNode.isMissingNode()) {
                    return "No cloud_cover data available";
                }
                return cloudNode.asText();
            }
        }
        return "No data for the specified date: " + dateString;
    }
}
