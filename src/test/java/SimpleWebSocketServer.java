//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.*;
//import lombok.extern.slf4j.Slf4j;
//import org.java_websocket.WebSocket;
//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.drafts.Draft_6455;
//import org.java_websocket.handshake.ClientHandshake;
//import org.java_websocket.server.WebSocketServer;
//
//import java.net.InetSocketAddress;
//import java.net.UnknownHostException;
//
///*
//    <dependency>
//            <groupId>org.java-websocket</groupId>
//            <artifactId>Java-WebSocket</artifactId>
//            <version>1.4.0</version>
//        </dependency>
// */
//@Slf4j
//public class SimpleWebSocketServer extends WebSocketServer {
//
//
//    private static SimpleWebSocketServer instance;
//
//    public static final Integer port = 8000;
//
//    @Override
//    public void onOpen(WebSocket conn, ClientHandshake handshake) {
//        Iterator<String> arg = handshake.iterateHttpFields();
//        while(arg.hasNext()) {
//            String next = arg.next();
//            log.info(next + ": " + handshake.getFieldValue(next));
//        }
//        log.info("handshake getResourceDescriptor", handshake.getResourceDescriptor());
//        byte[] content = handshake.getContent();
//        log.info("handshake getContent", new String(content));
//
//        log.info("Server open!");
//    }
//
//    @Override
//    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
//        log.info("Server closed!");
//    }
//
//    @Override
//    public void onMessage(WebSocket conn, String message) {
//        System.out.println("message = " + message);
//        conn.send("pong");
//        conn.sendPing();
//        //this.sendToAll(message);
//    }
//
//    @Override
//    public void onError(WebSocket conn, Exception e) {
//        log.error(e.getMessage(), e);
//    }
//
//    @Override
//    public void onStart() {
//        log.info("Server started!");
//    }
//
//    public void sendToAll(String text) {
//        this.broadcast(text);
////        Collection<WebSocket> con = getConnections();
////        synchronized (con) {
////            for (WebSocket c : con) {
////                c.send(text);
////            }
////        }
//    }
//
//    private SimpleWebSocketServer() {
//        super();
//        try {
//            SimpleWebSocketServer s = new SimpleWebSocketServer(port);
//            s.start();
//        } catch (UnknownHostException e) {
//           log.error(e.getMessage(), e);
//        }
//    }
//
//    private SimpleWebSocketServer(int port) throws UnknownHostException {
//        super(new InetSocketAddress(port));
//    }
//
//    public synchronized static SimpleWebSocketServer getInstance() {
//        if (instance == null) {
//            instance = new SimpleWebSocketServer();
//        }
//        return instance;
//    }
//
//    public static void main(String[] args) throws InterruptedException {
//        SimpleWebSocketServer.getInstance();
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        URI uri = null;
//        WebSocketClient client = null;
//        try {
//            uri = new URI("ws://localhost:" + SimpleWebSocketServer.port + "/abcderf");
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        if(uri != null){
//            Map<String, String> httpHeaders = new HashMap<>();
//            httpHeaders.put("a", "abc");
//            httpHeaders.put("n", "1234");
//            client = new SimpleWebSocketClient(uri, httpHeaders);
//            client.connectBlocking();
//        }
//        if(client != null){
//            client.send("hello java");
//        }
//    }
//}