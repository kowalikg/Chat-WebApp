import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Gabrysia on 22.01.2017.
 */
public class Room {

    protected String roomName;
    protected CopyOnWriteArrayList<User> userList;

    public Room (String roomName){
        this.roomName = roomName;
        userList = new CopyOnWriteArrayList<>();
    }
    public void pushUser(User user) throws NegativeArraySizeException{
        userList.add(user);
    }
    public void deleteUser(User user) throws IllegalArgumentException{
        if(!userList.remove(user))
            throw new IllegalArgumentException("There is no such user in this channel.");
    }

    public String getRoomName() {
        return roomName;
    }

    public CopyOnWriteArrayList<User> getUserList() {
        return userList;
    }
    public Room containsUser(User user){
        if(userList.contains(user)){
            return this;
        }
        return null;
    }

}
