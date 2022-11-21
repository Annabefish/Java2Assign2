package application.controller;

import application.Client_Main;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class Client implements Initializable {
    public class ClientThread implements Runnable {
        @Override
        public void run() {
            System.out.println("Thread entered");
            String str;
            try {
                str = Client_Main.client.getMessage();
                Platform.runLater(() -> {
                    try {
                        handleMessage(str);
                    } catch (IOException e) {
                    }
                });
//                    if (str == null || str.equals("") || str.startsWith("Loc") || str.startsWith("Start") || str.startsWith("Finish") || str.startsWith("GameOver"))
//                        break;

            } catch (IOException e) {
                Platform.runLater(() -> {
                try {
                    handleMessage("ServerDown\n");
                } catch (IOException ex) {
                }
                });
            }
            System.out.println("thread end");
        }

    }

    private static int player = -1;//0:player1 1:player2
    private static String name;
    private static String your_name;
    private static String my_avatar = "";
    private static String your_avatar = "";
    private static boolean log_or_reg = true;
    private static String my_chess;
    private static String your_chess;
    private boolean gameOver = false;
    private int tmp_chose_x = -1;
    private int tmp_chose_y = -1;
    private boolean startWaiting=false;
    private static int state=0;//0:welcome 1:reg/log 2:ready 3:mainUI 4 settings 5
    private Rectangle rect = new Rectangle();
    private static boolean TURN = false;//false:player1 true:player2
    private int[][] chessBoard = new int[3][3];
    private boolean[][] flag = new boolean[3][3];
    private String tmp_chess_num = "1";
    private String tmp_avatar = null;
    private static Thread t1=null;

    private static final int PLAY_1 = 1;
    private static final int PLAY_2 = 2;
    private static final int EMPTY = 0;
    private static final int BOUND = 90;
    private static final int OFFSET = 15;


    @FXML
    private Pane base_square;
    @FXML
    private MenuButton Name;
    @FXML
    private Rectangle game_panel;
    @FXML
    private PasswordField Password;
    @FXML
    private TextField Reg_Log_Text;
    @FXML
    private Label Waiting;
    @FXML
    private Label P1_waiting;
    @FXML
    private Label P2_waiting;
    @FXML
    private Button Login;
    @FXML
    private Button back;
    @FXML
    private Button Register;
    @FXML
    private Button Chess_Confirm;
    @FXML
    private Button Reg_Log_Button;
    @FXML
    private Label Reg_Log_Title;
    @FXML
    private Button avatar_set;
    @FXML
    private Button Back;
    @FXML
    private Button chess_set;
    @FXML
    private Button New_game;
    @FXML
    private MenuItem Settings;
    @FXML
    private MenuItem Log_out;
    @FXML
    private MenuItem Record;
    @FXML
    private Pane Chess_1_pane;
    @FXML
    private Pane Chess_2_pane;
    @FXML
    private Pane Chess_3_pane;
    @FXML
    private Pane Chess_4_pane;
    @FXML
    private Pane Avatar_pane;
    @FXML
    private Label Reg_Log_text;
    @FXML
    private Label Player2_Label;
    @FXML
    private Label Player1_Label;
    @FXML
    private ImageView P1_avatar;
    @FXML
    private ImageView P2_avatar;
    @FXML
    private ImageView Avatar_image;
    @FXML
    private Label Win;
    @FXML
    private Label Total;
    @FXML
    private Button Quit;
    @FXML
    private Button Reconnect;
    @FXML
    private Label Fail_Text;


    private void SwitchScene(String name, String title) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader().getResource(name));
        Pane pane;
        try {
            pane = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//            BorderPane root=new BorderPane();
        Scene root = new Scene(pane);
        Client_Main.primary_stage.setTitle(title);
        Client_Main.primary_stage.setScene(root);
        Client_Main.primary_stage.show();

    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(!location.toString().contains("welcome.fxml")&&!location.toString().contains("Settings.fxml")&&!location.toString().contains("ServerDown.fxml")){
            t1=new Thread(new ClientThread());
            t1.start();
        }
        if (location.toString().contains("welcome.fxml")) {
            state=0;
            Login.setOnMouseClicked(event -> {
                System.out.println("click login");
                //welcome->log
                log_or_reg = true;
                SwitchScene(Client_Main.Reg_Log, "log in");
            });
            Register.setOnMouseClicked(event -> {//welcome->reg
                System.out.println("click reg");
                log_or_reg = false;
                SwitchScene(Client_Main.Reg_Log, "register");

            });
        } else if (location.toString().contains("Reg_Log.fxml")) {
            state=1;
            if (log_or_reg) {
                System.out.println("log");
                Reg_Log_Title.setText("Log in");
                Reg_Log_Button.setText("Log");
            } else {
                System.out.println("reg");
                Reg_Log_Title.setText("Register");
                Reg_Log_Button.setText("Reg");
            }
            Reg_Log_Button.setOnMouseClicked(event -> {
                if(Reg_Log_text==null){
                    System.out.println("is null");
                }
                else{
                    System.out.println("not null");
                }
                name = Reg_Log_Text.getText();
                String tmp_password = Password.getText();
                System.out.println(name);
                System.out.println(tmp_password);
                if (log_or_reg)
                    Client_Main.client.sendMessage("Login\n" + name + "\n" + tmp_password + "\n");
                else
                    Client_Main.client.sendMessage("Register\n" + name + "\n" + tmp_password + "\n");
//                String get;
//                try {
//                    get = Client_Main.client.getMessage();
//                    handleMessage(get);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
                //log->ready
            });
            back.setOnMouseClicked(event -> {
                SwitchScene(Client_Main.welcome, "TIC-TAC-TOE");
            });
        } else if (location.toString().contains("Ready.fxml")) {
            state=2;
            Name.setText(name);
            New_game.setOnMouseClicked(event -> {
                if(startWaiting==false) {
                    startWaiting = true;
                    Waiting.setVisible(true);
                    Waiting.setText("Waiting...");
                    Client_Main.client.sendMessage("Start\n" + name + "\n" + my_avatar + "\n" + my_chess + "\n");
                }
//                new Thread(new ClientThread()).start();
//                try {
//                    s=Client_Main.client.getMessage();
//                    handleMessage(s);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
            });
            Settings.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    SwitchScene(Client_Main.Setting, "setting");
                }
            });

            Log_out.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Client_Main.client.sendMessage("LogOut\n" + name + "\n" + my_avatar + "\n" + my_chess + "\n");
                    SwitchScene(Client_Main.welcome, "welcome");
                    player = -1;
                    name = "";
                    my_avatar = "";
                    your_avatar = "";
                    log_or_reg = true;
                    my_chess = "";
                    your_chess = "";
                    tmp_chose_x = -1;
                    tmp_chose_y = -1;
                    TURN = false;
                    chessBoard = new int[3][3];
                    flag = new boolean[3][3];
                }
            });

            Record.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Client_Main.client.sendMessage("CloseThread\nRecord\n");
                    SwitchScene(Client_Main.Record, "Record");
                }
            });
//            Data.setOnMouseClicked(event -> {
//                //data shoe
//            });
        } else if (location.toString().contains("mainUI.fxml")) {
            state=3;
            P1_waiting.setText("Waiting");
            P2_waiting.setText("Waiting");
            System.out.println("my name:" + name);
            System.out.println("your name:" + your_name);
            if (player == 0) {
                Player1_Label.setText(name);
                if (my_avatar != null && !my_avatar.equals("")) {
                    P1_avatar.setImage(new Image(my_avatar));
                }
                Player2_Label.setText(your_name);
                if (your_avatar != null && !your_avatar.equals("")) {
                    P2_avatar.setImage(new Image(your_avatar));
                }
            } else {
                Player2_Label.setText(name);
                if (my_avatar != null && !my_avatar.equals("")) {
                    P2_avatar.setImage(new Image(my_avatar));
                }
                Player1_Label.setText(your_name);
                if (your_avatar != null && !your_avatar.equals("")) {
                    P1_avatar.setImage(new Image(your_avatar));
                }
            }
            showWaiting();
            game_panel.setOnMouseClicked(event -> {
                if ((player == 0 && !TURN) || (player == 1 && TURN)) {
                    int x = (int) (event.getX() / BOUND);
                    int y = (int) (event.getY() / BOUND);
                    if (chessBoard[x][y] == 0) {
                        if (tmp_chose_y != -1 && tmp_chose_x != -1) {
                            deleteSquare();
                        }
                        drawSquare(x, y);
                        tmp_chose_x = x;
                        tmp_chose_y = y;
                    }
                }
            });
            Chess_Confirm.setOnMouseClicked(event -> {
                if (!gameOver) {
                    if(tmp_chose_x!=-1&&tmp_chose_y!=-1) {
                        Client_Main.client.sendMessage("Loc\n" + intToString(tmp_chose_x) + " " + intToString(tmp_chose_y) + "\n");
                        if ((player == 0 && !TURN) || (player == 1 && TURN)) {
                            if (refreshBoard(tmp_chose_x, tmp_chose_y)) {
                                deleteSquare();
                                tmp_chose_x = -1;
                                tmp_chose_y = -1;
                                TURN = !TURN;
                                showWaiting();
                            }
                        }
                    }
                } else {
                    Client_Main.client.sendMessage("GameOver\n");
                    SwitchScene(Client_Main.Ready, "Ready");
                }

            });
            Quit.setOnMouseClicked(event -> {
                Client_Main.client.sendMessage("Quit\n");
            });
        } else if (location.toString().contains("Settings.fxml")) {
            state=4;
            if (my_chess != null)
                tmp_chess_num = my_chess;
            else
                tmp_chess_num = "1";
            chooseChess();
            if (my_avatar != null && !my_avatar.equals("")) {
                Avatar_image.setImage(new Image(my_avatar));
            }
            Avatar_pane.setOnMouseClicked(event -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("PNG Files", "*.PNG")
                        , new FileChooser.ExtensionFilter("JPG Files", "*.JPG")
                );
                fileChooser.setInitialDirectory(new File("D:\\lsy\\java2\\lab\\A2\\Tic-tac-toe\\resources\\image"));
                File selectedFile = fileChooser.showOpenDialog(Client_Main.primary_stage);
                if (selectedFile != null) {
                    Image tmp_image = new Image("file:" + selectedFile.getPath());
                    Avatar_pane.getChildren().remove(Avatar_image);
                    Avatar_image = new ImageView(tmp_image);
                    Avatar_image.setImage(tmp_image);
                    Avatar_image.setFitWidth(140);
                    Avatar_image.setFitHeight(140);
                    Avatar_pane.getChildren().add(Avatar_image);
                    tmp_avatar = "file:" + selectedFile.getPath();
                    System.out.println(tmp_avatar);
                }
            });
            avatar_set.setOnMouseClicked(event -> {
                my_avatar = tmp_avatar;
            });
            Chess_1_pane.setOnMouseClicked(event -> {
                if (!tmp_chess_num.equals("1")) {
                    dontChooseChess();
                    tmp_chess_num = "1";
                    chooseChess();
                }
            });
            Chess_2_pane.setOnMouseClicked(event -> {
                if (!tmp_chess_num.equals("2")) {
                    dontChooseChess();
                    tmp_chess_num = "2";
                    chooseChess();
                }
            });
            Chess_3_pane.setOnMouseClicked(event -> {
                if (!tmp_chess_num.equals("3")) {
                    dontChooseChess();
                    tmp_chess_num = "3";
                    chooseChess();
                }
            });
            Chess_4_pane.setOnMouseClicked(event -> {
                if (!tmp_chess_num.equals("4")) {
                    dontChooseChess();
                    tmp_chess_num = "4";
                    chooseChess();
                }
            });

            chess_set.setOnMouseClicked(event -> {
                my_chess = tmp_chess_num;
            });
            Back.setOnMouseClicked(event -> {
                SwitchScene(Client_Main.Ready, "Ready");
            });
        } else if (location.toString().contains("Record.fxml")) {
            state=5;
            Back.setOnMouseClicked(event -> {
                SwitchScene(Client_Main.Ready, "Ready");
            });
            Client_Main.client.sendMessage("Record\n");
//            String s;
//            try {E
//                s = Client_Main.client.getMessage();
//                handleMessage(s);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
        }else if (location.toString().contains("ServerDown.fxml")) {
            Fail_Text.setVisible(false);
            Reconnect.setOnMouseClicked(event -> {
                try {
                    Client_Main.client=new application.socket.Client(1234);
                    SwitchScene(Client_Main.welcome,"welcome");
                } catch (IOException e) {
                    Fail_Text.setVisible(true);
                }
            });

        }
    }

//
//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//        if(t1==null){
//
//            t1=new Thread(new ClientThread());
//            t1.start();
//        }
//        if (location.toString().contains("welcome.fxml")) {
//            state=0;
//            Login.setOnMouseClicked(event -> {
////                System.out.println("click login");
//                //welcome->log
//                log_or_reg = true;
//                SwitchScene(Client_Main.Reg_Log, "log in");
//            });
//            Register.setOnMouseClicked(event -> {//welcome->reg
////                System.out.println("click reg");
//                log_or_reg = false;
//                SwitchScene(Client_Main.Reg_Log, "register");
//
//            });
//        }
//        else if (location.toString().contains("Reg_Log.fxml")) {
//            state=1;
//            if (log_or_reg) {
////                System.out.println("log");
//                Reg_Log_Title.setText("Log in");
//                Reg_Log_Button.setText("Log");
//            } else {
////                System.out.println("reg");
//                Reg_Log_Title.setText("Register");
//                Reg_Log_Button.setText("Reg");
//            }
//            Reg_Log_Button.setOnMouseClicked(event -> {
//                name = Reg_Log_Text.getText();
//                Client_Main.name=name;
//                String tmp_password = Password.getText();
////                System.out.println(name);
////                System.out.println(tmp_password);
//                if (log_or_reg)
//                    Client_Main.client.sendMessage("Login\n" + name + "\n" + tmp_password + "\n");
//                else
//                    Client_Main.client.sendMessage("Register\n" + name + "\n" + tmp_password + "\n");
//
//            });
//            back.setOnMouseClicked(event -> {
//
//                SwitchScene(Client_Main.welcome, "TIC-TAC-TOE");
//            });
//        }
//        else if (location.toString().contains("Ready.fxml")) {
//            state=2;//0:welcome 1:reg/log 2:ready 3:mainUI 4 settings 5
//            Name.setText(name);
//            New_game.setOnMouseClicked(event -> {
//                Waiting.setVisible(true);
//                Waiting.setText("Waiting...");
//                Client_Main.client.sendMessage("Start\n" + name + "\n" + my_avatar + "\n" + my_chess + "\n");
//            });
//            Settings.setOnAction(new EventHandler<ActionEvent>() {
//                @Override
//                public void handle(ActionEvent event) {
//                    SwitchScene(Client_Main.Setting, "setting");
//                }
//            });
//
//            Log_out.setOnAction(new EventHandler<ActionEvent>() {
//                @Override
//                public void handle(ActionEvent event) {
//                    Client_Main.client.sendMessage("LogOut\n" + name + "\n" + my_avatar + "\n" + my_chess + "\n");
//                    SwitchScene(Client_Main.welcome, "welcome");
//                    player = -1;
//                    name = "";
//                    Client_Main.name=name;
//                    my_avatar = "";
//                    your_avatar = "";
//                    log_or_reg = true;
//                    my_chess = "";
//                    your_chess = "";
//                    tmp_chose_x = -1;
//                    tmp_chose_y = -1;
//                    TURN = false;
//                    chessBoard = new int[3][3];
//                    flag = new boolean[3][3];
//                }
//            });
//
//            Record.setOnAction(new EventHandler<ActionEvent>() {
//                @Override
//                public void handle(ActionEvent event) {
//
//                    SwitchScene(Client_Main.Record, "Record");
//                }
//            });
////            Data.setOnMouseClicked(event -> {
////                //data shoe
////            });
//        }
//        else if (location.toString().contains("mainUI.fxml")) {
//           state=3;//0:welcome 1:reg/log 2:ready 3:mainUI 4 settings 5
//            P1_waiting.setText("Waiting");
//            P2_waiting.setText("Waiting");
////            System.out.println("my name:" + name);
////            System.out.println("your name:" + your_name);
//            if (player == 0) {
//                Player1_Label.setText(name);
//                if (my_avatar != null && !my_avatar.equals("")) {
//                    P1_avatar.setImage(new Image(my_avatar));
//                    P1_avatar.setFitHeight(140);
//                    P1_avatar.setFitWidth(140);
//                }
//                Player2_Label.setText(your_name);
//                if (your_avatar != null && !your_avatar.equals("")) {
//                    P2_avatar.setImage(new Image(your_avatar));
//                    P2_avatar.setFitHeight(140);
//                    P2_avatar.setFitWidth(140);
//                }
//            } else {
//                Player2_Label.setText(name);
//                if (my_avatar != null && !my_avatar.equals("")) {
//                    P2_avatar.setImage(new Image(my_avatar));
//                }
//                Player1_Label.setText(your_name);
//                if (your_avatar != null && !your_avatar.equals("")) {
//                    P1_avatar.setImage(new Image(your_avatar));
//                }
//            }
//            showWaiting();
//            game_panel.setOnMouseClicked(event -> {
//                if ((player == 0 && !TURN) || (player == 1 && TURN)) {
//                    int x = (int) (event.getX() / BOUND);
//                    int y = (int) (event.getY() / BOUND);
//                    if (chessBoard[x][y] == 0) {
//                        if (tmp_chose_y != -1 && tmp_chose_x != -1) {
//                            deleteSquare();
//                        }
//                        drawSquare(x, y);
//                        tmp_chose_x = x;
//                        tmp_chose_y = y;
//                    }
//                }
//            });
//            Chess_Confirm.setOnMouseClicked(event -> {
//                if (!gameOver) {
//                    if((player==0&&!TURN )||(player==1&&TURN)) {
//                        Client_Main.client.sendMessage("Loc\n" + intToString(tmp_chose_x) + " " + intToString(tmp_chose_y) + "\n");
//                        if ((player == 0 && !TURN) || (player == 1 && TURN)) {
//                            if (refreshBoard(tmp_chose_x, tmp_chose_y)) {
//                                deleteSquare();
//                                tmp_chose_x = -1;
//                                tmp_chose_y = -1;
//                                TURN = !TURN;
//                                showWaiting();
//                            }
//                        }
//                    }
//                } else {
//                    Client_Main.client.sendMessage("GameOver\n");
//                    SwitchScene(Client_Main.Ready, "Ready");
//                }
//
//            });
//            Quit.setOnMouseClicked(event -> {
//                Client_Main.client.sendMessage("Quit\n");
//                SwitchScene(Client_Main.Ready,"Ready");
//            });
//        }
//        else if (location.toString().contains("Settings.fxml")) {
//            state=4;
//            if (my_chess != null)
//                tmp_chess_num = my_chess;
//            else
//                tmp_chess_num = "1";
//            chooseChess();
//            if (my_avatar != null && !my_avatar.equals("") && !my_avatar.equals("\"\"")) {
//                Avatar_image.setImage(new Image(my_avatar));
//            }
//            Avatar_pane.setOnMouseClicked(event -> {
//                FileChooser fileChooser = new FileChooser();
//                fileChooser.getExtensionFilters().addAll(
//                        new FileChooser.ExtensionFilter("PNG Files", "*.PNG")
//                        , new FileChooser.ExtensionFilter("JPG Files", "*.JPG")
//                );
//                fileChooser.setInitialDirectory(new File("D:\\lsy\\java2\\lab\\A2\\Tic-tac-toe\\resources\\image"));
//                File selectedFile = fileChooser.showOpenDialog(Client_Main.primary_stage);
//                if (selectedFile != null) {
//                    Image tmp_image = new Image("file:" + selectedFile.getPath());
//                    Avatar_pane.getChildren().remove(Avatar_image);
//                    Avatar_image = new ImageView(tmp_image);
//                    Avatar_image.setImage(tmp_image);
//                    Avatar_image.setFitWidth(140);
//                    Avatar_image.setFitHeight(140);
//                    Avatar_pane.getChildren().add(Avatar_image);
//                    tmp_avatar = "file:" + selectedFile.getPath();
////                    System.out.println(tmp_avatar);
//                }
//            });
//            avatar_set.setOnMouseClicked(event -> {
//                my_avatar = tmp_avatar;
//            });
//            Chess_1_pane.setOnMouseClicked(event -> {
//                if (!tmp_chess_num.equals("1")) {
//                    dontChooseChess();
//                    tmp_chess_num = "1";
//                    chooseChess();
//                }
//            });
//            Chess_2_pane.setOnMouseClicked(event -> {
//                if (!tmp_chess_num.equals("2")) {
//                    dontChooseChess();
//                    tmp_chess_num = "2";
//                    chooseChess();
//                }
//            });
//            Chess_3_pane.setOnMouseClicked(event -> {
//                if (!tmp_chess_num.equals("3")) {
//                    dontChooseChess();
//                    tmp_chess_num = "3";
//                    chooseChess();
//                }
//            });
//            Chess_4_pane.setOnMouseClicked(event -> {
//                if (!tmp_chess_num.equals("4")) {
//                    dontChooseChess();
//                    tmp_chess_num = "4";
//                    chooseChess();
//                }
//            });
//
//            chess_set.setOnMouseClicked(event -> {
//                my_chess = tmp_chess_num;
//            });
//            Back.setOnMouseClicked(event -> {
//
//                SwitchScene(Client_Main.Ready, "Ready");
//            });
//        }
//        else if (location.toString().contains("Record.fxml")) {
//            state=5;//0:welcome 1:reg/log 2:ready 3:mainUI 4 settings 5
//            Back.setOnMouseClicked(event -> {
//                SwitchScene(Client_Main.Ready, "Ready");
//            });
//            Client_Main.client.sendMessage("Record\n");
//            String s;
////            try {
////                s = Client_Main.client.getMessage();
////                handleMessage(s);
////            } catch (IOException e) {
////                throw new RuntimeException(e);
////            }
//        }
//        else if (location.toString().contains("ServerDown.fxml")) {
//            Fail_Text.setVisible(false);
//            Reconnect.setOnMouseClicked(event -> {
//                try {
//                    Client_Main.client=new application.socket.Client(1234);
//                    SwitchScene(Client_Main.welcome,"welcome");
//                } catch (IOException e) {
//                    Fail_Text.setVisible(true);
//                }
//            });
//
//        }
//    }

    private void showWaiting() {
        if (TURN) {
            P2_waiting.setVisible(true);
            P1_waiting.setVisible(false);
        } else {
            P2_waiting.setVisible(false);
            P1_waiting.setVisible(true);
        }
    }

    private void chooseChess() {
        switch (tmp_chess_num) {
            case "1":
                Chess_1_pane.setStyle("-fx-background-color: #ffefdd");
                break;
            case "2":
                Chess_2_pane.setStyle("-fx-background-color: #ffefdd");
                break;
            case "3":
                Chess_3_pane.setStyle("-fx-background-color: #ffefdd");
                break;
            case "4":
                Chess_4_pane.setStyle("-fx-background-color: #ffefdd");
                break;
        }
    }

    private void dontChooseChess() {
        switch (tmp_chess_num) {
            case "1":
                Chess_1_pane.setStyle("-fx-background-color: #ffefff");
                break;
            case "2":
                Chess_2_pane.setStyle("-fx-background-color: #ffefff");
                break;
            case "3":
                Chess_3_pane.setStyle("-fx-background-color: #ffefff");
                break;
            case "4":
                Chess_4_pane.setStyle("-fx-background-color: #ffefff");
                break;
        }
    }

    private void deleteSquare() {
        base_square.getChildren().remove(rect);
    }

    private void drawSquare(int x, int y) {
        base_square.getChildren().add(rect);
        rect.setX(x * BOUND + OFFSET);
        rect.setY(y * BOUND + OFFSET);
        rect.setHeight(BOUND);
        rect.setWidth(BOUND);
        rect.setStroke(Color.CYAN);
        rect.setFill(Color.TRANSPARENT);
    }


    private String intToString(int x) {
        switch (x) {
            case 1:
                return "1";
            case 2:
                return "2";
            case 3:
                return "3";
            case 4:
                return "4";
            case 5:
                return "5";
            case 6:
                return "6";
            case 7:
                return "7";
            case 8:
                return "8";
            case 9:
                return "9";
            case 0:
                return "0";
        }
        return null;
    }

    private boolean refreshBoard(int x, int y) {
        if (chessBoard[x][y] == EMPTY) {
            chessBoard[x][y] = TURN ? PLAY_1 : PLAY_2;
            drawChess(chessBoard[x][y] - 1, x, y);
            return true;
        }
        return false;
    }

    private void showGameOver(int i) {
        Chess_Confirm.setText("Back");
        gameOver = true;
        P1_waiting.setVisible(true);
        P2_waiting.setVisible(true);
        if (i == 1) {
            if (player == 0) {
                P1_waiting.setText("Win");
                P2_waiting.setText("Lose");
            }
            if (player == 1) {
                P2_waiting.setText("Win");
                P1_waiting.setText("Lose");
            }
        } else {
            if (i == 2) {
                if (player == 0) {
                    P2_waiting.setText("Win");
                    P1_waiting.setText("Lose");
                }
                if (player == 1) {
                    P1_waiting.setText("Win");
                    P2_waiting.setText("Lose");
                }
            } else {
                P2_waiting.setText("Even");
                P1_waiting.setText("Even");
            }
        }
    }

    private void drawChess(int new_player, int i, int j) {
        System.out.println("Draw Chess");
        Image image;
        if (new_player == player) {
            image = new Image("file:.//resources//image//Chess//" + your_chess + "_" + intToString(2 - player) + ".PNG");
        } else {
            image = new Image("file:.//resources//image//Chess//" + my_chess + "_" + intToString(player + 1) + ".PNG");
        }
        ImageView iv = new ImageView(image);
        base_square.getChildren().add(iv);
        iv.setFitHeight(BOUND);
        iv.setFitWidth(BOUND);
        iv.setX(i * BOUND + OFFSET);
        iv.setY(j * BOUND + OFFSET);
//        for (int i = 0; i < chessBoard.length; i++) {
//            for (int j = 0; j < chessBoard[0].length; j++) {
//                if (flag[i][j]) {
//                    // This square has been drawing, ignore.
//                    continue;
//                }
//                switch (chessBoard[i][j]) {
//                    case PLAY_1:
//                        drawCircle(i, j);
//                        break;
//                    case PLAY_2:
//                        drawLine(i, j);
//                        break;
//                    case EMPTY:
//                        // do nothing
//                        break;
//                    default:
//                        System.err.println("Invalid value!");
//                }
//            }
//        }
    }

    //    private void drawCircle (int i, int j) {
//        Circle circle = new Circle();
//        base_square.getChildren().add(circle);
//        circle.setCenterX(i * BOUND + BOUND / 2.0 + OFFSET);
//        circle.setCenterY(j * BOUND + BOUND / 2.0 + OFFSET);
//        circle.setRadius(BOUND / 2.0 - OFFSET / 2.0);
//        circle.setStroke(Color.RED);
//        circle.setFill(Color.TRANSPARENT);
//        flag[i][j] = true;
//    }
//
//    private void drawLine (int i, int j) {
//        Line line_a = new Line();
//        Line line_b = new Line();
//        base_square.getChildren().add(line_a);
//        base_square.getChildren().add(line_b);
//        line_a.setStartX(i * BOUND + OFFSET * 1.5);
//        line_a.setStartY(j * BOUND + OFFSET * 1.5);
//        line_a.setEndX((i + 1) * BOUND + OFFSET * 0.5);
//        line_a.setEndY((j + 1) * BOUND + OFFSET * 0.5);
//        line_a.setStroke(Color.BLUE);
//
//        line_b.setStartX((i + 1) * BOUND + OFFSET * 0.5);
//        line_b.setStartY(j * BOUND + OFFSET * 1.5);
//        line_b.setEndX(i * BOUND + OFFSET * 1.5);
//        line_b.setEndY((j + 1) * BOUND + OFFSET * 0.5);
//        line_b.setStroke(Color.BLUE);
//        flag[i][j] = true;
//    }
    private boolean handleMessage(String message) throws IOException {
        System.out.println(state);
        String[] array = message.split("\n");//0:welcome 1:reg/log 2:ready 3:mainUI 4 settings 5
        if(state==3) {
            if (array[0].contains("Loc")) {
                String[] title = array[0].split(" ");
                System.out.println("location");
                String[] loc = array[1].split(" ");
                int row = Integer.parseInt(loc[0]);
                int col = Integer.parseInt(loc[1]);
//                deleteSquare();
                refreshBoard(row, col);
                TURN = !TURN;
                if (title.length > 1) {
                    if (title[1].equals("Win")) {
                        showGameOver(1);
                    } else {
                        if (title[1].equals("Lose")) {
                            showGameOver(2);
                        } else {
                            showGameOver(0);
                        }
                    }
                    return false;
                } else {
                    showWaiting();
                }
            }
            if (array[0].equals("GameOver")) {
                System.out.println("GameOver");
                System.out.println(array[1]);
                if (array[1].equals("win")) {
                    showGameOver(1);
                } else if (array[1].equals("lose")) {
                    showGameOver(2);
                } else {
                    showGameOver(0);
                }
                TURN = false;
                return true;
            }
            if (array[0].equals("Finish")) {
                if(array[1].equals("1")){
                    System.out.println("p2 Quit");
                    P2_waiting.setText("Quit");
                    P2_waiting.setVisible(true);
                    P1_waiting.setVisible(false);
                    gameOver = true;
                } else {
                    System.out.println("p1 Quit");
                    P1_waiting.setText("Quit");
                    P1_waiting.setVisible(true);
                    P2_waiting.setVisible(false);
                    gameOver = true;
                }
                Chess_Confirm.setText("Back");
                return true;
            }
        }
        if(state==2) {
            if (array[0].equals("Start")) {
                startWaiting=false;
                player = Integer.parseInt(array[1]);
                System.out.println("I'm player " + array[1]);
                your_name = array[2];
                your_avatar = array[3];
                your_chess = array[4];
                TURN = false;
                SwitchScene(Client_Main.mainUI, "TIC-TAC-TOE");
                return false;
            }
        }
        if(state==1) {
            if (array[0].equals("RegWrong")) {
                Reg_Log_text.setText("Name Exist, Please change another one");
            }
            if (array[0].equals("LogWrong")) {
                Reg_Log_text.setText("Password or Userword wrong, Please enter again");
            }

            if (array[0].equals("LogIn")) {
                System.out.println("LogIn");
                name = array[1];
                Client_Main.name=name;
                my_avatar = array[2];
                my_chess = array[3];

                SwitchScene(Client_Main.Ready, "Ready");
                return false;
            }
            if (array[0].equals("Logined")) {
                Reg_Log_text.setText("This account has been logged in.");
            }

        }
        if(state==5) {
            if (array[0].equals("Record")) {
                Win.setText(array[1]);
                Total.setText(array[2]);
                return false;
            }
        }
        if (array[0].equals("ServerDown")) {
            SwitchScene(Client_Main.ServerDown,"NOOOO!");
            return true;
        }
        if (array[0].equals("CloseThread")) {
            if(array[1].equals("Record")){
                SwitchScene(Client_Main.Record,"Record");
            }
            return true;
        }

        t1=new Thread(new ClientThread());
        t1.start();
        return false;

    }


}
