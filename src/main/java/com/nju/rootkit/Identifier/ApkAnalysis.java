package com.nju.rootkit.Identifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
		//读取AndroidManifest文件
		/*
		File f=new File(apkFileDir+"\\"+packageName+"\\AndroidManifest.xml");
	    FileInputStream fis;
		try {
			fis = new FileInputStream(f);
		    
		    //Construct BufferedReader from InputStreamReader  
		    BufferedReader br = new BufferedReader(new InputStreamReader(fis));  
		    
		    String line = null; 
		    ArrayList permission=new ArrayList<String>();
		    permission.add(packageName);
		    int start;
		    int end;
		    String p=null;
			while ((line = br.readLine()) != null) {  
				//获取权限
			    if(line.contains("permission")){
			    	start=line.indexOf("permission.");
			    	end=line.indexOf("\"");
			    	p=line.substring(start, end);
			    	permission.add(p);
			    	System.out.println(p);
			    }
			}
			
			br.close();
		    
		} catch (FileNotFoundException e1) {
			System.out.println(e1.toString());
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
			e.printStackTrace();
		}  
		*/
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

}
