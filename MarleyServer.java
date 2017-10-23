import java.io.*;

public class MarleyServer {
    public static void main(String[] args) throws IOException {
        MultiCastThreadRosa serverMulticast = new MultiCastThreadRosa();
        serverMulticast.terminal();
        serverMulticast.unicast();
        //serverMulticast.mensajeContinuo();
    }
}
