import java.io.*;
import java.net.*;
import java.util.*;

public class cliente {
  private InetAddress addressCentral = new InetAddress();

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
             System.out.println("Usage: java QuoteClient <hostname>");
             return;
        }

        // get a **DatagramSocket**
        DatagramSocket socket = new DatagramSocket();
        // send request
        byte[] buf = new byte[256];
        String mensaje = "Hola";
        buf = mensaje.getBytes();
        pedirIp();
        //InetAddress address = InetAddress.getByName(args[0]);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, addressCentral, 4445);
        socket.send(packet);

        // get response
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        // display response
        String received = new String(packet.getData());
        System.out.println("Quote of the Moment: " + received);

        socket.close();
    }

    public void pedirIp(){
      String entradaTeclado = "";
      Scanner entrada = new Scanner(System.in);
      System.out.println("Ingrese IP servidor central");
      entradaTeclado = entrada.nextLine();
      anddresCentral.getByName(entradaTeclado)
    }
}
