package application.Data;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerData {
    public static List<Map<String,String>> data_map;
    public PlayerData() throws IOException {
        data_map=new ArrayList<>();
        loadData();

    }
    private void loadData() throws IOException {
        BufferedReader in = new BufferedReader(new FileReader("D:\\lsy\\java2\\lab\\A2\\Tic-tac-toe\\resources\\Data\\Player_Data.txt"));
        data_map=in.lines().map(e->e.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)",-1)).map(s->{Map<String,String> data_tmp=new HashMap<>();
        data_tmp.put("name",s[0]);
        data_tmp.put("password",s[1]);
        data_tmp.put("win",s[2]);
        data_tmp.put("total",s[3]);
        data_tmp.put("avatar",s[4]);
        data_tmp.put("chess",s[5]);
        return data_tmp;}).collect(Collectors.toList());
        in.close();
//        while((data=in.readLine())!=-1){
//            Map<String,String> data_tmp=new HashMap<>();
//            String[] col=data_row[i].split(",");
//            data_tmp.put("name",col[0]);
//            data_tmp.put("password",col[1]);
//            data_tmp.put("win",col[2]);
//            data_tmp.put("total",col[3]);
//            data_tmp.put("avatar",col[4]);
//            data_tmp.put("chess",col[5]);
//            data_map.add(data_tmp);
//        }
    }
    public static void writeData() throws IOException {
        FileWriter fw = new FileWriter("./resouces/Data/Player_Data.txt.txt");
        String data="";
        for(int i=0;i<data_map.size();i++){
            Map<String,String> data_tmp=data_map.get(i);
            data+=data_tmp.get("name")+","+data_tmp.get("password")+","+data_tmp.get("win")+","+data_tmp.get("total")+",\""+data_tmp.get("avatar")+"\","+data_tmp.get("chess")+"\n";
        }
        fw.write(data);
        fw.close();

    }


}
