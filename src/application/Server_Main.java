package application;

import application.Data.PlayerData;
import application.socket.Server;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;

public class Server_Main{


    public static void main(String[] args) throws IOException {

             ServerSocket ss=new ServerSocket(1234);
             Server sv=new Server(ss);
             sv.Start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    PlayerData.writeData();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });


    }
}
