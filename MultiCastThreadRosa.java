import java.io.*;
import java.net.*;
import java.util.*;

public class MultiCastThreadRosa extends Thread {
    private DatagramSocket socketC = null;
    private MulticastSocket socket = null;
    private DatagramSocket socketU = null;
    private List<Titanes> titans = new ArrayList<Titanes>();
    private long TEN_SECONDS = 10000;
    private int puerto;
    private InetAddress addressMulticast; //230.0.0.1
    private InetAddress addressPeticiones;
    private int puertoPeticiones;
    private String nombre;
    private Titanes titan = null;
    private int whiletrue = 1;
    private int puertoserverCentral;
    private InetAddress addressCentral;

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

        while(paraWhile == scanner.nextLine());
        System.out.println("Ingrese IP Server Central: ");
        addressCentral = InetAddress.getByName(scanner.nextLine());

        System.out.println("Puerto Server Central: ");
        puertoserverCentral = scanner.nextInt();

        socket = new MulticastSocket(puerto);
        socketU = new DatagramSocket(puertoPeticiones, addressPeticiones);
        socketC = new DatagramSocket();
    }

    public void terminal(){
        Thread t = new Thread(new Runnable(){
            public void run(){
                Scanner scanner = new Scanner(System.in);
                String input;
                int flag = 1;

                while(flag == 1){
                    System.out.println("[Distrito "+nombre+"]\t");
                    System.out.println("¿Que deseas hacer?");
                    System.out.println("[Publicar Titan || exit ]");
                    input = scanner.nextLine();
                    if(input.equals("Publicar Titan")){

                        publicarTitan(pedirID());
                    }
                    else if(input.equals("exit")){
                        socket.close();
                        flag = 0;
                        whiletrue = 0;
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
            while(whiletrue == 1){
                byte[] recibir = new byte[256];
                DatagramPacket packet = new DatagramPacket(recibir, recibir.length);
                socketU.receive(packet);

                InetAddress cliente = packet.getAddress();
                int puertoCliente = packet.getPort();
                Boolean flag = false;

                try{
                  ByteArrayInputStream serializado = new ByteArrayInputStream(recibir);
                  ObjectInputStream is = new ObjectInputStream(serializado);
                  UnicastRequest request = (UnicastRequest)is.readObject();
                  is.close();
                  if(request.getId() != -1){
                      for(int i = 0; i < titans.size(); i++){
                        if(titans.get(i).getId() == request.getId()){
                          titan = titans.get(i);
                          flag = true;
                          titans.remove(i);
                        }
                      }

                      if(flag){
                          if(request.getAccion().equals("asesinar")){
                              enviarUnicast(cliente, puertoCliente, titan, "asesinado");
                              titan.cambiarEstado("asesinado");
                              enviarMulticast(titan);
                          }
                          else{
                              enviarUnicast(cliente, puertoCliente, titan, "capturado");
                              titan.cambiarEstado("capturado");
                              enviarMulticast(titan);
                          }

                      }
                  }
                  else{
                      enviarUnicast(cliente, puertoCliente, titan, "perrito");
                  }

                } catch (ClassNotFoundException e){
                  e.printStackTrace();
                }
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
    public void publicarTitan(int id){
        Titanes nuevotitan = new Titanes(id);
        titans.add(nuevotitan);
        //byte[] buf = new byte[256];

        //Intento de serialización
        enviarMulticast(nuevotitan);
        //socket.close();
    }

    public void enviarMulticast(Titanes titan){
        try{
            ByteArrayOutputStream serial = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(serial);
            os.writeObject(titan);
            os.close();
            byte[] buf = serial.toByteArray();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, addressMulticast, puerto);
            try{
                socket.send(packet);
            } catch (IOException e){e.printStackTrace();}
        } catch (IOException e){e.printStackTrace();}
    }

    public void enviarUnicast(InetAddress cliente, int puertoCliente, Titanes titan, String accion){
        UnicastRequest response = new UnicastRequest(titan.getId(), accion);
        if(accion.replaceAll("\\P{Print}", "").equals("perrito")){
            response.asignarLista(titans);
        }
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
        } catch (IOException e){e.printStackTrace();}
    }

    private int pedirID(){
        byte[] bufMsg = "idporfi".getBytes();
        byte[] response = new byte[256];
        DatagramPacket packet = new DatagramPacket(bufMsg, bufMsg.length, addressCentral, puertoserverCentral);
        try{
            socketC.send(packet);
        } catch (IOException e){e.printStackTrace();}
        DatagramPacket packetResponse = new DatagramPacket(response, response.length);
        try{
            socketC.receive(packetResponse);
        } catch (IOException e){e.printStackTrace();}
        String numero = new String(packetResponse.getData());
        System.out.println(numero.replaceAll("\\P{Print}",""));
        return Integer.valueOf(numero.replaceAll("\\P{Print}",""));
    }
}
