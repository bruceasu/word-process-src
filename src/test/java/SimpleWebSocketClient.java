//import java.util.Map;
//import lombok.extern.slf4j.Slf4j;
//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.drafts.Draft;
//import org.java_websocket.handshake.ServerHandshake;
//
//import java.net.URI;
//
//@Slf4j
//public class SimpleWebSocketClient extends WebSocketClient {
//
//    public SimpleWebSocketClient(URI serverUri,  Map<String, String> httpHeaders) {
//        super(serverUri, httpHeaders);
//    }
//
//    @Override
//    public void onOpen(ServerHandshake serverHandshake) {
//        log.info("Client open!");
//    }
//
//    @Override
//    public void onMessage(String s) {
//        log.info("Client message:" + s);
//    }
//
//    @Override
//    public void onClose(int i, String s, boolean b) {
//        log.info("Client onClose!");
//    }
//
//    @Override
//    public void onError(Exception e) {
//        log.error(e.getMessage(), e);
//    }
//
//}