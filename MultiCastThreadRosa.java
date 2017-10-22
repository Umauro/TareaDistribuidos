import java.io.*;
import java.net.*;
import java.util.*;

public class MultiCastThreadRosa extends Thread {
    private MulticastSocket socket = null;
    private List<Titanes> titans = new ArrayList<Titanes>();
    private long TEN_SECONDS = 10000;
    private String titanes = "Hola que tal";
    private int puerto;
    private InetAddress addressMulticast; //230.0.0.1
    private String nombre;

    public MultiCastThreadRosa() throws IOException {
        super("MultiCastThreadRosa");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Nombre Distrito: ");
        nombre = scanner.nextLine();

        System.out.println("IP Multicast: ");
        try{
            addressMulticast = InetAddress.getByName(scanner.nextLine());
        } catch (UnknownHostException e) {e.printStackTrace();}

        System.out.println("Puerto Multicast: ");
        puerto = scanner.nextInt();

        socket = new MulticastSocket(puerto);
    }

    public void run() {
      int uno = 1;
      while(uno <= 100){
            try {
                byte[] buf = new byte[256];

                // construct quote
                String dString = titanes;
                buf = dString.getBytes();

                // send it
                DatagramPacket packet = new DatagramPacket(buf, buf.length,
                    addressMulticast, puerto);
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
