**Инструкция использование библиотека**

**Чтобы пользовать библиотекой, нужно:**
1. Включить библиотку в свой проект/приложение через команда import com.example.weather.Library (класс)
2. Создать новый объект Library c параметром ключа API из Metosource
    Пример: Library forecaster = new Library(apikey);
3. Вызывать методы из cоздаемого объкета для достать данных о погоде

**Список методы из библиотки (Class Library):**

    GetTemperatureByDate(String placeName, LocalDate date)
        --Возвращает температура по дату (только в течении 7 дней - текущий день и 6 следующих дней) 
    
    GetTemperatureByHour(String placeName, LocalDate date, int hour)
        --Возвращает температура по часам (только в течении 24 часов с текушего часа)
    
    GetCloudByDate(String placeName, LocalDate date)
        --Возращает состояние облака по дату (только в течении 7 дней - текущий день и 6 следующих дней) 
    GetCloudByHour(String placeName, LocalDate date, int hour)
        --Возращает состояние облака по дату (только в течении 24 часов с текушего часа)
        
    GetWindByDate(String placeName, LocalDate date)
        --Возращает скорость ветра по дату (только в течении 7 дней - текущий день и 6 следующих дней) 
    
    GetWindByHour(String placeName, LocalDate date, int hour)
        --Возращает скорость ветра по часам (только в течении 24 часов с текушего часа) 


**Пример:**

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

**Результат:**
            
            Test Library
            Daily Temperature : 8.2 Celcius Degree
            Temperature in 10 hours: 17.2 Celcius Degree
            Daily Cloud-cover : 44 %
            Cloud cover in 20 hours: :57 %
            Daily Wind-speed : 2.2 m/s
            Wind speed in 12 hours:2.3 m/s
