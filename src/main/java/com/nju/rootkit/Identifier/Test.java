package com.nju.rootkit.Identifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
	
	public static void main(String[] args){
		//ApkAnalysis a=new ApkAnalysis();		
		//a.decompileApk();
		//a.obtainPermission();
		//a.obtainAPI();
		
		//APIExtracting b=new APIExtracting();
		//b.extractAPIs("E:\\AndroidTools\\Android-19-API\\android");
		
		//APKExtracting c=new APKExtracting();

//	    PrintWriter pw = null; 
//
//		for(int count=1;count<=50;count++){
//			System.out.println("-----第"+count+"页-----");
//			c.extractApk(count);	
//		}
//		
//		try {
//			pw = new PrintWriter(new FileWriter("E:\\AndroidTools\\APKTool\\SampleLink.txt",true), true);
//			Set set=c.getSet();
//			Iterator i = set.iterator();//先迭代出来  
//	        while(i.hasNext()){//遍历  
//	        	pw.println("http://apk.hiapk.com"+i.next().toString().trim());  
//	        }    
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
   
			
		//c.downloadAPKs();
		
//		Database d=new Database();
//		d.connect();
//		System.out.println("执行操作");
//		d.close();
		
		//Statistics s=new Statistics();
		//s.setPermissions();
		//s.setAPIs();
		//s.obtainTotalPermission();
		//s.obtainTotalApi();
		//s.countTotalPermission();
		//s.countPermissionDepart();
		//s.calulatePermissionX2();
		//s.calulatePermissionRate();
		//s.countTotalApi();
		//s.countApiDepart();
		//s.calulateApiX2();
		//s.calulateApiRate();
		
		
		Identifier i=new Identifier();
		i.connect();
		File dir=new File("E:\\AndroidTools\\APKTool\\NormalTest");
		File[] fs=dir.listFiles();
		int malwareCount=0;
		int normalCount=0;
		int isMalware;
		double[] rate=new double[2];
		double[] tmp=new double[2];
		for(File f:fs){
			rate=i.identifyPermission(f.getAbsolutePath());
			tmp=i.identifyApi(f.getAbsolutePath());
			
//			//两种必须同时用
//			if(rate[0]==0.0 || rate[1]==0.0 || tmp[0]==0.0 || tmp[1]==0.0){
//				continue;
//			}
			
			//两种可以有任一种
			if((rate[0]==0.0 && rate[1]==0.0) && (tmp[0]==0.0 && tmp[1]==0.0)){
				continue;
			}
			
//			//只可以没有权限文件
//			if(tmp[0]==0.0 && tmp[1]==0.0){
//				continue;
//			}
			
			rate[0]=rate[0]+tmp[0];
			rate[1]=rate[1]+tmp[1];
			
//			if(tmp[0]==0.0 && tmp[1]==0.0){
//				continue;
//			}
			
			if(rate[0]>rate[1]){
				normalCount++;
			}else{
				malwareCount++;
			}
		}
		
		i.closeConnect();
		
		System.out.println("检测为恶意软件："+malwareCount);
		System.out.println("检测为非恶意软件："+normalCount);
	}	

}



