package application.controller;

import application.Client_Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private static final int PLAY_1 = 1;
    private static final int PLAY_2 = 2;
    private static final int EMPTY = 0;
    private static final int BOUND = 90;
    private static final int OFFSET = 15;

    @FXML
    private Button Login;
    @FXML
    private Button Register;

    @FXML
    private Button Reg_Log_Button;

    @FXML
    private Button Reg_Log_Title;

    @FXML
    private Pane base_square;

    @FXML
    private Rectangle game_panel;

    private static boolean TURN = false;

    private static final int[][] chessBoard = new int[3][3];
    private static final boolean[][] flag = new boolean[3][3];

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        Login.setOnMouseClicked(event -> {
//            System.out.println("click login");
//            //welcome->log
//            try {
//                Client_Main.root=Client_Main.fxmlLoader_Reg_Log.load();
//                Reg_Log_Title.setText("Log in");
//                Reg_Log_Button.setText("Log");
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            Scene scene = new Scene(Client_Main.root);
//            Client_Main.primary_stage.setTitle("Log");
//            Client_Main.primary_stage.setScene(scene);
//            Client_Main.primary_stage.show();
//
//        });
//
//        Register.setOnMouseClicked(event -> {//welcome->reg
//            System.out.println("click login");
//            try {
//                Client_Main.root=Client_Main.fxmlLoader_Reg_Log.load();
//                Reg_Log_Title.setText("Register");
//                Reg_Log_Button.setText("Reg");
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            Scene scene = new Scene(Client_Main.root);
//            Client_Main.primary_stage.setTitle("Reg");
//            Client_Main.primary_stage.setScene(scene);
//            Client_Main.primary_stage.show();
//
//        });
        //        game_panel.setOnMouseClicked(event -> {
//            int x = (int) (event.getX() / BOUND);
//            int y = (int) (event.getY() / BOUND);
//            if (refreshBoard(x, y)) {
//                TURN = !TURN;
//            }
//        });
    }

    private boolean refreshBoard (int x, int y) {
        if (chessBoard[x][y] == EMPTY) {
            chessBoard[x][y] = TURN ? PLAY_1 : PLAY_2;
            drawChess();
            return true;
        }
        return false;
    }

    private void drawChess () {
        for (int i = 0; i < chessBoard.length; i++) {
            for (int j = 0; j < chessBoard[0].length; j++) {
                if (flag[i][j]) {
                    // This square has been drawing, ignore.
                    continue;
                }
                switch (chessBoard[i][j]) {
                    case PLAY_1:
                        drawCircle(i, j);
                        break;
                    case PLAY_2:
                        drawLine(i, j);
                        break;
                    case EMPTY:
                        // do nothing
                        break;
                    default:
                        System.err.println("Invalid value!");
                }
            }
        }
    }

    private void drawCircle (int i, int j) {
        Circle circle = new Circle();
        base_square.getChildren().add(circle);
        circle.setCenterX(i * BOUND + BOUND / 2.0 + OFFSET);
        circle.setCenterY(j * BOUND + BOUND / 2.0 + OFFSET);
        circle.setRadius(BOUND / 2.0 - OFFSET / 2.0);
        circle.setStroke(Color.RED);
        circle.setFill(Color.TRANSPARENT);
        flag[i][j] = true;
    }

    private void drawLine (int i, int j) {
        Line line_a = new Line();
        Line line_b = new Line();
        base_square.getChildren().add(line_a);
        base_square.getChildren().add(line_b);
        line_a.setStartX(i * BOUND + OFFSET * 1.5);
        line_a.setStartY(j * BOUND + OFFSET * 1.5);
        line_a.setEndX((i + 1) * BOUND + OFFSET * 0.5);
        line_a.setEndY((j + 1) * BOUND + OFFSET * 0.5);
        line_a.setStroke(Color.BLUE);

        line_b.setStartX((i + 1) * BOUND + OFFSET * 0.5);
        line_b.setStartY(j * BOUND + OFFSET * 1.5);
        line_b.setEndX(i * BOUND + OFFSET * 1.5);
        line_b.setEndY((j + 1) * BOUND + OFFSET * 0.5);
        line_b.setStroke(Color.BLUE);
        flag[i][j] = true;
    }
}
