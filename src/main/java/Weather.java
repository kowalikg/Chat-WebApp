import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Gabrysia on 26.01.2017.
 */
public class Weather {

    private String description;
    private String time;
    private Double temperature;
    private String weatherDescription;
    public Weather() throws IOException, JSONException {
        generateWeather();

    }
    private void generateWeather() throws IOException, JSONException {
        String url = "http://api.openweathermap.org/data/2.5/weather?q=Cracow&APPID=";
        Downloader downloader = new Downloader(url);
        downloader.download();
        String jsonResult = downloader.getJsonResult();

        if (jsonResult.equals("error")){
            weatherDescription = "No weather available.";
            return;

        }
        JSONObject jsonObject = new JSONObject(jsonResult);

         description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
         temperature = Double.parseDouble(jsonObject.getJSONObject("main").getString("temp")) - 273.15;
         time = String.valueOf(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
         weatherDescription = "The weather in " + "Cracow: " + description + ", temperature: " + temperature + " C, at: " + time;

    }
    public String getWeather(){
        return weatherDescription;
    }
}
