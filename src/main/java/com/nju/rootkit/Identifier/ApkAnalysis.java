package com.nju.rootkit.Identifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ApkAnalysis {
	
	private String apkToolPath="E:\\AndroidTools\\APKTool\\apktool\\apktool";//apktool的路径
	int count=0;
	Set set=new HashSet();
	ArrayList<String> apiPackage=null;
	
	/*
	 * 反编译APK
	 */
	public void decompileApk(){
		
		//String apkFileDir="E:\\AndroidTools\\APKTool\\MalwareTest";//解析后的apk文件存放路径,恶意软件测试集
		//String apkFileDir="E:\\AndroidTools\\APKTool\\MalwareTrain";//解析后的apk文件存放路径,恶意软件训练集
		//String apkFileDir="E:\\AndroidTools\\APKTool\\NormalTrain";//解析后的apk文件存放路径,正常软件训练集
		String apkFileDir="E:\\AndroidTools\\APKTool\\NormalTest";//解析后的apk文件存放路径,正常软件测试集
		
		try {			
			//获取需要反编译的apk文件
			File apkDir=new File("E:\\AndroidTools\\APKTool\\Sample\\Test");
			File[] apks=apkDir.listFiles();
			String apkName=null;
			String apkPath=null;
			String tempName=null;
			String packageName=null;
			String cmd=null;
			
			int count=1;
			
			Runtime runtime=Runtime.getRuntime();
			File apk=null;
			for(int i=81;i<apks.length;i++){
			
				apk=apks[i];
				if(apk.isDirectory()){
					continue;
				}
				
				apkName=apk.getName();
				apkPath=apk.getAbsolutePath();
				tempName=apkName.substring(0, apkName.length()-4);
				packageName=tempName.replace(".", "");
				
				cmd=apkToolPath+" d "+apkPath+" -o "+apkFileDir+"\\"+packageName;//d为反编译命令
				
				Process p=runtime.exec("cmd /c "+cmd);
				p.waitFor();
				System.out.println(i+"："+apkName+"解析完毕！");
				p.destroy();
			}
			
			runtime.exec("cmd /c wmic process where name='cmd.exe' call terminate");			
			
		} catch (IOException e) {
			System.out.println(e.toString());
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 		
	}
	
	/*
	 * 反编译单个apk
	 */
	public String decompileApk(String apkName){
		String apkFileDir="E:\\AndroidTools\\apk";//解析后的apk文件存放路径
		String packageName=null;//反编译后文件夹名
		
		try {			
			//获取需要反编译的apk文件
			File apk=new File("E:\\AndroidTools\\apk\\"+apkName);
			
			String tempName=apkName.substring(0, apkName.length()-4);
			packageName=tempName.replace(".", "");			
			
			Runtime runtime=Runtime.getRuntime();
			String apkPath=apk.getAbsolutePath();
			String cmd=apkToolPath+" d "+apkPath+" -o "+apkFileDir+"\\"+packageName;//d为反编译命令
			
			Process p=runtime.exec("cmd /c "+cmd);
			p.waitFor();
			System.out.println(packageName+"解析完毕！");
			p.destroy();
			
			runtime.exec("cmd /c wmic process where name='cmd.exe' call terminate");			
			
		} catch (IOException e) {
			System.out.println(e.toString());
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	
		
		return "E:\\AndroidTools\\apk\\"+packageName;
	}
	
	public ArrayList<String> obtainPermission(String apkFileDir){
		System.out.println("开始解析权限："+apkFileDir);
		//String apkFileDir="E:\\AndroidTools\\APKTool\\MalwareTrain";//解析后的apk文件存放路径
		//String packageName="test";
		
		//判断文件是否存在
		File f=new File(apkFileDir+"\\AndroidManifest.xml");
		if(!f.exists()){
			return null;
		}

		//创建一个DocumentBuilderFactory的对象
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		//创建DocumentBuilder对象
		ArrayList<String> ps=new ArrayList<String>();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(apkFileDir+"\\AndroidManifest.xml");
			//获取所有uses-permission节点的集合
			NodeList bookList = document.getElementsByTagName("uses-permission");
			//遍历
			String p;
			for (int i = 0; i < bookList.getLength(); i++) {
				Node n=bookList.item(i);
				Element elem = (Element) n; 
				p=elem.getAttribute("android:name");
				ps.add(p);
				//System.out.println(p);
			}
			return ps;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ps;
	}
	
	public Set obtainAPI(String apkFileDir){
		//String apkFileDir="E:\\AndroidTools\\APKTool\\MalwareTrain";//解析后的apk文件存放路径
		//String packageName="_comaijiaoyouandroidsipphone_1005_105";
		System.out.println("开始解析API："+apkFileDir);
		if(apiPackage==null){
			apiPackage=new ArrayList<String>();
			getAPIPackages();
		}
		
		set.clear();
		
		File apkFile=new File(apkFileDir);
		File[] files=apkFile.listFiles();
		if(files==null || files.length==0){
			return set;
		}
		for(File f:files){
			if(f.getAbsolutePath().contains("\\smali")){
				analyzeFile(f.getAbsolutePath());
			}
		}
		
		return set;
		
	}
	
	private void analyzeFile(String path){
		File rootFile=new File(path);
		if(rootFile.isDirectory()){
			File[] files=rootFile.listFiles();
			
			if(files!=null){
				for(File f:files){
					//analyzeFile(f.getAbsolutePath());	
					//只检测android包和com包
					if(f.getAbsolutePath().contains("\\android") || f.getAbsolutePath().contains("\\com")){
						analyzeFile(f.getAbsolutePath());	
					}
				}				
			}
		}else{
			File f=new File(path);
			if(f.getName().contains(".smali") && f.getName().charAt(0)>='A' && f.getName().charAt(0)<='Z' ){
				BufferedReader reader = null; 
				try {
					reader = new BufferedReader(new FileReader(f));
					String line = null; 
					String[] parts=null;
					String api=null;
					int tmp=0;
					
					while ((line = reader.readLine()) != null){
						//获取API调用函数
						if(line.contains("invoke-")){
							parts=line.split(";->");
							//确认是否为API调用函数
							if(parts.length==2 && parts[0].contains("Landroid")){
								
								//确认是否为需要的API调用函数
								boolean isAPI=false;
								for(String str:apiPackage){
									if(parts[0].contains(str)){
										isAPI=true;
										break;
									}
								}
								if(!isAPI){
									continue;
								}
								
								tmp=parts[0].lastIndexOf('/');
								api=parts[0].substring(tmp+1, parts[0].length());
								
								//如果存在内部类调用API
								if(api.contains("$")){
									int pos=api.indexOf('$');
									api=api.substring(0, pos);
								}
								
								if(!parts[1].contains("<init>")){
									tmp=parts[1].indexOf('(');
									int pos=50;
									if(parts[1].contains("$")){
										pos=parts[1].indexOf('$');
									}
									
									if(tmp<pos){
										api+="."+parts[1].substring(0, tmp);
									}else{
										api+="."+parts[1].substring(0, pos);
									}
																		
									//System.out.println(f.getAbsolutePath()+"-----"+api);	
									set.add(api);
								}								

							}
						}
					}
					reader.close();  
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
			}
		}
	}
	
	private void getAPIPackages(){
		apiPackage.add("/accessibilityservice/");//开发可访问服务
		
		apiPackage.add("/accounts/");//账户
		
		apiPackage.add("/app/ActivityManager");//包含封装整个Android应用程序模型的高级类
		apiPackage.add("/app/AlarmManager");//包含封装整个Android应用程序模型的高级类
		apiPackage.add("/app/AliasActivity");//包含封装整个Android应用程序模型的高级类
		apiPackage.add("/app/DownloadManager");//包含封装整个Android应用程序模型的高级类
		apiPackage.add("/app/FragmentManager");//包含封装整个Android应用程序模型的高级类
		apiPackage.add("/app/KeyguardManager");//包含封装整个Android应用程序模型的高级类
		apiPackage.add("/app/LoaderManager");//包含封装整个Android应用程序模型的高级类
		apiPackage.add("/app/NativeActivity");//包含封装整个Android应用程序模型的高级类
		apiPackage.add("/app/SearchManager");//包含封装整个Android应用程序模型的高级类
		apiPackage.add("/app/WallpaperManager");//包含封装整个Android应用程序模型的高级类
		apiPackage.add("/app/ContextImpl");//包含封装整个Android应用程序模型的高级类
		
		apiPackage.add("/bluetooth/");//蓝牙使用
		
		apiPackage.add("/content/pm/");//用于在设备上访问和发布数据
		apiPackage.add("/content/ContentResolver");//用于在设备上访问和发布数据
		apiPackage.add("/content/BroadcastReceiver");//用于在设备上访问和发布数据
		apiPackage.add("/content/ClipboardManager");//用于在设备上访问和发布数据
		apiPackage.add("/content/ContentProvider");//用于在设备上访问和发布数据
		apiPackage.add("/content/Intent");//用于在设备上访问和发布数据
		apiPackage.add("/content/UriMatcher");//用于在设备上访问和发布数据
		apiPackage.add("/content/UriPermission");//用于在设备上访问和发布数据
		
		apiPackage.add("/database/");//数据库相关
		
		apiPackage.add("/hardware/Camera");//对硬件功能的支持
		
		apiPackage.add("/location/");//位置信息
		
		apiPackage.add("/media/MediaRecorder");//管理音频和视频中各种媒体
		apiPackage.add("/media/RingtoneManager");//管理音频和视频中各种媒体
		apiPackage.add("/media/MediaRecorder");//管理音频和视频中各种媒体
		apiPackage.add("/media/AudioRecord");//管理音频和视频中各种媒体
		
		apiPackage.add("/net/");//网络访问
		
		apiPackage.add("/os/");//操作系统服务
		
		apiPackage.add("/provider/");//访问Android提供的内容
		
		apiPackage.add("/telephony/CellLocation");//监控基本电话信息
		apiPackage.add("/telephony/NeighboringCellInfo");//监控基本电话信息
		apiPackage.add("/telephony/PhoneStateListener");//监控基本电话信息
		apiPackage.add("/telephony/ServiceState");//监控基本电话信息
		apiPackage.add("/telephony/SmsManager");//监控基本电话信息
		apiPackage.add("/telephony/SmsMessage");//监控基本电话信息
		apiPackage.add("/telephony/TelephonyManager");//监控基本电话信息
		
	}

}
