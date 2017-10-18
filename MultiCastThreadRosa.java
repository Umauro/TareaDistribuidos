import java.io.*;
import java.net.*;
import java.util.*;

public class MultiCastThreadRosa extends ThreadRosa {

    private long FIVE_SECONDS = 5000;

    public MultiCastThreadRosa() throws IOException {
        super("MultiCastThreadRosa");
    }

    public void run() {
        while (moreInfo) {
            try {
                byte[] buf = new byte[256];

                // construct quote
                String dString = null;
                if (in == null)
                    dString = new Date().toString();
                else
                    dString = getNextQuote();
                buf = dString.getBytes();

                // send it
                InetAddress group = InetAddress.getByName("230.0.0.1");
                DatagramPacket packet = new DatagramPacket(buf, buf.length,
                    group, 4446);
                socket.send(packet);

                // sleep for a while
                try {
                    sleep((long)(Math.random() * FIVE_SECONDS));
                } catch (InterruptedException e) { }
            } catch (IOException e) {
                e.printStackTrace();
                moreInfo = false;
            }
        }
        socket.close();
    }
}
