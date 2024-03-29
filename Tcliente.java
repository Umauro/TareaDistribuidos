import java.io.*;
import java.net.*;
import java.util.*;

public class Tcliente{
    private static InetAddress addressCentral;
    private static int puerto;
    private static String entradaTeclado;
    private static InetAddress addressMulticast;
    private static int puertoM;
    private int titan;
    private InetAddress ipServer;
    private int puertoU;
    private DatagramSocket socketUni;
    private static ConexionMulticast conexion;
    private MulticastSocket socketM;
    private List<Titanes> titanes = new ArrayList<Titanes>();
    private List<Titanes> asesinados = new ArrayList<Titanes>();
    private List<Titanes> capturados = new ArrayList<Titanes>();
    protected Boolean flag = true;
    protected Boolean adios = false;

    public Tcliente() {
        Scanner scanner = new Scanner(System.in);

        //IP Servidor Central
        System.out.println("[Cliente] IP Servidor Central: ");
        entradaTeclado = scanner.nextLine();
        //entradaTeclado = "127.0.0.1";
        try{
            addressCentral = InetAddress.getByName(entradaTeclado);
        } catch (UnknownHostException e) {e.printStackTrace();}
        //Puerto Servidor Central
        System.out.println("[Cliente] Ingrese Puerto servidor central: ");
        puerto = scanner.nextInt();
        //puerto = 4445;
    }

    //Logica de acciones del cliente.
    public void terminal(final String ipPeticiones,final int puertoPeticiones){
      Thread t = new Thread(new Runnable(){
          public void run(){
            byte[] response = new byte[256];
            try{
              ipServer = InetAddress.getByName(ipPeticiones);
            }catch (UnknownHostException e) {e.printStackTrace();}
            puertoU = puertoPeticiones;
            pedirLista();
            int opcion;
            Scanner entrada = new Scanner(System.in);
            Titanes nachin = null;
            while(flag){
              //Menu del cliente
              System.out.println("[CLIENTE] Ingrese Opción");
              System.out.println("[CLIENTE] [1] Lista de Titanes");
              System.out.println("[Cliente] [2] Cambiar de Distrito");
              System.out.println("[Cliente] [3] Capturar Titan");
              System.out.println("[Cliente] [4] Asesinar Titan");
              System.out.println("[Cliente] [5] Lista Titanes Capturados");
              System.out.println("[Cliente] [6] Lista Titanes Asesinados");
              opcion = entrada.nextInt();

              //Lista titanes
              if(opcion == 1){
                for(int i = 0; i < titanes.size(); i++){
                  System.out.println("*******");
                  System.out.println("ID: " + titanes.get(i).getId());
                  System.out.println("Nombre: " + titanes.get(i).getNombre());
                  System.out.println("Tipo: " + titanes.get(i).getTipo());
                  System.out.println("*******");
                }
              }

              //Cambiar de distrito, se vuelve a realizar la peticion al servidor central.
              else if(opcion == 2){
                System.out.println("Abandonando Distrito");
                try{
                    serverCentral();
                    ConexionMulticast conexion = getConexion();
                    addressMulticast = InetAddress.getByName(conexion.getMulticastIp());
                    puertoM = conexion.getMulticastPort();
                    ipServer = InetAddress.getByName(conexion.getPeticionesIp());
                    puertoU = conexion.getPeticionesPort();
                    //socketM.close();
                    try{
                        socketM = new MulticastSocket(puertoM);
                        socketM.joinGroup(addressMulticast);
                        socketM.setSoTimeout(500);
                        pedirLista();
                    }catch(IOException e){
                        e.printStackTrace();
                    }

                }catch(UnknownHostException e){e.printStackTrace();
                }
              }

              //Capturar titan, se comprueba si existe dicho titan, en caso positivo se envia la peticiones
              //al servidor distrito con una peticion con la id del titan y la accion "capturar"
              else if(opcion == 3){
                System.out.println("Ingrese ID del titan a Capturar");
                int id = entrada.nextInt();
                UnicastRequest serverResponse;
                nachin = null;
                for(int i = 0; i < titanes.size(); i++){
                  if(titanes.get(i).getId() == id){
                    nachin = titanes.get(i);
                  }
                }
                if(nachin == null){
                    System.out.println("No existe dicho titan en este servidor.");
                }
                else{
                    if(nachin.getTipo().equals("Normal") || nachin.getTipo().equals("Cambiante")){
                        serverResponse = enviarPeticion(id, "capturar");
                        if(serverResponse.getAccion().replaceAll("\\P{Print}","").equals("capturado".replaceAll("\\P{Print}",""))){
                            capturados.add(nachin);
                        }
                    }
                    else{
                        System.out.println("No se puede capturar un titan Excentrico");
                    }
                }
              }

              //Sigue la misma logica que la opcion 3, solo que con el metodo asesinar.
              else if(opcion == 4){
                System.out.println("Ingrese ID del titan a Asesinar");
                int id = entrada.nextInt();
                UnicastRequest serverResponse;
                nachin = null;

                for(int i = 0; i < titanes.size(); i++){
                  if(titanes.get(i).getId() == id){
                    nachin = titanes.get(i);
                  }
                }
                if(nachin == null){
                    System.out.println("No existe dicho titan en este servidor.");
                }
                else{
                    if(nachin.getTipo().equals("Normal") || nachin.getTipo().equals("Excentrico")){
                        serverResponse = enviarPeticion(id, "asesinar");
                        if(serverResponse.getAccion().replaceAll("\\P{Print}","").equals("asesinado".replaceAll("\\P{Print}",""))){
                            asesinados.add(nachin);
                        }
                    }
                    else{
                        System.out.println("No se puede asesinar un titan Cambiante");
                    }
                  }
              }

              //Se lista la lista local de titanes capturados.
              else if(opcion == 5){
                  for(int i = 0; i < capturados.size(); i++){
                    System.out.println("*******");
                    System.out.println("ID: " + capturados.get(i).getId());
                    System.out.println("Nombre: " + capturados.get(i).getNombre());
                    System.out.println("Tipo: " + capturados.get(i).getTipo());
                    System.out.println("*******");
                  }
              }

              //Se lista la lista local de titanes asesinados.
              else if(opcion == 6){
                  for(int i = 0; i < asesinados.size(); i++){
                    System.out.println("*******");
                    System.out.println("ID: " + asesinados.get(i).getId());
                    System.out.println("Nombre: " + asesinados.get(i).getNombre());
                    System.out.println("Tipo: " + asesinados.get(i).getTipo());
                    System.out.println("*******");
                  }
              }
            }
          }
      });
      t.start();
    }

    //Metodo para escuchar los informes del servidor multicast.
    public void infoMulti(final String ipMulticast,final int puertoMulticast){
        Thread t = new Thread(new Runnable(){
            public void run(){
              try{
                  addressMulticast = InetAddress.getByName(ipMulticast);
              } catch (UnknownHostException e) {e.printStackTrace();}

                //Codigo para recibir mensajes de servidor multicast
                byte[] buf = new byte[256];
                try{
                    socketM = new MulticastSocket(puertoMulticast);
                    socketM.joinGroup(addressMulticast); //230.0.0.1
                    socketM.setSoTimeout(500);
                    DatagramPacket packetM;
                    while(true){
                        try{
                            packetM = new DatagramPacket(buf, buf.length);
                            socketM.receive(packetM);

                            try{
                              ByteArrayInputStream serializado = new ByteArrayInputStream(buf);
                              ObjectInputStream is = new ObjectInputStream(serializado);
                              Titanes nuevotitan = (Titanes)is.readObject();
                              is.close();
                              String mensaje;
                              int id, i;
                              id = nuevotitan.getId();
                              //Dependiendo del estado que reciban desde el servidor van a ejecutar distintas acciones,
                              //Si el estado es 1, se agrega el titan a la lista local
                              if(nuevotitan.getState() == 1){
                                  mensaje = "[CLIENTE] Aparece nuevo Titan! "+ nuevotitan.getNombre() + ", tipo " +nuevotitan.getTipo() +", ID"
                                                  +nuevotitan.getId()+".";
                                  System.out.println(mensaje);
                                  titanes.add(nuevotitan);
                              }
                              //SI el estado es 2 se elimina el titan de la lista.
                              else if(nuevotitan.getState() == 2){
                                  mensaje = "[Cliente] El titan "+nuevotitan.getNombre()+" fue asesinado!";
                                  System.out.println(mensaje);
                                  for(i = 0; i < titanes.size(); i++){
                                      if(titanes.get(i).getId() == id){
                                          titanes.remove(i);
                                          break;
                                      }
                                  }
                              }
                              //Si el estado es 3 se elimina, pero con un mensaje distinto.
                              else if(nuevotitan.getState() == 3){
                                  mensaje = "[Cliente] El titan "+nuevotitan.getNombre()+" fue capturado!";
                                  System.out.println(mensaje);
                                  for(i = 0; i < titanes.size(); i++){
                                      if(titanes.get(i).getId() == id){
                                          titanes.remove(i);
                                          break;
                                      }
                                  }
                              }

                            } catch (ClassNotFoundException e){
                              e.printStackTrace();
                            }
                        }catch (SocketTimeoutException e){

                        }
                    }
                    //socketM.leaveGroup(addressMulticast);
                } catch (IOException e) {e.printStackTrace();}
            }
        });
        t.start();
    }

    //Metodo conexion al server central
    public void serverCentral() {
      int ok = 0;
      //Mientras la variable ok sea 0, se seguira intentando conectar a un nuevo distrito.
      while(ok == 0){
      try{
            DatagramSocket socket = new DatagramSocket();
            // variables envío de request
            byte[] buf = new byte[256];
            byte[] recibir = new byte[256];
            String mensaje = "";

            //Pedir nombre del distrito
            Scanner entrada = new Scanner(System.in);
            System.out.println("[CLIENTE] Ingrese nombre del distrito a investigar");
            mensaje = entrada.nextLine();
            //Envío Request al Servidor Central
            buf = mensaje.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, addressCentral, puerto);
            try{
              socket.send(packet);
              // get response
              packet = new DatagramPacket(recibir, recibir.length);
              socket.receive(packet);
              try{
                ByteArrayInputStream serializado = new ByteArrayInputStream(recibir);
                ObjectInputStream is = new ObjectInputStream(serializado);
                ConexionMulticast nuevaConexion = (ConexionMulticast)is.readObject();
                is.close();
                if(nuevaConexion.getPeticionesPort() == -1){
                    System.out.println("El servidor no existe");
                }
                else if(nuevaConexion.getPeticionesPort() == -2){
                    System.out.println("Se nego el acceso al servidor");
                }
                else{
                    conexion = nuevaConexion;
                    ok = 1;
                }
              } catch (ClassNotFoundException e){
                e.printStackTrace();
              }
            } catch (IOException e) {e.printStackTrace();}
              socket.close();
            } catch (SocketException e) {e.printStackTrace();}
        }
    }

    public ConexionMulticast getConexion(){
      return conexion;
    }

    //Metodo para pedir la lista de titanes al servidor distrito
    public void pedirLista(){
        UnicastRequest nuevodist = enviarPeticion(-1, "lista");
        titanes = nuevodist.getLista();
    }

    //Metodo de envio de paquete para realizar alguna accion en los titanes.
    public UnicastRequest enviarPeticion(int i, String accion){
        try{
          socketUni = new DatagramSocket();
        }catch (SocketException e){e.printStackTrace();}
        DatagramPacket packetRequest;
        DatagramPacket packetResponse;
        //Tamaño de la respuesta.
        byte[] response = new byte[512];
        UnicastRequest serverResponse = new UnicastRequest(i, accion);
        UnicastRequest request = new UnicastRequest(i, accion);
        try{
          ByteArrayOutputStream serial = new ByteArrayOutputStream();
          ObjectOutputStream os = new ObjectOutputStream(serial);
          os.writeObject(request);
          os.close();
          byte[] bufMsg = serial.toByteArray();
          try{
              packetRequest = new DatagramPacket(bufMsg, bufMsg.length, ipServer, puertoU);
              socketUni.send(packetRequest);
              packetResponse = new DatagramPacket(response, response.length);
              socketUni.receive(packetResponse);
              try{
                ByteArrayInputStream serializado = new ByteArrayInputStream(response);
                ObjectInputStream is = new ObjectInputStream(serializado);
                serverResponse = (UnicastRequest)is.readObject();
                is.close();
                } catch (ClassNotFoundException e){e.printStackTrace();}
            } catch (IOException e){e.printStackTrace();}
        } catch (IOException e){e.printStackTrace();}
        return serverResponse;
    }
}
