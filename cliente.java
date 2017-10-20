import java.io.*;
import java.net.*;
import java.util.*;

public class cliente {
    private static InetAddress addressCentral;
    private static int puerto;
    private static String entradaTeclado;


    public static void main(String[] args) throws IOException {
        //PARA MULTICASTE
        MulticastSocket socket = new MulticastSocket(4446);
        InetAddress group = InetAddress.getByName("230.0.0.1");
        socket.joinGroup(group);

        DatagramPacket packet;
        for (int i = 0; i <= 10; i++) {
            byte[] buf = new byte[256];
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            String received = new String(packet.getData());
            System.out.println("Quote of the Moment: " + received);
        }
        socket.leaveGroup(group);

        //Para central
        /*
        // get a **DatagramSocket**
        DatagramSocket socket = new DatagramSocket();

        // variables envío de request
        byte[] buf = new byte[256];
        byte[] recibir = new byte[256];
        String mensaje = "";



        Scanner entrada = new Scanner(System.in);

        //Pedir Datos del Servidor Central
        //NOMBRE DISTRITO
        System.out.println("Ingrese nombre de distrito a investigar: ");
        mensaje = entrada.nextLine();

        //IP SERVIDOR
        System.out.println("Ingrese IP servidor central: ");
        entradaTeclado = entrada.nextLine();
        addressCentral = InetAddress.getByName(entradaTeclado);

        //PUERTO SERVIDOR
        System.out.println("Ingrese Puerto servidor central: ");
        puerto = entrada.nextInt();



        //Envío Request al Servidor Central
        buf = mensaje.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, addressCentral, puerto);
        socket.send(packet);

        // get response
        packet = new DatagramPacket(recibir, recibir.length);
        socket.receive(packet);

        // display response
        String received = new String(packet.getData());
        System.out.println(received);

        socket.close();
        */
    }
}
