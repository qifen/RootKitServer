package com.nju.rootkit.Identifier;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class Identifier {
	
	Database db=new Database();
	Connection conn;
	
	public void connect(){
		conn=db.connect();
	}
	
	public void closeConnect(){
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getAnalysisResult(String apkName){
		//生成随机数，形成文件名
//		Random random = new Random();
//        int a=random.nextInt(5000);
//        System.out.println(a);
		
		String result=null;
		ApkAnalysis apkAnalysis=new ApkAnalysis();
		String packageDir=apkAnalysis.decompileApk(apkName);//反编译apk
		
		File f=new File(packageDir);
		if(!f.exists()){
			System.out.println("解析失败！");
			return null;
		}
		
		double[] rate=new double[2];
		double[] tmp=new double[2];
		connect();
		rate=identifyPermission(packageDir);
		tmp=identifyApi(packageDir);
		closeConnect();
		rate[0]=rate[0]+tmp[0];
		rate[1]=rate[1]+tmp[1];
		
		if(rate[0]>rate[1]){
			result="非恶意软件";
		}else{
			result="恶意软件";
		}
		
		System.out.println("结果是："+result);
		return result;
	}
	
	public double[] identifyPermission(String apkPath){
		ApkAnalysis analysis=new ApkAnalysis();
		ArrayList<String> permissions=analysis.obtainPermission(apkPath);
		
		int isMalware=0;
		double[] rate={0.0,0.0};
		if(permissions==null){
			System.out.println("xml文件不存在！");
		}else{
			//conn=db.connect();
			PreparedStatement psts = null;
			ResultSet rs = null;
			
			String sql="select * from rootkit.permissionrate";
			try {
				psts= conn.prepareStatement(sql);
				rs = psts.executeQuery();
				
				double malwareRate=349.0/724.0;
				double normalRate=375.0/724.0;
				
				boolean hasP;
				String pname;
				double pinmalware;
				double pnotinmalware;
				double pinnormal;
				double pnotinnormal;
				while(rs.next()){
					hasP=false;
					pname=rs.getString("pname").trim();
					pinmalware=rs.getDouble("pinmalware");
					pnotinmalware=rs.getDouble("pnotinmalware");
					pinnormal=rs.getDouble("pinnormal");
					pnotinnormal=rs.getDouble("pnotinnormal");
					
					for(String p:permissions){
						if(p.trim().equals(pname)){
							hasP=true;
							break;
						}
					}
					
					//软件是否申请此权限
					if(hasP){
						malwareRate=malwareRate*pinmalware;
						normalRate=normalRate*pinnormal;
						
					}else{
						malwareRate=malwareRate*pnotinmalware;
						normalRate=normalRate*pnotinnormal;
					}
					
				}
				
				psts.close();
				rs.close();
				
				rate[0]=normalRate;
				rate[1]=malwareRate;
				
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
//			try {
//				conn.close();
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
		}
		
		return rate;
	}
	
	public double[] identifyApi(String apkPath){
		ApkAnalysis analysis=new ApkAnalysis();
		Set set=analysis.obtainAPI(apkPath);
		System.out.println(apkPath);
		
		double[] rate={0.0,0.0};
		int isMalware=0;//-1为恶意软件，0为不存在，1为非恶意软件
		if(set.size()==0){
			System.out.println("smali文件不存在！");
		}else{
			//conn=db.connect();
			PreparedStatement psts = null;
			ResultSet rs = null;
			
			String sql="select * from rootkit.apirate";
			try {
				psts= conn.prepareStatement(sql);
				rs = psts.executeQuery();
				
				double malwareRate=349.0/724.0;
				double normalRate=375.0/724.0;
				
				boolean hasP;
				String pname;
				double pinmalware;
				double pnotinmalware;
				double pinnormal;
				double pnotinnormal;
				while(rs.next()){
					hasP=false;
					pname=rs.getString("apiname").trim();
					pinmalware=rs.getDouble("pinmalware");
					pnotinmalware=rs.getDouble("pnotinmalware");
					pinnormal=rs.getDouble("pinnormal");
					pnotinnormal=rs.getDouble("pnotinnormal");
					
					Iterator i=set.iterator();
					while(i.hasNext()){
						if(i.next().toString().equals(pname)){
							hasP=true;
							break;
						}
					}
					
					//软件是否申请此权限
					if(hasP){
						malwareRate=malwareRate*pinmalware;
						normalRate=normalRate*pinnormal;
						
					}else{
						malwareRate=malwareRate*pnotinmalware;
						normalRate=normalRate*pnotinnormal;
					}
					
				}
				
				psts.close();
				rs.close();
				
				rate[0]=normalRate;
				rate[1]=malwareRate;
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
//			try {
//				conn.close();
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
		}
		
		return rate;
	}

}
