import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Gabrysia on 26.01.2017.
 */
public class JSONGenerator {
    private JSONObject jsonObject;

    public JSONObject generateUserList(ArrayList<User> userList ) throws JSONException {
        ArrayList<String> userNameList = new ArrayList<>();
        for (User u: userList ) {
            userNameList.add(u.getUserName());
        }
        jsonObject = new JSONObject().put("users", userNameList);
        return jsonObject;
    }

    public JSONObject generateRoomList(ArrayList<Room> roomList) throws JSONException {
        ArrayList<String> roomNameList = new ArrayList<>();
        for (Room r: roomList) {
            roomNameList.add(r.getRoomName());

        }
        jsonObject = new JSONObject().put("rooms", roomNameList);
        return jsonObject;
    }

}
