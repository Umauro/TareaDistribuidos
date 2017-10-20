import java.io.*;
import java.net.*;
import java.util.*;

public class servercentral extends Thread {

    protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    protected boolean moreQuotes = true;

    public servercentral() throws IOException {
        this("servercentral");
    }

    public servercentral(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(4445);

        try {
            in = new BufferedReader(new FileReader("info.txt"));
        } catch (FileNotFoundException e) {
            System.err.println("Could not open quote file. Serving time instead.");
        }
    }

    public void run() {

        /*while (moreQuotes) {
            try {
                byte[] buf = new byte[256];

                // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(packet.getData());
                System.out.println(received);

                // figure out response
                String dString = null;
                if (in == null)
                    dString = new Date().toString();
                else
                    dString = getNextQuote();
                buf = dString.getBytes();

                // send the response to the client at "address" and "port"
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            moreQuotes = false;
            }
        }
        */
        try{
          Scanner entrada = new Scanner(System.in);
          byte[] buf = new byte[256];
          byte[] bufMsg = new byte[256];

          // Recibir Paquete
          DatagramPacket packet = new DatagramPacket(buf, buf.length);
          socket.receive(packet);
          String received = new String(packet.getData());

          // Manejo de autorización
          InetAddress ipCliente = packet.getAddress();
          int puertoCliente = packet.getPort();

          System.out.println("Dar autorización a " + ipCliente.toString()+ " al distrito " + received + "?");
          System.out.println("[1] SI");
          System.out.println("[2] NO");
          int decision = entrada.nextInt();

          if(decision == 1){
            String mensaje = "ADELANTE";
            bufMsg = mensaje.getBytes();
            packet = new DatagramPacket(bufMsg, bufMsg.length, ipCliente, puertoCliente);
            socket.send(packet);
          }

          else if(decision == 2){
            String mensaje = "NOHAYMANO";
            bufMsg = mensaje.getBytes();
            packet = new DatagramPacket(bufMsg, bufMsg.length, ipCliente, puertoCliente);
            socket.send(packet);
          }
        } catch (IOException e){
          e.printStackTrace();
        }
        socket.close();
    }
}
