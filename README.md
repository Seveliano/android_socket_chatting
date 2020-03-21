# android_socket_chatting

This is an Android Chatting Project with Socket.io.
Express server is used for this.

- Socket.io library
    implementation ('io.socket:socket.io-client:1.0.0') {
        // excluding org.json which is provided by Android
        exclude group: 'org.json', module: 'json'
    }
    
- Firebase push notification is used.
