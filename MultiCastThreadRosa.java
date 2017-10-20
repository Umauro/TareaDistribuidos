import java.io.*;
import java.net.*;
import java.util.*;

public class MultiCastThreadRosa extends ThreadRosa {
    private List<Titanes> titans = new ArrayList<Titanes>();
    private long TEN_SECONDS = 10000;
    private String titanes = "Hola que tal";
    
    public MultiCastThreadRosa() throws IOException {
        super("MultiCastThreadRosa");
    }

    public void run() {
      int uno = 1;
      while(uno <= 10){
            try {
                byte[] buf = new byte[256];

                // construct quote
                String dString = titanes;
                buf = dString.getBytes();

                // send it
                InetAddress group = InetAddress.getByName("230.0.0.1");
                DatagramPacket packet = new DatagramPacket(buf, buf.length,
                    group, 4446);
                socket.send(packet);
                uno++;
                // sleep for a while
                try {
                    sleep((long)(Math.random() * TEN_SECONDS));
                } catch (InterruptedException e) { }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }
}
