import java.time.LocalDate;

public class App {
    public static void main(String[] args) {
        String apiKey = "gyqo0sonhmcyjhlyh2mejjkypn14e38jappjg19h"; // APIKEY
        Library weatherLibrary = new Library(apiKey);

        System.out.println("Test Library");
        try {
            // Temperature
            String temperature = weatherLibrary.GetTemperatureByDate("tomsk", LocalDate.of(2024, 6, 5));
            System.out.println("Daily Temperature : " + temperature + " Celcius Degree");

            String hourlyTemperature = weatherLibrary.GetTemperatureByHour("tomsk", LocalDate.now(), 18);
            System.out.println("Temperature in 10 hours: " + hourlyTemperature + " Celcius Degree");

            // Cloud
            String cloud = weatherLibrary.GetCloudByDate("Tomsk", LocalDate.of(2024, 6, 2));
            System.out.println("Daily Cloud-cover : " + cloud + " %");

            String hourlyCloud = weatherLibrary.GetCloudByHour("Tomsk", LocalDate.now(), 20);
            System.out.println("Cloud cover in 20 hours: :" + hourlyCloud + " %");

            // Wind
            String wind = weatherLibrary.GetWindByDate("Tomsk", LocalDate.of(2024, 6, 2));
            System.out.println("Daily Wind-speed : " + wind + " m/s");

            String hourlyWind = weatherLibrary.GetWindByHour("Tomsk", LocalDate.now(), 12);
            System.out.println("Wind speed in 12 hours:" + hourlyWind + " m/s");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
