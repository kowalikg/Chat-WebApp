import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Gabrysia on 26.01.2017.
 */
public class ChatBot extends Room {

    public ChatBot() {
        super("ChatBot");
    }

    public String generateAnswer(String message) throws IOException, JSONException {
        message = message.toLowerCase();

        switch (message){
            case "time":
                return "Now is " + String.valueOf( new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) );
            case "day":
                return "Today is " + String.valueOf( new SimpleDateFormat("EEEE", Locale.US).format(new Date()));
            case "weather":
                return "The weather in " + new Weather().getWeather();
        }
        return "To get time write 'time'," +
                " to get day of the week write 'day'," +
                " to get weather in Cracow write 'weather'.";
    }


}
