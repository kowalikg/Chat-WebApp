import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

@WebSocket
public class ChatWebSocketHandler {

    private String sender, msg;
    private ChatContainer chatContainer = new ChatContainer();

    private HashMap<String, String> whereToSend = new HashMap<>();

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {

    }
    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) throws JSONException {
        User userWhoLeft = chatContainer.getUserBySession(user);
        Room room = chatContainer.getCurrentUserRoom(userWhoLeft);
        if (room != null){
            String status = chatContainer.deleteUserFromRoom(user, room.getRoomName());
            chatContainer.deleteUserFromChat(userWhoLeft);
            if(whereToSend.get(status).equals("b"))
                sendToBroadcast(status, room.getRoomName(), userWhoLeft.getUserName(), "");
        }
        chatContainer.deleteUserFromChat(userWhoLeft);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            parseJSON(session, message);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseJSON(Session session, String message) throws JSONException, IOException {
        JSONObject jsonObject = new JSONObject(message);
        String typeOfJson = jsonObject.getString("type");
        String messageFromJson = jsonObject.getString("message");

        String typeOfMessage = "";
        String roomName = "global";
        String userName = "";

        generateSendingMap();

        switch (typeOfJson){
            case "newUser": typeOfMessage = chatContainer.pushUser(messageFromJson, session);
            break;
            case "newRoom": typeOfMessage = chatContainer.pushRoom(messageFromJson);
            break;
            case "getRoomList": typeOfMessage = "get_room_list"; break;
            case "joinRoom":
                roomName = messageFromJson;
                typeOfMessage = chatContainer.pushUserToRoom(session, roomName);
                userName = chatContainer.getUserBySession(session).getUserName();
                break;
            case "leaveRoom":
                roomName = messageFromJson;
                userName = chatContainer.getUserBySession(session).getUserName();
                typeOfMessage = chatContainer.deleteUserFromRoom(session, roomName);
                break;
            case "message":
                roomName = jsonObject.getString("room");
                userName = chatContainer.getUserBySession(session).getUserName();
                message = messageFromJson;
                typeOfMessage = typeOfJson;
        }

        if(whereToSend.get(typeOfMessage).equals("b")){
            sendToBroadcast(typeOfMessage, roomName, userName, message);
        }
        else{
            sendToUser(typeOfMessage, session);
        }
        if (roomName.equals("ChatBot") && typeOfJson.equals("message")){
            String botAnswer = chatContainer.generateAnswerFromChatBot(message);
            sendToBroadcast(typeOfMessage, roomName, "ChatBot", botAnswer);
        }
        if (roomName.equals("ChatBot") && typeOfJson.equals("joinRoom")){
            String botAnswer = chatContainer.generateAnswerFromChatBot("");
            sendToBroadcast("message", roomName, "ChatBot", botAnswer);
        }

    }

    private void sendToUser(String message, Session session) throws JSONException, IOException {
        JSONGenerator generator = new JSONGenerator();
        JSONObject messageObject = null;
        switch(message) {
            case "username_unavailable":
            case "room_name_unavailable":
            case "no_such_user_in_room":
                messageObject = new JSONObject().put("error", message);
                break;
            case "user_added":
            case "get_room_list": messageObject = generator.generateRoomList(chatContainer.getRoomList()); break;
        }
        messageObject.put("type", message);
        session.getRemote().sendString(String.valueOf(messageObject));
    }

    private void sendToBroadcast(String typeOfMessage, String roomName, String userName, String message) throws JSONException {
        JSONGenerator generator = new JSONGenerator();
        JSONObject messageObject = null;

        String messageToShow = "";
        String sender = "";

        switch(typeOfMessage){
            case "room_added": messageObject = generator.generateRoomList(chatContainer.getRoomList()); break;
            case "delete_user_from_chat":
                messageObject = generator.generateUserList(chatContainer.getRoomByName(roomName).getUserList());
                break;
            case "user_added_to_room":
                messageObject = generator.generateUserList(chatContainer.getRoomByName(roomName).getUserList());
                messageToShow =  userName + " join to the room.";
                sender = "server";
                break;
            case "user_deleted_from_room":
                messageObject = generator.generateUserList(chatContainer.getRoomByName(roomName).getUserList());
                messageToShow = userName + " left the room";
                sender = "server";
                break;
            case "message":
                messageObject = generator.generateUserList(chatContainer.getRoomByName(roomName).getUserList());
                messageToShow = message;
                sender = userName;
        }
        if (messageObject != null){
            messageObject.put("type", typeOfMessage);
            chatContainer.sendMessegeToEveryUserInRoom(roomName, sender, messageObject, messageToShow);
        }

    }

    private void generateSendingMap() {
        whereToSend.put( "room_name_unavailable", "s");
        whereToSend.put( "room_added", "b");
        whereToSend.put( "username_unavailable", "s");
        whereToSend.put( "user_added", "s");
        whereToSend.put( "no_such_user_in_room", "s");
        whereToSend.put( "get_room_list", "s");
        whereToSend.put( "user_added_to_room", "b");
        whereToSend.put( "user_deleted_from_room", "b");
        whereToSend.put( "message", "b");
    }

}
