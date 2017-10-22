import java.io.*;
import java.net.*;
import java.util.*;

public class MultiCastThreadRosa extends Thread {
    private MulticastSocket socket = null;
    private List<Titanes> titans = new ArrayList<Titanes>();
    private long TEN_SECONDS = 10000;
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

    public void terminal(){
        Thread t = new Thread(new Runnable(){
            public void run(){
                Scanner scanner = new Scanner(System.in);
                String input;
                int flag = 1;

                while(flag == 1){
                    System.out.println("[Distrito "+nombre+"]\t");
                    input = scanner.nextLine();

                    if(input.equals("Publicar Titan")){
                        publicarTitan();
                    }
                    else if(input.equals("exit")){
                        socket.close();
                        flag = 0;
                    }
                }
            }
        });
        t.start();
    }


    public void mensajeContinuo(){
        Thread t = new Thread(new Runnable(){
            public void run(){
                Titanes titanActual;
                while(true){
                    int i;
                    String mensajeDifusion = "[Distrito "+nombre+"] Titanes en la zona:\n";

                    for(i = 0; i < titans.size(); i++){
                        titanActual = titans.get(i);
                        mensajeDifusion += titanActual.mostrar();
                    }

                    byte[] buf = new byte[256];
                    buf = mensajeDifusion.getBytes();

                    DatagramPacket packet = new DatagramPacket(buf, buf.length, addressMulticast, puerto);
                    try{
                        socket.send(packet);
                    } catch (IOException e) {e.printStackTrace();}
                    try {
                        sleep((long)(Math.random() * TEN_SECONDS));
                    } catch (InterruptedException e) {e.printStackTrace();}
                }
            }
        });
        t.start();
    }

    public void publicarTitan(){
        Titanes nuevotitan = new Titanes();
        titans.add(nuevotitan);
        byte[] buf = new byte[256];

        //Contruccion de mensaje
        String mensaje = "[Distrito "+nombre+"]Aparece nuevo Titan! "+nuevotitan.getNombre()+", tipo "
                            +nuevotitan.getTipo()+", ID"+nuevotitan.getId()+".";
        buf = mensaje.getBytes();

        //Enviar Mensaje
        DatagramPacket packet = new DatagramPacket(buf, buf.length, addressMulticast, puerto);
        try{
            socket.send(packet);
        } catch (IOException e){e.printStackTrace();}
        //socket.close();
    }
}
