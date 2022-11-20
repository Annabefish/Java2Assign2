package application;

import application.socket.Client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.*;
import java.util.Random;

public class Client_Main extends Application {
    public static String welcome="welcome.fxml";
    public static String Reg_Log="Reg_Log.fxml";
    public static String Setting="Settings.fxml";
    public static String Ready="Ready.fxml";
    public static String mainUI="mainUI.fxml";
    public static String Record="Record.fxml";
    public static Stage primary_stage;
    public static Client client;
    @Override

    public void start(Stage stage)  {

        try {
            FXMLLoader fxmlLoader_welcome = new FXMLLoader();

            fxmlLoader_welcome.setLocation(getClass().getClassLoader().getResource("welcome.fxml"));

            Pane welcome_pane = fxmlLoader_welcome.load();
//            BorderPane root=new BorderPane();
            Scene root = new Scene(welcome_pane);
            stage.setTitle("Welcome");
            stage.setScene(root);
            primary_stage=stage;

            stage.show();

//            ServerSocket serverSocket = new ServerSocket(1234);
        } catch (Exception e) {
            e.printStackTrace();
        }







    }
//    @Override
//    public void start(Stage primaryStage) {
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader();
//
//            fxmlLoader.setLocation(getClass().getClassLoader().getResource("mainUI.fxml"));
//            Pane root = fxmlLoader.load();
//            primaryStage.setTitle("Tic Tac Toe");
//            primaryStage.setScene(new Scene(root));
//            primaryStage.setResizable(false);
//            primaryStage.show();
////            ServerSocket serverSocket = new ServerSocket(1234);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void main(String[] args) throws IOException {

        client=new Client(1234);
        launch(args);
    }
}
