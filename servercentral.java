import java.io.*;
import java.net.*;
import java.util.*;

public class servercentral extends Thread {
    private List<Legion> clientes = new ArrayList<Legion>();
    private Legion clienteActual;
    private int nuevaID = 0;
    protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    protected boolean moreQuotes = true;
    protected int puerto;
    protected InetAddress addressCentral;
    protected List<ConexionMulticast> conexiones = new ArrayList<ConexionMulticast>();
    public servercentral() throws IOException {
        this("servercentral");
    }

    public servercentral(String name) throws IOException {
        super(name);
        int flag = 1;
        Scanner scanner = new Scanner(System.in);
        System.out.println("[Central] Ingresar IP Servidor Central: ");
        try{
            addressCentral = InetAddress.getByName(scanner.nextLine());
        } catch (UnknownHostException e) {e.printStackTrace();}

        System.out.println("[Central] Ingrese puerto de Servidor Central: ");
        puerto = scanner.nextInt();
        socket = new DatagramSocket(puerto, addressCentral);

        int opcion;
        while(flag == 1){
          System.out.println("Agregar Distrito?");
          System.out.println("[1] SI");
          System.out.println("[2] NO");
          opcion = scanner.nextInt();
          if(opcion == 1){
            ConexionMulticast nuevoDistrito = new ConexionMulticast();
            conexiones.add(nuevoDistrito);
          }
          else if(opcion == 2){
            flag = 0;
          }
        }
    }

    //Metodo ciclico del servidor central, estara constantemente escuchando a peticiones de otros equipos,
    //y aceptara o rechazara ingresos a nuevos distritos.
    public void run() {
        try{
          Scanner entrada = new Scanner(System.in);

          // Recibir Paquete
          while(true){
              int existe = 0;
              int existeservidor = 0;
              byte[] buf = new byte[256];
              DatagramPacket packet = new DatagramPacket(buf, buf.length);
              socket.receive(packet);
              String received = new String(packet.getData());

              // Manejo de autorización
              InetAddress ipCliente = packet.getAddress();
              int puertoCliente = packet.getPort();
              //Se determina la naturaleza del mensaje, idporfi implica que el servidor distrito quiere la ID del titan.
              if(received.replaceAll("\\P{Print}","").equals("idporfi")){
                  enviarID(ipCliente, puertoCliente);
              }

              //En caso contrario es un mensaje de un cliente, para ingresar a un distrito.
              //Se comprueba que cliente es el que quiere cambiar de distrito.
              else{
                  for(int j = 0; j < clientes.size(); j++){
                      clienteActual = clientes.get(j);
                      if(clienteActual.comprobarSoldado(ipCliente) == 1){
                          existe = 1;
                          break;
                      }
                  }
                  //Se comprueba si existe el servidor para cambiar.
                  for(int i = 0; i < conexiones.size(); i++){
                      if(conexiones.get(i).getNombre().replaceAll("\\P{Print}","").equals(received.replaceAll("\\P{Print}",""))){
                          existeservidor = 1;
                          break;
                      }
                  }
                  //SI el servidor existe entonces se procede a dar o no autorizacion para cambiar.
                  if(existeservidor == 1){
                      System.out.println("[Central] Dar autorización a " + ipCliente.toString()+ " al distrito " + received + "?");
                      System.out.println("[1] SI");
                      System.out.println("[2] NO");
                      int decision = entrada.nextInt();

                      //Se da permiso.
                      if(decision == 1){
                          //Se determina si el cliente ya esta registrado en la lista,
                          //En caso de no estarlo se ingresa.
                        if(existe == 0){
                            clienteActual = new Legion(received, ipCliente);
                            clientes.add(clienteActual);
                        }
                        //Se determina la ip del servidor distrito, para enviar la autorizacion al cliente.
                        for(int i = 0; i < conexiones.size(); i++){
                          if(conexiones.get(i).getNombre().replaceAll("\\P{Print}","").equals(received.replaceAll("\\P{Print}",""))){
                            try{
                              ByteArrayOutputStream serial = new ByteArrayOutputStream();
                              ObjectOutputStream os = new ObjectOutputStream(serial);
                              os.writeObject(conexiones.get(i));
                              os.close();
                              //Se cambia el distrito del usuario.
                              if(existe == 1){
                                  clienteActual.cambiarDistrito(received);
                              }
                              byte[] bufMsg = serial.toByteArray();
                              DatagramPacket packetResponse = new DatagramPacket(bufMsg, bufMsg.length, ipCliente, puertoCliente);
                              //Se responde con la informacion correspondiente.
                              try{
                                  socket.send(packetResponse);
                              } catch (IOException e){e.printStackTrace();}
                            }catch (IOException e){
                              e.printStackTrace();
                            }
                          }
                        }

                      }
                      //Se niega el ingreso y se enviar un objeto tipo ConexionMulticast con un puerto de valor -2
                      else if(decision == 2){
                          try{
                            ByteArrayOutputStream serial = new ByteArrayOutputStream();
                            ObjectOutputStream os = new ObjectOutputStream(serial);
                            os.writeObject(new ConexionMulticast(-2));
                            os.close();
                            byte[] bufMsg = serial.toByteArray();
                            DatagramPacket packetResponse = new DatagramPacket(bufMsg, bufMsg.length, ipCliente, puertoCliente);
                            try{
                                socket.send(packetResponse);
                            } catch (IOException e){e.printStackTrace();}
                          }catch (IOException e){
                            e.printStackTrace();
                          }
                      }
                  }
                  //El servidor al que se quiere ingresar no existe, por lo tanto se envia una respuesta con un puerto
                  //-1
                  else{
                      try{
                        ByteArrayOutputStream serial = new ByteArrayOutputStream();
                        ObjectOutputStream os = new ObjectOutputStream(serial);
                        os.writeObject(new ConexionMulticast(-1));
                        os.close();
                        byte[] bufMsg = serial.toByteArray();
                        DatagramPacket packetResponse = new DatagramPacket(bufMsg, bufMsg.length, ipCliente, puertoCliente);
                        try{
                            socket.send(packetResponse);
                        } catch (IOException e){e.printStackTrace();}
                      }catch (IOException e){
                        e.printStackTrace();
                      }
                  }
              }
          }

        } catch (IOException e){
          e.printStackTrace();
        }
        socket.close();
    }

    //Metodo de respuesta al servidor distrito, simplemente envia la nueva ID y aumenta el contador de este mismo,
    //para no repetirlo.
    public void enviarID(InetAddress ipserver, int puertoserver){
        String mensaje = Integer.toString(nuevaID);
        byte[] bufMsg = mensaje.getBytes();
        DatagramPacket packet = new DatagramPacket(bufMsg, bufMsg.length, ipserver, puertoserver);
        try{
            socket.send(packet);
        } catch (IOException e){e.printStackTrace();}
        nuevaID++;
    }
}
