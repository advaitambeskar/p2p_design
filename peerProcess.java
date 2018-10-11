import java.util.*;

public class peerProcess {
    static Config config = new Config();
    static HashMap<Integer, Peer> peers = new HashMap<Integer, Peer>();
    static Server server = null;

    public static void main(String[] args) throws Exception {
        Logger.logDebug("this is for debugging only");

        // initialize peers
        for (Peer peer : config.getPeers()) {
            peers.put(peer.id, peer);
        }

        server = new Server();
    }
}