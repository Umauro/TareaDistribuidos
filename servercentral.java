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
    }

    public void run() {

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
