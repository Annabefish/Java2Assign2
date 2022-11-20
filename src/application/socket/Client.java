package application.socket;

import application.Client_Main;

import java.io.*;
import java.net.Socket;

public class Client {

    Socket socket;
    OutputStream os;
    InputStream is;

    public Client(int port) throws IOException {
        this.socket = new Socket("localhost", port);
        os = socket.getOutputStream();
        is = socket.getInputStream();
    }

    public boolean sendMessage(String s) {
        try {
            System.out.println("send: " + s);
            byte[] msg = (s + "end\n").getBytes();
            os.write(msg);
            return true;
        } catch (IOException e) {
            return false;
        }

    }

    public String getMessage() throws IOException {
        InputStreamReader ipsr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(ipsr);
        String s = "";
        StringBuilder sb = new StringBuilder();
        while ((s = br.readLine()) != null) {
            if (s.equals("end")) {
                break;
            } else {
                sb.append(s + "\n");
            }

        }
        System.out.println("receive: " + sb.toString());
        return sb.toString();
//        byte[] buf = new byte[1024];
//        int readLen=0;
//        StringBuilder sb=new StringBuilder();
//        while((readLen = is.read(buf))!=-1){
//            String s =new String(buf,0,readLen);
//            if(s.equals("end")){
//                break;
//            }
//            else {
//                sb.append(s);
//            }
//        }
//        System.out.println(sb.toString());
//        return sb.toString();

    }

    public InputStream getIs() {
        return is;
    }

    public OutputStream getOs() {
        return os;
    }
}
