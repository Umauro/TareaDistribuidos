import java.io.*;
import java.net.*;
import java.util.*;

public class UnicastRequest implements Serializable{
  private int id;
  private String accion;
  public UnicastRequest(int ID, String toDo){
      id = ID;
      accion = toDo;
  }
  public int getId(){
    return id;
  }
  public String getAccion(){
    return accion;
  }
  public void setAccion(String toDo){
    accion = toDo;
  }
}
