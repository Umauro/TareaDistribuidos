import java.io.*;
import java.net.*;
import java.util.*;

public class Tcliente{
    private static InetAddress addressCentral;
    private static int puerto;
    private static String entradaTeclado;
    private static InetAddress addressMulticast;
    private static int puertoM;

    public Tcliente() {
        Scanner scanner = new Scanner(System.in);

        //IP Servidor Central
        System.out.println("IP Servidor Central: ");
        entradaTeclado = scanner.nextLine();
        try{
            addressCentral = InetAddress.getByName(entradaTeclado);
        } catch (UnknownHostException e) {e.printStackTrace();}
        //Puerto Servidor Central
        System.out.println("Ingrese Puerto servidor central: ");
        puerto = scanner.nextInt();

        //IP Multicast
        System.out.println("Ingrese IP servidor Multicast: "); //230.0.0.1
        while(entradaTeclado == scanner.nextLine());
        entradaTeclado = scanner.nextLine();
        try{
            addressMulticast = InetAddress.getByName(entradaTeclado);
        } catch (UnknownHostException e) {e.printStackTrace();}
        //Puerto Multicast
        System.out.println("Ingrese Puerto servidor multicast: ");
        puertoM = scanner.nextInt();
    }

    public void infoMulti(){
        Thread t = new Thread(new Runnable(){
            public void run(){
                //Codigo para recibir mensajes de servidor multicast
                byte[] buf = new byte[256];
                try{
                    MulticastSocket socketM = new MulticastSocket(puertoM);
                    socketM.joinGroup(addressMulticast); //230.0.0.1

                    DatagramPacket packetM;
                    int i = 1;
                    while(i != 10){
                        packetM = new DatagramPacket(buf, buf.length);
                        socketM.receive(packetM);

                        String receivedM = new String(packetM.getData());
                        System.out.println("Quote of the Moment: " + receivedM);
                        i++;
                    }
                    socketM.leaveGroup(addressMulticast);
                } catch (IOException e) {e.printStackTrace();}
            }
        });
        t.start();
    }

    public void serverCentral() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                    // get a **DatagramSocket**
                try{
                    DatagramSocket socket = new DatagramSocket();
                    // variables envío de request
                    byte[] buf = new byte[256];
                    byte[] recibir = new byte[256];
                    String mensaje = "";

                    //Envío Request al Servidor Central
                    buf = mensaje.getBytes();
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, addressCentral, puerto);
                    try{
                        socket.send(packet);

                        // get response
                        packet = new DatagramPacket(recibir, recibir.length);
                        socket.receive(packet);
                    } catch (IOException e) {e.printStackTrace();}

                    // display response
                    String received = new String(packet.getData());
                    System.out.println(received);

                    socket.close();
                } catch (SocketException e) {e.printStackTrace();}
            }
        });
        t.start();
    }
}
