package com.nju.rootkit.Identifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class APIExtracting {
	
	private Set apiSet=new HashSet();
	private ArrayList<String> apiPackage=new ArrayList<String>();
	int count=0;
	
	public void extractAPIs(String androidDir){
		
		//读取需要提取的API包名
		getAPIPackages();
		
		File rootFile=new File(androidDir);
		
		if(rootFile.exists()){
			File[] files=rootFile.listFiles();
			String name=null;
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
	
	private void getAPIPackages(){
		apiPackage.add("\\accessibilityservice\\");//开发可访问服务
		
		apiPackage.add("\\accounts\\");//账户
		
		apiPackage.add("\\app\\ActivityManager");//包含封装整个Android应用程序模型的高级类
		apiPackage.add("\\app\\AlarmManager");//包含封装整个Android应用程序模型的高级类
		apiPackage.add("\\app\\AliasActivity");//包含封装整个Android应用程序模型的高级类
		apiPackage.add("\\app\\DownloadManager");//包含封装整个Android应用程序模型的高级类
		apiPackage.add("\\app\\FragmentManager");//包含封装整个Android应用程序模型的高级类
		apiPackage.add("\\app\\KeyguardManager");//包含封装整个Android应用程序模型的高级类
		apiPackage.add("\\app\\LoaderManager");//包含封装整个Android应用程序模型的高级类
		apiPackage.add("\\app\\NativeActivity");//包含封装整个Android应用程序模型的高级类
		apiPackage.add("\\app\\SearchManager");//包含封装整个Android应用程序模型的高级类
		apiPackage.add("\\app\\WallpaperManager");//包含封装整个Android应用程序模型的高级类
		apiPackage.add("\\app\\ContextImpl");//包含封装整个Android应用程序模型的高级类
		
		apiPackage.add("\\bluetooth\\");//蓝牙使用
		
		apiPackage.add("\\content\\pm\\");//用于在设备上访问和发布数据
		apiPackage.add("\\content\\ContentResolver");//用于在设备上访问和发布数据
		apiPackage.add("\\content\\BroadcastReceiver");//用于在设备上访问和发布数据
		apiPackage.add("\\content\\ClipboardManager");//用于在设备上访问和发布数据
		apiPackage.add("\\content\\ContentProvider");//用于在设备上访问和发布数据
		apiPackage.add("\\content\\Intent");//用于在设备上访问和发布数据
		apiPackage.add("\\content\\UriMatcher");//用于在设备上访问和发布数据
		apiPackage.add("\\content\\UriPermission");//用于在设备上访问和发布数据
		
		apiPackage.add("\\database\\");//数据库相关
		
		apiPackage.add("\\hardware\\Camera");//对硬件功能的支持
		
		apiPackage.add("\\location\\");//位置信息
		
		apiPackage.add("\\media\\MediaRecorder");//管理音频和视频中各种媒体
		apiPackage.add("\\media\\RingtoneManager");//管理音频和视频中各种媒体
		apiPackage.add("\\media\\MediaRecorder");//管理音频和视频中各种媒体
		apiPackage.add("\\media\\AudioRecord");//管理音频和视频中各种媒体
		
		apiPackage.add("\\net\\");//网络访问
		
		apiPackage.add("\\os\\");//操作系统服务
		
		apiPackage.add("\\provider\\");//访问Android提供的内容
		
		apiPackage.add("\\telephony\\CellLocation");//监控基本电话信息
		apiPackage.add("\\telephony\\NeighboringCellInfo");//监控基本电话信息
		apiPackage.add("\\telephony\\PhoneStateListener");//监控基本电话信息
		apiPackage.add("\\telephony\\ServiceState");//监控基本电话信息
		apiPackage.add("\\telephony\\SmsManager");//监控基本电话信息
		apiPackage.add("\\telephony\\SmsMessage");//监控基本电话信息
		apiPackage.add("\\telephony\\TelephonyManager");//监控基本电话信息
		
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
			//过滤API类文件
			String p=file.getAbsolutePath();
			boolean isP=false;
			for(int i=0;i<apiPackage.size();i++){
				if(p.contains(apiPackage.get(i))){
					isP=true;
					break;
				}
			}	
			
			if(!isP){
				return;
			}
			
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
		File file =new File("E:\\AndroidTools\\Android-19-API\\AllAPIs");
		
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
