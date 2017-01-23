import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;

/**
 * Created by Gabrysia on 22.01.2017.
 */
public class ChatContainer {

    /*
    Ta klasa zajmuje się wszystkim co dotyczy agregacji:
    - użytkowników w czacie
    - kanałów w czacie
    - użytkowników w kanale
     */


    //lista użytkowników
    private ArrayList<User> userList = new ArrayList<>();
    //lista pokoi
    private ArrayList<Room> roomList = new ArrayList<>();

    private int lastUserID = 1;
    private int lastRoomID = 1;

    //dodawanie pokoju do listy
    public void pushRoom (String roomName) throws IllegalArgumentException{
        if (roomListContains(roomName))
            throw new IllegalArgumentException("There is a room with this name");
        Room room = new Room(lastRoomID++, roomName);
        roomList.add(room);
    }
    //dodawanie użytkownika do listy
    public void pushUser (String userName, Session session) throws IllegalArgumentException{
        if (userListContains(userName))
            throw new IllegalArgumentException("There is a user with this name");
        User user = new User(lastUserID++, userName, session);
        userList.add(user);
    }
    //dodawanie użytkownika do pokoju
    public void pushUserToRoom(Session session, String roomName) throws NegativeArraySizeException{
        User user = getUserBySession(session);
        Room room = getRoomByName(roomName);
        room.pushUser(user);
    }
    //usuwanie użytkownika z pokoju
    public void deleteUserFromRoom(Session session, String roomName) throws IllegalArgumentException{
        User user = getUserBySession(session);
        Room room = getRoomByName(roomName);
        if (room.getUserList().contains(user)){
            room.deleteUser(user);
        }
        throw new IllegalArgumentException("There is no such user in this channel.");
    }

    private User getUserBySession(Session session) throws IllegalArgumentException {
        for (User u: userList) {
            if (u.getUserSession().equals(session))
                return u;
        }
        throw new IllegalArgumentException("No user connected to this session");
    }

    private Room getRoomByName(String roomName){
        for (Room r: roomList){
            if (r.getRoomName().equals(roomName))
                return r;
        }
        return null;
    }

    private boolean userListContains(String userName) {
        for (User u: userList){
            if(u.getUserName().equals(userName))
                return true;
        }
        return false;
    }

    private boolean roomListContains(String roomName){
        for (Room r: roomList) {
            if(r.getRoomName().equals(roomName))
                return true;
        }
        return false;
    }
    public ArrayList getRoomList(){ return roomList; }
    public ArrayList getUserList(){ return userList; }

}
