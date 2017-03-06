import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Gabrysia on 26.01.2017.
 */
public class JSONGenerator {
    private JSONObject jsonObject;

    public JSONObject generateUserList(CopyOnWriteArrayList<User> userList ) throws JSONException {
        CopyOnWriteArrayList<String> userNameList = new  CopyOnWriteArrayList<>();
        for (User u: userList ) {
            userNameList.add(u.getUserName());
        }
        jsonObject = new JSONObject().put("users", userNameList);
        return jsonObject;
    }

    public JSONObject generateRoomList( CopyOnWriteArrayList<Room> roomList) throws JSONException {
        CopyOnWriteArrayList<String> roomNameList = new  CopyOnWriteArrayList<>();
        for (Room r: roomList) {
            roomNameList.add(r.getRoomName());

        }
        jsonObject = new JSONObject().put("rooms", roomNameList);
        return jsonObject;
    }

}
