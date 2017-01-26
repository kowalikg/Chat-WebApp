import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static j2html.TagCreator.*;

/**
 * Created by Gabrysia on 22.01.2017.
 */
public class ChatContainer {

    //lista użytkowników
    private ArrayList<User> chatUserList = new ArrayList<>();
    //lista pokoi
    private ArrayList<Room> roomList = new ArrayList<>();

    public ChatContainer(){
        roomList.add(new ChatBot());
    }
    //dodawanie pokoju do listy
    public String pushRoom (String roomName){
        if (roomListContains(roomName)){
            return "room_name_unavailable";
        }
        Room room = new Room(roomName);
        roomList.add(room);
        return "room_added";
    }
    //dodawanie użytkownika do listy
    public String pushUser (String userName, Session session){
        if (userListContains(userName))
            return "username_unavailable";
        User user = new User(userName, session);
        chatUserList.add(user);
        return "user_added";
    }
    //dodawanie użytkownika do pokoju
    public String pushUserToRoom(Session session, String roomName){
        User user = getUserBySession(session);
        Room room = getRoomByName(roomName);

        room.pushUser(user);
        return "user_added_to_room";
    }
    //usuwanie użytkownika z pokoju
    public String deleteUserFromRoom(Session session, String roomName){
        User user = getUserBySession(session);
        Room room = getRoomByName(roomName);
        if (room.getUserList().contains(user)){
            room.deleteUser(user);
            return "user_deleted_from_room";
        }
        return "no_such_user_in_room";
    }
    //usuwanie użytkownika z chatu
    public void deleteUserFromChat(User user){
        chatUserList.remove(user);
    }
    //pozyskiwanie użytkownika przez sesję
    public User getUserBySession(Session session) throws IllegalArgumentException {
        for (User u: chatUserList) {
            if (u.getUserSession().equals(session))
                return u;
        }
        throw new IllegalArgumentException("No user connected to this session");
    }
    //pozyskiwanie pokoju przez jego imię
    public Room getRoomByName(String roomName) {
        for (Room r : roomList) {
            if (r.getRoomName().equals(roomName))
                return r;
        }
        return null;
    }
    //pozyskiwanie pokoju w którym jest użytkownik
    public Room getCurrentUserRoom(User user){
        for (Room r: roomList){
            if(r.containsUser(user) != null)
                return r;
        }
        return null;
    }
    // generowanie odpowiedzi od chatbota
    public String generateAnswerFromChatBot(String message) throws IOException, JSONException {
        String chatBotAnswer =  ((ChatBot) roomList.get(0)).generateAnswer(message);
        return chatBotAnswer;
    }
    //generowanie HTMLa
    private String createHtmlMessageFromSender(String sender, String message) {
        return article().with(
                b(sender + " says:" ),
                p(message),
                span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date()))
        ).render();
    }
    //wysyłanie wiadomości wszystkim użytkownikom w pokoju
    public void sendMessegeToEveryUserInRoom(String roomName, String sender, JSONObject message, String messageToShow){
        Set<Session> userSet;
        if(roomName.equals("global")){
            userSet = generateSessionList();
        }
        else{
            Room room = getRoomByName(roomName);
            userSet = generateSessionList(room);
        }
        userSet.stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(String.valueOf(message.put("sender", sender).put("message", createHtmlMessageFromSender(sender, messageToShow))));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });
    }
    //sprawdzanie czy jest taki użytkownik
    private boolean userListContains(String userName) {
        for (User u: chatUserList){
            if(u.getUserName().equals(userName))
                return true;
        }
        return false;
    }
    //sprawdzanie czy jest taki pokój
    private boolean roomListContains(String roomName){
        for (Room r: roomList) {
            if(r.getRoomName().equals(roomName))
                return true;
        }
        return false;
    }
    //generowanie zbioru sesji użytkowników z pokoju
    private Set generateSessionList(Room room) {
        ArrayList<User> userRoomList = room.getUserList();
        HashSet<Session> userSessions = new HashSet<>();
        for (User u: userRoomList) {
            userSessions.add(u.getUserSession());
        }
        return userSessions;

    }
    //generowanie zbioru sesji wszystkich użytkowników
    private Set generateSessionList() {
        HashSet<Session> userSessions = new HashSet<>();
        for (User u: chatUserList) {
            userSessions.add(u.getUserSession());
        }
        return userSessions;

    }

    public ArrayList<Room> getRoomList(){ return roomList; }
}
