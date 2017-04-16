package com.nju.rootkit.graphviz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by wei on 2017/4/10.
 */
public class Identify {

    private Map<Integer,GNode> nodes;
    private ArrayList<String> standards;
    private ArrayList<String> operations;
    private ArrayList<String> results;

    public static String content = "";

    public Identify(Map<Integer,GNode> nodes){
        this.nodes = nodes;
        standards = new ArrayList<>();
        operations = new ArrayList<>();
        results = new ArrayList<>();
    }

//    public Identify(){
//        standards = new ArrayList<>();
//        operations = new ArrayList<>();
//        results = new ArrayList<>();
//    }

    public void identify(){
        getStandards();
        getOperations();
        compare();
    }

    private void getStandards() {
        try {
            File file = new File("F:/AndroidTools/behavior/behavior.txt");
            if(file.isFile() && file.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file));
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
//                    System.out.println(lineTxt);
                    standards.add(lineTxt);
                }
                read.close();
            }else{
                System.out.println("找不到指定的文件");
            }
        }catch (Exception e){
            System.out.println("读取行为文件内容出错");
            e.printStackTrace();
        }
    }

    private void getOperations() {
        if (nodes != null && nodes.size() != 0){
            Set<Map.Entry<Integer,GNode>> entries = nodes.entrySet();
            for (Map.Entry<Integer,GNode> entry:entries){
                String temp = entry.getValue().getLabel();
                String[] list = temp.split(",");
                if (list.length == 0){  //为0或其它
                    if (!temp.startsWith("0")){ //不为0
                        list = temp.split("\n");
                        if (list.length != 0){
                            for (String str : list){
                                if (str != null && !str.equals("")){
                                    operations.add(str);
                                }
                            }
                        }
                    }
                }else {
                    for (String str : list){
                        operations.add(str.replace("\n", ""));
                    }
                }
            }
        }
    }

    private void compare() {
        if (operations.size() != 0 && standards.size() != 0){
            for (String operation : operations){
                for (String standard : standards){
                    if (operation.contains(standard)){
                        results.add(standard);
                    }
                }
            }
            removeAndOut();
        }
    }

    private void removeAndOut(){
        if (results.size() != 0){
            ArrayList<String> listTemp= new ArrayList<>();
            Iterator<String> it = results.iterator();
            while(it.hasNext()){
                String a = it.next();
                if(listTemp.contains(a)){
                    it.remove();
                }
                else{
                    listTemp.add(a);
                }
            }

            content = "";
            for (String result : results){
                content = content + result + "\n";
            }
        }
    }





    public static void main(String[] args){
//        Identify identify = new Identify();
//        identify.getStandards();
    }


}
