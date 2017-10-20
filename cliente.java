import java.io.*;
import java.net.*;
import java.util.*;

public class cliente {
    private static InetAddress addressCentral;
    private static int puerto;
    private static String entradaTeclado;
    private static InetAddress addressMulticast;
    private static int puertoM;


    public static void main(String[] args) throws IOException {
        //Para central

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

        //PARA MULTICAST
        //IP SERVIDOR MULTICAST
        System.out.println("Ingrese IP servidor Multicast: ");
        while(entradaTeclado == entrada.nextLine());
        entradaTeclado = entrada.nextLine();
        InetAddress group = InetAddress.getByName(entradaTeclado);
        addressMulticast = InetAddress.getByName(entradaTeclado);

        //PUERTO SERVIDOR MULTICAST
        System.out.println("Ingrese Puerto servidor multicast: ");
        puertoM = entrada.nextInt();

        MulticastSocket socketM = new MulticastSocket(puertoM);
        socketM.joinGroup(group); //230.0.0.1

        DatagramPacket packetM;
        for (int i = 0; i <= 10; i++) {
            packetM = new DatagramPacket(buf, buf.length);
            socketM.receive(packetM);

            String receivedM = new String(packetM.getData());
            System.out.println("Quote of the Moment: " + receivedM);
        }
        socketM.leaveGroup(addressMulticast);

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
    }
}
