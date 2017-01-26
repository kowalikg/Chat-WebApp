var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat/");
webSocket.onopen = nameListener();
webSocket.onmessage = function (msg) { javaCommunicate(msg); };
webSocket.onclose = function () { alert("WebSocket connection closed") };

function nameListener(){
		document.getElementById("name_button").addEventListener("click", function () {
			saveName();
		});
}
	
function saveName (){
	var userName = document.getElementById('input_name').value;

	if(userName){
	    webSocket.send(generateJSON("newUser", userName));
	    document.cookie = "username=" + userName;
	    document.cookie = "roomName=global";
	}
	else{
	    tryOtherUserName();
	}
}

function javaCommunicate (msg) {
	var data = JSON.parse(msg.data);
	var error = data.error;

	if(error){
	    generateError(data);
	}
	else{
		updateChat(data);
	}
}

function updateChat(data){
	var type = data.type;
	switch(type){
	    case "user_added": showChatPanel(data); addRoomListener(); break;
        case "room_added": if(getCookie("roomName") == "global") refreshRoomList(data); break;
        case "delete_user_from_chat": refreshUsers(data); break;
        case "user_added_to_room": refreshUsers(data); refreshChat(data); break;
        case "get_room_list": if(getCookie("roomName") == "global") showChatPanel(data); break;
        case "user_deleted_from_room": refreshUsers(data); refreshChat(data);break;
        case "message": refreshChat(data);break;
	}
}

function generateError(data){
	var error = data.error;
	switch(error){
		case "username_unavailable": console.log("Błąd: " + error); tryOtherUserName(); break;
		case "room_name_unavailable": console.log("Błąd: " + error); tryOtherRoomName(); break;
	}
}

function tryOtherUserName(){
	document.getElementById('user_name_error').hidden = false;
	document.getElementById('user_name_error').setAttribute("class", "error");
}

function tryOtherRoomName(){
	document.getElementById('room_name_error').hidden = false;
	document.getElementById('room_name_error').setAttribute("class", "error");
}

function addRoomListener() {
	document.getElementById('addNewChatButton').addEventListener("click", function () {createNewRoom(); });
}

function createNewRoom(){
    var roomName = document.getElementById('newRoomInput').value;
	if (roomName){
		webSocket.send(generateJSON("newRoom", roomName));
	}
	else{
	    tryOtherRoomName();
	}
}

function insert(targetId, message) {
     document.getElementById(targetId).insertAdjacentHTML("afterbegin", message);
}

function showChatPanel(data){

    document.getElementById('welcome').innerHTML = 'Welcome ' + getCookie("username") + '!';
	document.getElementById('input_name').hidden = true;
	document.getElementById('name_button').hidden = true;
	document.getElementById('name_title').hidden = true;
	document.getElementById('user_name_error').hidden = true;
	document.getElementById('newRoomInput').hidden = false;
	document.getElementById('addNewChatButton').hidden = false;

	refreshRoomList(data);
}

function refreshRoomList(data){
    var rooms = data.rooms;
    number = rooms.length;

	document.getElementById('room_name_error').hidden = true;
	document.getElementById('room_list').hidden = false;
	document.getElementById('chatListTitle').hidden = false;
	document.getElementById('chatListTitle').innerHTML = "Here are " + number + " available rooms:";
	document.getElementById('room_list').innerHTML = "";
	rooms.forEach(function (room) {
        insert("room_list", "<li>" + room + "<button id = \"" + room + "\" onclick = \"joinRoom(this)\">Join</li>");
    });
}

function refreshUsers(data){
    var users = data.users;
    document.getElementById('user_list').hidden = false;

    document.getElementById('user_list').innerHTML = "";
    users.forEach(function (user) {
        insert("user_list", "<li>" + user + "</li>");
    });
}

function refreshChat(data){
    var message = data.message;
	insert("chat", message);
}

function hideRoomPanel(roomName){
    document.getElementById('chatListTitle').hidden = true;
    document.getElementById('newRoomInput').hidden = true;
    document.getElementById('addNewChatButton').hidden = true;
    document.getElementById('chat').hidden = false;
    document.getElementById('room_list').innerHTML = "";
    document.getElementById('room_name_error').hidden = true;
}

function showMessagePanel(){
    insert("messageDiv", "<button id ='sendMessage'>Send</button>");
    insert("messageDiv", "<input id ='messageInput' placeholder = 'Write your message'></>");
    document.getElementById("sendMessage").addEventListener("click", function () {sendMessage();});
}

function deleteRoomInterface(){
    document.getElementById("room_list").innerHTML = "";
	document.getElementById('user_list').hidden = true;
    document.getElementById("chat").innerHTML = "";
    document.getElementById('chat').hidden = true;
    document.getElementById('messageInput').hidden = true;
    document.getElementById('sendMessage').hidden = true;
}

function joinRoom(img){
    var roomName = img.id;
    hideRoomPanel(roomName);

    insert("room_list", "<li>" + roomName + "<button id = \"" + roomName + "\" onclick = \"leaveRoom(this)\">Leave</li>");
    showMessagePanel();
    document.cookie = "roomName=" + roomName;
    webSocket.send(generateJSON("joinRoom", roomName));
}

function sendMessage(){
   var message = document.getElementById("messageInput").value;
   webSocket.send(generateJSON("message", message, getCookie("roomName")));
}

function leaveRoom(button){
    var roomName = button.id;
    document.cookie = "roomName=global";
    deleteRoomInterface();

    webSocket.send(generateJSON("leaveRoom", roomName));
    webSocket.send(generateJSON("getRoomList", "all"));
}

function generateJSON(typeOfMessage, message){
	var JSONResult = "{\"type\": \"" + typeOfMessage + "\", \"message\": \"" + message + "\"};";
	return JSONResult;
}

function generateJSON(typeOfMessage, message , room){
    var JSONResult = "{\"type\": \"" + typeOfMessage + "\", \"room\": \"" +  room + "\", \"message\": \"" + message + "\"};";
    return JSONResult;
}

function getCookie(cname) {
    var name = cname + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    for(var i = 0; i <ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}