import java.net.*;
import java.io.*;
import java.nio.*;
import java.util.*;

class Handshake {
    public Socket connection;
    int peerid;
    Handshake(Socket connection) {
        this.connection = connection;
    }

    public static void sendHandshake(DataOutputStream output) throws Exception {
        byte [] buf = new byte[32];
        String header = "P2PFILESHARINGPROJ";
        for(int i = 0; i < header.length(); i++) {
            buf[i] = header.getBytes()[i];
        }
        for(int i = header.length(); i < 28; i++) {
            buf[i] = 0;
        }
        ByteBuffer int2bytes = ByteBuffer.allocate(4);
        int2bytes.putInt(peerProcess.id);
        buf[28] = int2bytes.get(0);
        buf[29] = int2bytes.get(1);
        buf[30] = int2bytes.get(2);
        buf[31] = int2bytes.get(3);
        output.write(buf, 0, 32);
    }

    public static int consumeHandshake(DataInputStream input) throws Exception {
        byte [] buf = new byte[32];
        input.read(buf);
        String header = new String(Arrays.copyOfRange(buf, 0, 19));
        if (header.equals("P2PFILESHARINGPROJ")) {
            throw new Exception("Bad handshake header: " + header);
        }
        int peerid = ByteBuffer.wrap(Arrays.copyOfRange(buf, 28, 32)).getInt();
        return peerid;
    }

    public int run() {
        try {
            DataInputStream input = new DataInputStream(connection.getInputStream());
            DataOutputStream output =  new DataOutputStream(connection.getOutputStream());
            sendHandshake(output);
            peerid = consumeHandshake(input);
            Peer peer = peerProcess.peers.get(peerid);
            if (peer.msgstream != null)
                throw new Exception("Creating message stream for peer more than once");
            peer.msgstream = new MessageStream(peer, connection, input, output);
            if (peer.thread != null)
                throw new Exception("Creating thread for peer more than once");
            peer.thread = new PeerThread(peer);
            peer.thread.start();
            return peerid;
        } catch (Exception e) {
            peerProcess.logger.logDebug("Exception raised during handshake: " + e);
        }
        return -1;
    }
}