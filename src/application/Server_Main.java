package application;

import application.Data.PlayerData;
import application.socket.Server;
import java.io.IOException;
import java.net.ServerSocket;

public class Server_Main {


    public static void main(String[] args) throws IOException {

        ServerSocket ss = new ServerSocket(1234);
        Server sv = new Server(ss);
        sv.Start();
    }
}
