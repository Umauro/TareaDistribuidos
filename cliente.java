import java.io.*;
import java.net.*;
import java.util.*;

public class cliente {
    private static InetAddress addressCentral;
    private static int puerto;

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
        //pedirIp();
        //InetAddress address = InetAddress.getByName(args[0]);

        String entradaTeclado = " ";
        Scanner entrada = new Scanner(System.in);

        //Pedir Datos del Servidor Central
        System.out.println("Ingrese IP servidor central: ");
        entradaTeclado = entrada.nextLine();
        System.out.println("Ingrese Puerto servidor central: ");
        puerto = entrada.nextInt();

        addressCentral = InetAddress.getByName(entradaTeclado);
        
        DatagramPacket packet = new DatagramPacket(buf, buf.length, addressCentral, puerto);
        socket.send(packet);

        // get response
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        // display response
        String received = new String(packet.getData());
        System.out.println("Quote of the Moment: " + received);

        socket.close();
    }
}
