import java.util.ArrayList;

/**
 * Created by Gabrysia on 22.01.2017.
 */
public class Room {

    private int roomID;
    private String roomName;
    private ArrayList<User> userList;

    public Room (int roomID, String roomName){
        this.roomID = roomID;
        this.roomName = roomName;
        userList = new ArrayList<>();
    }
    public void pushUser(User user) throws NegativeArraySizeException{
        if(!userList.add(user));
            throw new NegativeArraySizeException("Failed to push user.");
    }
    public void deleteUser(User user) throws IllegalArgumentException{
        if(!userList.remove(user))
            throw new IllegalArgumentException("There is no such user in this channel.");
    }

    public int getRoomID() {
        return roomID;
    }

    public String getRoomName() {
        return roomName;
    }

    public ArrayList getUserList() {
        return userList;
    }

}
