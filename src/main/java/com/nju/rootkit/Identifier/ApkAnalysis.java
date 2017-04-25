package com.nju.rootkit.Identifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
	private String apkFileDir="E:\\AndroidTools\\APKTool\\APKs";//解析后的apk文件存放路径
	private String apkName=null;
	private String packageName=null;
	int count=0;
	Set set=new HashSet();
	
	/*
	 * 反编译APK
	 */
	public void decompileApk(String apkPath,String apkName){
		this.apkName=apkName;		
		System.out.println("apkName-------"+apkName);
		
		String tempName=apkName.substring(0, apkName.length()-4);
		File old=new File(apkFileDir+"\\"+tempName);
		tempName=tempName.replace(".", "");
		this.packageName=tempName;
		//this.packageName="test";
		System.out.println("packageName-------"+this.packageName);
		
		Runtime runtime = Runtime.getRuntime();
		String cmd=apkToolPath+" d "+apkPath+"\\"+apkName+" "+apkFileDir+"\\"+this.packageName;//d为反编译命令
		//String cmd="cd "+apkToolPath;
		
		try {			
			Process p=runtime.exec("cmd /c "+cmd,null,new File(apkFileDir));
			p.waitFor();
			System.out.println("apk解析完毕！");
			runtime.exec("cmd /c wmic process where name='cmd.exe' call terminate");

			p.destroy();
			
		} catch (IOException e) {
			System.out.println(e.toString());
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			obtainPermission();
		}
		
		
	}
	
	public void obtainPermission(){
		System.out.println("开始解析权限！");
		packageName="test";

		//创建一个DocumentBuilderFactory的对象
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		//创建DocumentBuilder对象
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(apkFileDir+"\\"+packageName+"\\AndroidManifest.xml");
			//获取所有uses-permission节点的集合
			NodeList bookList = document.getElementsByTagName("uses-permission");
			//遍历
			String p;
			for (int i = 0; i < bookList.getLength(); i++) {
				Node n=bookList.item(i);
				Element elem = (Element) n; 
				p=elem.getAttribute("android:name");
				System.out.println(p);
			}
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
	}
	
	public void obtainAPI(){
		String packageName="test";
		
		String smaliPath=apkFileDir+"\\"+packageName+"\\smali";
		analyzeFile(smaliPath);
		
		for(Iterator it = set.iterator();  it.hasNext(); ){
			System.out.println(it.next().toString());    
		}
		System.out.println("共调用API个数："+set.size());
		
	}
	
	private void analyzeFile(String path){
		File rootFile=new File(path);
		if(rootFile.isDirectory()){
			File[] files=rootFile.listFiles();
			
			if(files!=null){
				for(File f:files){
					//只检测android包和com包
					if(f.getAbsolutePath().contains("\\android") || f.getAbsolutePath().contains("\\com")){
						analyzeFile(f.getAbsolutePath());
					}					
				}				
			}
		}else{
			File f=new File(path);
			if(f.getName().contains(".smali") && (!f.getName().equals("SuppressLint.smali")) 
					&& (!f.getName().equals("TargetApi.smali"))){
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
								tmp=parts[0].lastIndexOf('/');
								api=parts[0].substring(tmp+1, parts[0].length());
								
								if(!api.contains("$") && !parts[1].contains("<init>")){
									tmp=parts[1].indexOf('(');
									api+="."+parts[1].substring(0, tmp);
									
									//System.out.println(f.getAbsolutePath()+"-----"+api);	
									set.add(f.getAbsolutePath()+"-----"+api);
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

}
