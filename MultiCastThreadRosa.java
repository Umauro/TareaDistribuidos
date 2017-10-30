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

    //Constructor de la clase, se inicializan todas las variables necesarias y se realiza la conexion con el
    //servidor central.
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

    //Metodo que recibe las acciones del distrito por terminal
    //puede ser Publicar Titan o exit
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
                        //Se solicita la id del proximo titan al servidor central.
                        publicarTitan(pedirID());
                    }
                    else if(input.equals("exit")){
                        socket.close();
                        flag = 0;
                        whiletrue = 0;
                        System.exit(0);
                    }
                }
            }
        });
        t.start();
    }

    //Corresponde al metodo para responder a las solicitudes de los clientes
    //Puede ser, capturar titan o asesinar titan
    //en caso de no ser ninguna de las anteriores entonces el cliente ingreso recien al servidor
    //Y se envia la lista completa de titanes.
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

                      //Se determina la finalidad del mensaje, asesinar o capturar
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
                  //En caso de no ser para asesinar o capturar se envia la lista completa de titanes.
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

    //Se crea un elemento del tito titan con una id solicitada al servidor central.
    public void publicarTitan(int id){
        Titanes nuevotitan = new Titanes(id);
        titans.add(nuevotitan);
        enviarMulticast(nuevotitan);
    }

    //Metodo para enviar mensaje a todos los usuarios,
    //dependiendo del estado que tenga la variable tipo Titan, se eliminara, agregara cada titan a la lista
    //de los clientes.
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

    //Metodo de respuesta para las peticiones unicast, se entregan como parametros
    //la ip del cliente, el puerto de respuesta, el titan en cuestion y la accion que se debe realizar
    //ya sea ingresar a la lista o eliminar.
    public void enviarUnicast(InetAddress cliente, int puertoCliente, Titanes titan, String accion){
        UnicastRequest response;
        if(titan != null) response = new UnicastRequest(titan.getId(), accion);
        else response = new UnicastRequest(-1, accion);
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

    //Solicitud de ID al servidor central.
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
        return Integer.valueOf(numero.replaceAll("\\P{Print}",""));
    }
}
