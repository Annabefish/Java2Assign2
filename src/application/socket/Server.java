package application.socket;

import application.Data.PlayerData;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    class ServerThread implements Runnable {
        private InputStream is;
        private OutputStream os;
        private Socket client;
        private Map<String,String> map;

        private String my_str;
        public ServerThread(Socket client) throws IOException {

            this.client=client;
            is= client.getInputStream();
            os=client.getOutputStream();
        }
        public ServerThread(Socket client,Map<String,String> map) throws IOException {

            this.client=client;
            is= client.getInputStream();
            os=client.getOutputStream();
            this.map=map;
        }
        @Override
        public void run(){
            System.out.println("Server thread start");
            while (true) {
                String s= null;
                try {
                    s = getMessage(is);
                } catch (IOException e) {
                    break;
                }
                if(s==null||handleMessage(s))
                    break;
            }
            System.out.println("thread end");
        }
        public boolean handleMessage(String s){
            String[] array=s.split("\n");
            if(array[0].equals("Start")){
                if(waiting==null||waiting.equals(client)){
                    waiting=client;
                    p1_str=array[1]+"\n"+array[2]+"\n"+array[3];
                    p1_map=map;
                    System.out.println("p1_str: "+p1_str);

                }
                else{
                    int key=checkMapKey();
                    second_data=array[1]+"\n"+array[2]+"\n"+array[3];
                    startNewGame(key,client);
                    waiting=null;
                    System.out.println("before GameThread:"+p1_str+array[1]+"\n"+array[2]+"\n"+array[3]);
                    Runnable thread=new GameThread(key,p1_str,array[1]+"\n"+array[2]+"\n"+array[3],p1_map,map);
                    new Thread(thread).start();

                }
                return true;
            }
            else if(array[0].equals("Login")){
                String name=array[1];
                String password=array[2];
                map=checkLogin(name,password);
                //check name and password
                if(map!=null){
                    //return basic information
                    my_str=map.get("name")+"\n"+map.get("avatar")+"\n"+map.get("chess");
                    System.out.println("this thread:"+my_str);
                    sendMessage(os,"LogIn\n"+my_str+"\n");
                }
                else{
                    sendMessage(os,"LogWrong\n");
                }
            }
            else if(array[0].equals("Record")){
                String total=map.get("total");
                String win=map.get("win");
                sendMessage(os,"Record\n"+win+"\n"+total+"\n");

                //return win number
            }
            else if(array[0].equals("Register")){
                System.out.println("Handle reg");
                String name=array[1];
                String password=array[2];
                if(checkReg(name,password)){
                    map=new HashMap<>();
                    map.put("name",name);
                    map.put("password",password);
                    map.put("win","0");
                    map.put("total","0");
                    map.put("avatar","");
                    map.put("chess","1");
                    PlayerData.data_map.add(map);
                    sendMessage(os,"LogIn\n"+map.get("name")+"\n"+map.get("avatar")+"\n"+map.get("chess")+"\n");
                }
                else{
                    sendMessage(os,"RegWrong\n");
                }

                //name exist
                //store

            }
            else if(array[0].equals("LogOut")) {
                String name = array[1];
                String my_avatar = array[2];
                String my_chess = array[3];
                map.put("name", name);
                map.put("avatar", my_avatar);
                map.put("chess", my_chess);
            }
            return false;
        }
        private Map<String,String> checkLogin(String name,String password){
            for(int i =0; i<PlayerData.data_map.size();i++) {
                Map<String,String> map=PlayerData.data_map.get(i);
                if(map.get("name").equals(name)&&map.get("password").equals(password)){
                    return map;
                }
            }
            return null;
        }
        private boolean checkReg(String name, String password){
            for(int i =0; i<PlayerData.data_map.size();i++) {
                Map<String,String> map=PlayerData.data_map.get(i);
                if(map.get("name").equals(name)){
                    return false;
                }
            }
            return true;
        }

    }


    class GameThread implements Runnable{
        private int turn;
        private int[][] board=new int[3][3];
        private InputStream inputStream_player1=null,inputStream_player2=null;
        private OutputStream outputStream_player1=null,outputStream_player2=null;
        private String p1,p2;
        private Map<String,String> map1,map2;

        String location=null;
        public GameThread(int turn,String p1,String p2,Map<String,String> map1,Map<String,String> map2){
            this.turn=turn;
            this.p1=p1;
            this.p2=p2;
            this.map1=map1;
            this.map2=map2;
        }

        public void run(){

            int round=0;
            try {
                inputStream_player1=players.get(turn)[0].getInputStream();
                inputStream_player2=players.get(turn)[1].getInputStream();
                outputStream_player1=players.get(turn)[0].getOutputStream();
                outputStream_player2=players.get(turn)[1].getOutputStream();
                sendMessage(outputStream_player1,"Start\n0\n"+p2+"\n");
                sendMessage(outputStream_player2,"Start\n1\n"+p1+"\n");
            } catch (IOException e) {

            }

            while(true){
                if(round%2==0){
                        try {
                            System.out.println("------------");
                            String s=getMessage(inputStream_player1);
                            System.out.println("get Message");
                            handleMessage(s,round%2);
                        }
                        catch (IOException e) {
                            sendMessage(outputStream_player2,"Finish\n");
                            try {
                                Runnable serverThread=new ServerThread(players.get(turn)[1]);
                                new Thread(serverThread).start();

                            } catch (IOException ex) {
                            }
                            break;

                        }
                }else{
                    try {
                        String s = getMessage(inputStream_player2);
                        System.out.println("get Message");
                        handleMessage(s,round%2);
                    } catch (IOException e) {
                        sendMessage(outputStream_player1,"Finish\n");
                        try {
                            Runnable serverThread=new ServerThread(players.get(turn)[0]);
                            new Thread(serverThread).start();

                        } catch (IOException ex) {
                        }
                        break;
                    }
                }
                System.out.printf("round: %d\n",round);
                if(judgeWinner(round%2)||round==8){
                    if(!judgeWinner(round%2)&&round==8){
                        int i=Integer.parseInt(map1.get("total"))+1;
                        map1.put("total",String.valueOf(i));


                        i=Integer.parseInt(map2.get("total"))+1;
                        map2.put("total",String.valueOf(i));

                        //even
                        sendMessage(outputStream_player2, "Loc even\n" + location + "\n");
                        sendMessage(outputStream_player1,"GameOver\neven\n");
                    }
                    else{
                        if(round%2==0) {
                            int i=Integer.parseInt(map1.get("total"))+1;
                            map1.put("total",String.valueOf(i));
                            i=Integer.parseInt(map1.get("win"))+1;
                            map1.put("win",String.valueOf(i));
                            i=Integer.parseInt(map2.get("total"))+1;
                            map2.put("total",String.valueOf(i));

                            sendMessage(outputStream_player2, "Loc Lose\n" + location + "\n");
                            sendMessage(outputStream_player1,"GameOver\nwin\n");
                        }
                        else{
                            int i=Integer.parseInt(map1.get("total"))+1;
                            map1.put("total",String.valueOf(i));
                            i=Integer.parseInt(map2.get("win"))+1;
                            map2.put("win",String.valueOf(i));
                            i=Integer.parseInt(map2.get("total"))+1;
                            map2.put("total",String.valueOf(i));

                            sendMessage(outputStream_player1, "Loc Lose\n" + location + "\n");
                            sendMessage(outputStream_player2,"GameOver\nwin\n");
                        }

                    }
                    try {
                        new Thread(new ServerThread(players.get(turn)[0],map1)).start();
                    } catch (IOException e) {

                    }
                    try {
                        new Thread(new ServerThread(players.get(turn)[1],map2)).start();
                    } catch (IOException e) {

                    }
                    players.remove(turn);
                    break;
                } else {
                    if(round%2==0) {
                        sendMessage(outputStream_player2, "Loc\n" + location + "\n");
                    }
                    else {
                        sendMessage(outputStream_player1, "Loc\n" + location + "\n");
                    }
                }
                round++;
            }


        }

        private void handleMessage(String message,int player) throws IOException {
            String[] array=message.split("\n");
            System.out.println("handle");
            if(array[0].equals("Loc")) {
                System.out.println("Enter Loc");
                String[] loc = array[1].split(" ");
                int row = Integer.parseInt(loc[0]);
                int col = Integer.parseInt(loc[1]);
                board[row][col] = player + 1;
                location=array[1];
            }

        }




        private boolean judgeWinner(int player){
            System.out.println("JudgeWinner");
            int count;
            // heng
            for(int i=0;i<3;i++){
                count=0;
                for(int j=0;j<3;j++){
                    if(board[i][j]==player+1){
                       count++;
                    }
                }
                if(count==3){
                    return true;
                }
            }
            // shu
            for(int i=0;i<3;i++){
                count=0;
                for(int j=0;j<3;j++){
                    if(board[j][i]==player+1){
                        count++;
                    }
                }
                if(count==3){
                    return true;
                }
            }

            // xie
            count=0;
            for(int i=0;i<3;i++){
                if(board[i][i]==player+1){
                    count++;
                }
            }
            if(count==3){
                return true;
            }
            count=0;
            for(int i=0;i<3;i++){
                if(board[2-i][i]==player+1){
                    count++;
                }
            }
            if(count==3){
                return true;
            }

            return false;



        }
    }
    private Map<Integer,Socket[]> players;
    String p1_str;
    private Socket waiting=null;
    private ServerSocket serverSocket;
    PlayerData pd;
    String first_data;
    String second_data;
    Map<String,String> p1_map;



    public Server(ServerSocket serverSocket) throws IOException {
        players=new HashMap<>();
        this.serverSocket=serverSocket;
        pd=new PlayerData();

    }

    public void Start() throws IOException {
        while(true){
            Socket socket=serverSocket.accept();
            Runnable thread = new ServerThread(socket);
            new Thread(thread).start();
        }
    }

    private int checkMapKey(){
        for(int i=1;i<players.keySet().size()+1;i++){
            if(!players.keySet().contains(i)){
                return i;
            }
        }
        return 0;
    }


    private void startNewGame(int key,Socket new_client){
        players.put(key,new Socket[]{waiting,new_client});
    }

    private boolean sendMessage(OutputStream os, String s) {
        try {
            System.out.println("send: "+s);
            os.write((s+"end\n").getBytes());
            return true;
        }
        catch (IOException e){
            return false;
        }

    }
    public String getMessage(InputStream is) throws IOException {
//        String s = "";
//        StringBuilder sb=new StringBuilder();
//        while((s = br.readLine()) != null)
//            sb.append(s);
//        System.out.println(sb.toString());
//        return sb.toString();
//            System.out.println(s);
        InputStreamReader ipsr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(ipsr);
        String s = "";
        StringBuilder sb=new StringBuilder();
        while((s = br.readLine()) != null) {
            if(s.equals("end")){
                break;
            }
            else {
                sb.append(s+"\n");
            }

        }
        System.out.println("receive: "+sb.toString());
        return sb.toString();


    }






}
