package application.Stages;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class Welcome extends Application {
    public void start(Stage stage)  {

        try {
            Image image = new Image("file:resources//image//example.jpg");
            ImageView iv=new ImageView();
            iv.setImage(image);
            double width=image.getWidth();
            double height=image.getHeight();

            iv.setFitWidth(width*0.5);
            iv.setFitHeight(height*0.5);

            FXMLLoader fxmlLoader = new FXMLLoader();

            fxmlLoader.setLocation(getClass().getClassLoader().getResource("welcome.fxml"));
            Pane root = fxmlLoader.load();
            Pane another=new Pane(iv,root);

//            BorderPane root=new BorderPane();
            Scene scene = new Scene(another,width*0.5,height*0.5);
            stage.setTitle("FXML Welcome");

            stage.setScene(scene);

            stage.show();

//            ServerSocket serverSocket = new ServerSocket(1234);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)  {
        launch(args);
    }

}