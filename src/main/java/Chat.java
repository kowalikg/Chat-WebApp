import static spark.Spark.*;

public class Chat {

    public static void main(String[] args) {

        staticFiles.location("/public"); //chatbot.html is served at localhost:4567 (default port)
        webSocket("/chat", ChatWebSocketHandler.class);
        init();
    }


}
