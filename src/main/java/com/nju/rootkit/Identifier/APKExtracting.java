package com.nju.rootkit.Identifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * 实现使用网络爬虫获取安卓应用程序的样本数据
 */
public class APKExtracting {
	
	Set set=new HashSet();

	public Set getSet(){
		return set;
	}
	
	public void extractApk(int count){ 
	    URL url = null;  
	    URLConnection urlconn = null;  
	    BufferedReader br = null;  
//	    PrintWriter pw = null; 
        //String regex = "http://www.wandoujia.com/apps/([A-Za-z0-9]+\\.)+[A-Za-z0-9]+/binding";//豌豆荚
        String regex = "/appdown/([A-Za-z0-9]+\\.)+[A-Za-z0-9]+";//安卓市场
        Pattern p = Pattern.compile(regex);          
        try {
			url = new URL("http://apk.hiapk.com/apps?sort=5&pi="+count);//安卓市场
			urlconn = url.openConnection();  
//			pw = new PrintWriter(new FileWriter("E:\\AndroidTools\\APKTool\\SampleLink.txt",true), true);//存储收集到的链接
			
            br = new BufferedReader(new InputStreamReader(urlconn.getInputStream())); 
            String buf = null;  
            while ((buf = br.readLine()) != null){
            	//System.out.println(buf);
            	Matcher buf_m = p.matcher(buf); 
                while (buf_m.find()) {  
                    //pw.println(buf_m.group());  
                    //System.out.println(buf_m.group());
                    set.add(buf_m.group());
                }  
            } 
            System.out.println(set.size()+"");
            
            br.close();  
            //pw.close();  
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	public void downloadAPKs(){
		File f=new File("E:\\AndroidTools\\APKTool\\SampleLink.txt");
		BufferedReader reader = null; 
		int count=0;
		try {
			reader = new BufferedReader(new FileReader(f));
			String line=null;
			while ((line = reader.readLine()) != null){
				System.out.println(line);
				downloadAPK(line.trim());
				count++;
			}
			System.out.println("一共应用个数为："+count);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void downloadAPK(String urlPath){
        // 下载网络文件        

        URL url=null;
        try {
			url= new URL(urlPath);
            URLConnection conn = url.openConnection();
            InputStream inStream = conn.getInputStream();
            
            //获取包名
            String[] parts=urlPath.split("/");
            String packageName=parts[4];
            
            FileOutputStream fs = new FileOutputStream("E:\\AndroidTools\\APKTool\\Sample\\"+packageName+".apk");

            byte[] buffer = new byte[1204];
            int byteread = 0;
            while ((byteread = inStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteread);
            }
            
            System.out.println(packageName);
            fs.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
	}

}
