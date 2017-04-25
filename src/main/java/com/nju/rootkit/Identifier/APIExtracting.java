package com.nju.rootkit.Identifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class APIExtracting {
	
	private Set apiSet=new HashSet();
	int count=0;
	
	public void extractAPIs(String androidDir){
		File rootFile=new File(androidDir);
		
		if(rootFile.exists()){
			File[] files=rootFile.listFiles();
			for(File f:files){
				analyzeFile(f.getAbsolutePath());
			}
			
			if(apiSet.size()>0){
				writeToFile();
			}
			
			System.out.println("总共API个数："+count);
			
		}else{
			System.out.println("android文件夹路径不正确！");
		}
	}
	
	private void analyzeFile(String filePath){
		
		File file=new File(filePath);
		//文件夹
		if(file.isDirectory()){
			File[] files=file.listFiles();
			if(files!=null){
				for(File f:files){
					analyzeFile(f.getAbsolutePath());
				}
			}
		}else{//为java文件
			//获取类名
			String[] names=file.getName().split("\\.");
			String className=names[0];
			
			BufferedReader reader = null;  
			
			try {
				reader = new BufferedReader(new FileReader(file));
				String line=null;
				
				while ((line = reader.readLine()) != null) { 
					line=line.trim();
					if(!line.contains("*") && !line.contains("@") 
							&& line.contains("(") && line.contains("")){
						//筛选出方法或类
						if(line.contains("public") || line.contains("private") || line.contains("protected")){
							
							//筛选出方法
							String[] tmps=line.split("\\(");
							String[] words=tmps[0].split(" ");
							if(words.length==3){
								
								apiSet.add(className+"."+words[2]);
							}else if(words.length==4){
								apiSet.add(className+"."+words[3]);
							}
						}
						
					}
				}
				
				if(apiSet.size()>500){
					writeToFile();
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void writeToFile(){
		File file =new File("E:\\AndroidTools\\Android-19-API\\AllAPI");
		
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			FileWriter fileWritter = new FileWriter(file.getAbsolutePath(),true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			
			String content="";
			for(Iterator it = apiSet.iterator();  it.hasNext(); ){
				content+=it.next().toString()+"\n";
				count++;
			}
			
            bufferWritter.write(content);
            bufferWritter.close();
            
            //清除set中内容
            apiSet.clear();
            System.out.println(count);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
