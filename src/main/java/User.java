import org.eclipse.jetty.websocket.api.Session;

/**
 * Created by Gabrysia on 22.01.2017.
 */
public class User {

    private int userID;
    private String userName;
    private Session userSession;

    public User(int userID, String userName, Session userSession){
        this.userID = userID;
        this.userName = userName;
        this.userSession = userSession;
    }

    public int getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public Session getUserSession() {
        return userSession;
    }
}
