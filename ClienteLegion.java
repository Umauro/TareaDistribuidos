import java.io.*;
import java.net.*;
import java.util.*;

public class ClienteLegion {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
             System.out.println("Usage: java QuoteClient <hostname>");
             return;
        }

        MulticastSocket socket = new MulticastSocket(4446);
        InetAddress group = InetAddress.getByName("230.0.0.1");
        socket.joinGroup(group);

        DatagramPacket packet;
        for (int i = 0; i < 5; i++) {
            byte[] buf = new byte[256];
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            String received = new String(packet.getData());
            System.out.println("Quote of the Moment: " + received);
        }
        socket.leaveGroup(group);
        socket.close();
    }
}
