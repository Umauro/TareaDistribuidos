import java.io.*;
import java.net.*;
import java.util.*;

public class MultiCastThreadRosa extends Thread {
    private MulticastSocket socket = null;
    private DatagramSocket socketU = null;
    private List<Titanes> titans = new ArrayList<Titanes>();
    private long TEN_SECONDS = 10000;
    private int puerto;
    private InetAddress addressMulticast; //230.0.0.1
    private InetAddress addressPeticiones;
    private int puertoPeticiones;
    private String nombre;
    private Titanes titan;

    public MultiCastThreadRosa() throws IOException {
        super("MultiCastThreadRosa");

        Scanner scanner = new Scanner(System.in);
        System.out.println("Nombre Distrito: ");
        nombre = scanner.nextLine();
        //nombre = "trost";

        System.out.println("IP Multicast: ");
        try{
            addressMulticast = InetAddress.getByName(scanner.nextLine());
            //addressMulticast = InetAddress.getByName("230.0.0.1");
        } catch (UnknownHostException e) {e.printStackTrace();}

        System.out.println("Puerto Multicast: ");
        puerto = scanner.nextInt();
        //puerto = 4446;

        String paraWhile = "";
        while(paraWhile == scanner.nextLine());
        System.out.println("Ingrese IP de Peticiones");
        paraWhile = scanner.nextLine();
        //paraWhile = "127.0.0.1";
        String aPeticiones = paraWhile;

        try{
          addressPeticiones = InetAddress.getByName(aPeticiones);
        }catch (UnknownHostException e) {e.printStackTrace();}

        System.out.println("Puerto peticiones: ");
        puertoPeticiones = scanner.nextInt();
        //puertoPeticiones = 4447;

        socket = new MulticastSocket(puerto);
        socketU = new DatagramSocket(puertoPeticiones);
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

    public void unicast(){
      Thread t = new Thread(new Runnable(){
        public void run(){
          try{
            byte[] recibir = new byte[256];
            DatagramPacket packet = new DatagramPacket(recibir, recibir.length);
            socketU.receive(packet);

            InetAddress cliente = packet.getAddress();
            int puertoCliente = packet.getPort();
            System.out.println(packet.getPort());


            Boolean flag = false;

            try{
              ByteArrayInputStream serializado = new ByteArrayInputStream(recibir);
              ObjectInputStream is = new ObjectInputStream(serializado);
              UnicastRequest request = (UnicastRequest)is.readObject();
              is.close();

              for(int i = 0; i < titans.size(); i++){
                if(titans.get(i).getId() == request.getId()){
                  titan = titans.get(i);
                  flag = true;
                  titans.remove(i);
                }
              }

              if(flag){
                UnicastRequest response = new UnicastRequest(titan.getId(), "asesinado");
                try{
                  ByteArrayOutputStream serial = new ByteArrayOutputStream();
                  ObjectOutputStream os = new ObjectOutputStream(serial);
                  os.writeObject(response);
                  os.close();
                  byte[] bufMsg = serial.toByteArray();
                  DatagramPacket packetResponse = new DatagramPacket(bufMsg, bufMsg.length, cliente, puertoCliente);
                  try{
                      socket.send(packetResponse);

                  } catch (IOException e){e.printStackTrace();}
                }catch (IOException e){
                  e.printStackTrace();
                }
              }

            } catch (ClassNotFoundException e){
              e.printStackTrace();
            }
          }catch (IOException e) {e.printStackTrace();}

        }
      });
      t.start();
    }

    /*
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
    */
    public void publicarTitan(){
        Titanes nuevotitan = new Titanes();
        titans.add(nuevotitan);
        //byte[] buf = new byte[256];

        //Intento de serialización
        try{
          ByteArrayOutputStream serial = new ByteArrayOutputStream();
          ObjectOutputStream os = new ObjectOutputStream(serial);
          os.writeObject(nuevotitan);
          os.close();
          byte[] buf = serial.toByteArray();
          DatagramPacket packet = new DatagramPacket(buf, buf.length, addressMulticast, puerto);
          try{
              socket.send(packet);
          } catch (IOException e){e.printStackTrace();}
        }catch (IOException e){
          e.printStackTrace();
        }

        //Enviar Mensaje

        //socket.close();
    }
}
