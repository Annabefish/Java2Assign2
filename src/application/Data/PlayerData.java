package application.Data;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerData {
    public List<Map<String, String>> data_map;

    public PlayerData() throws IOException {
        data_map = new ArrayList<>();
        loadData();

    }

    private void loadData() throws IOException {
        BufferedReader in = new BufferedReader(new FileReader("D:\\lsy\\java2\\lab\\A2\\Tic-tac-toe\\resources\\Data\\Player_Data.txt"));
        data_map = in.lines().map(e -> e.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1)).map(s -> {
            Map<String, String> data_tmp = new HashMap<>();
            data_tmp.put("name", s[0]);
            data_tmp.put("password", s[1]);
            data_tmp.put("win", s[2].equals("")?"0":s[2]);
            data_tmp.put("total", s[3].equals("")?"0":s[3]);
            if(s[4].startsWith("\"")&&s[4].endsWith("\"")){
                data_tmp.put("avatar", s[4].substring(1,s[4].length()-1));
            }
            else {
                data_tmp.put("avatar", s[4]);
            }
            data_tmp.put("chess", s[5]);
            return data_tmp;
        }).collect(Collectors.toList());
        in.close();

    }

    public void writeData() throws IOException {
        FileWriter fw = new FileWriter("D:\\lsy\\java2\\lab\\A2\\Tic-tac-toe\\resources\\Data\\Player_Data.txt");
        String data = "";
        for (int i = 0; i < data_map.size(); i++) {
            Map<String, String> data_tmp = data_map.get(i);
            data += data_tmp.get("name") + "," + data_tmp.get("password") + "," + data_tmp.get("win") + "," + data_tmp.get("total") + ",\"" + data_tmp.get("avatar") + "\"," + data_tmp.get("chess") + "\n";
        }
        fw.write(data);
        fw.close();
        System.out.println("file close");

    }


}
