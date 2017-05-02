package com.nju.rootkit.Identifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Statistics {
	
	Database db=new Database();
	Connection conn;
	
	public void setPermissions(){
		conn=db.connect();
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 设置手动提交 
		
		String sql="insert into rootkit.permission(name) values (?)";//插入permission表
		PreparedStatement psts = null;
		try {
			psts= conn.prepareStatement(sql);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
		
		File f=new File("E:\\AndroidTools\\Android-Permission\\Permissions.txt");//android所有权限文件
		BufferedReader reader = null; 
		
		try {
			reader = new BufferedReader(new FileReader(f));
			String line = null; 
			while ((line = reader.readLine()) != null){
				psts.setString(1, line);
				psts.addBatch();          // 加入批量处理  
			}
	        psts.executeBatch(); // 执行批量处理  
	        conn.commit();  // 提交  
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		db.close();
		
	}
	
	public void setAPIs(){
		conn=db.connect();
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 设置手动提交 
		
		String sql="insert into rootkit.api(name) values (?)";//插入permission表
		PreparedStatement psts = null;
		try {
			psts= conn.prepareStatement(sql);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
		
		File f=new File("E:\\AndroidTools\\Android-19-API\\AllAPIs");//android所有api文件
		BufferedReader reader = null; 
		
		try {
			reader = new BufferedReader(new FileReader(f));
			String line = null; 
			while ((line = reader.readLine()) != null){
				psts.setString(1, line);
				psts.addBatch();          // 加入批量处理  
			}
	        psts.executeBatch(); // 执行批量处理  
	        conn.commit();  // 提交  
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		db.close();
		
	}
	
	public void obtainTotalPermission(){
		ApkAnalysis analysis=new ApkAnalysis();
		conn=db.connect();
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 设置手动提交 
		PreparedStatement psts = null;
		
		//File rootDir=new File("E:\\AndroidTools\\APKTool\\MalwareTrain");//恶意软件
		File rootDir=new File("E:\\AndroidTools\\APKTool\\NormalTrain");//非恶意软件
		File[] files=rootDir.listFiles();
		ArrayList<String> ps;
		for(File f:files){
			String fileName=f.getName();
			ps=analysis.obtainPermission(f.getAbsolutePath());
			if(ps!=null){
				String sql="insert into rootkit.apkpermission(apkname,permissionname,property) values (?,?,?)";//插入apkpermission表
				try {
					psts= conn.prepareStatement(sql);					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				for(String p:ps){
					try {
						psts.setString(1, fileName);
						psts.setString(2, p);
						psts.setInt(3, 1);
						psts.addBatch();  
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		        try {
					psts.executeBatch();// 执行批量处理  	
					conn.commit();  // 提交  
					psts.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 	 		        
			}
		}
		
		db.close();
	}
	
	public void obtainTotalApi(){
				
		ApkAnalysis analysis=new ApkAnalysis();
		conn=db.connect();
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 设置手动提交 
		PreparedStatement psts = null;
		
		//File rootDir=new File("E:\\AndroidTools\\APKTool\\MalwareTrain");//恶意软件
		File rootDir=new File("E:\\AndroidTools\\APKTool\\NormalTrain");//非恶意软件
		File[] files=rootDir.listFiles();
		Set set=new HashSet();
		
		File f=null;
		for(int count=333;count<files.length;count++){
			f=files[count];
			String fileName=f.getName();
			
			if(fileName.startsWith("com")){
				if(!fileName.startsWith("comz")){
					continue;
				}
			}
			set=analysis.obtainAPI(f.getAbsolutePath());
			
			if(set.size()>0){
				System.out.println((count+1)+"--APK："+fileName+"-----API个数为："+set.size());
				String sql="insert into rootkit.apkapi(apkname,apiname,property) values (?,?,?)";//插入apkapi表
				try {
					psts= conn.prepareStatement(sql);					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Iterator i = set.iterator();//先迭代出来  
		        while(i.hasNext()){//遍历  
		        	//System.out.println(i.next().toString());  
					try {
						psts.setString(1, fileName);
						psts.setString(2, i.next().toString().trim());
						psts.setInt(3, 1);
						psts.addBatch(); 
			            
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        } 
		        
		        try {
					psts.executeBatch();// 执行批量处理  	
					conn.commit();  // 提交  
					psts.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void countTotalPermission(){
		conn=db.connect();
		PreparedStatement psts = null;
		ResultSet rs = null;
		//读取permission总数
		String sql="select permissionname,count(distinct apkname) as c from rootkit.apkpermission group by permissionname";
		ArrayList<String> pCount=new ArrayList<String>();
		try {
			psts= conn.prepareStatement(sql);
			rs = psts.executeQuery();
			String pName=null;
			int c=0;
			while (rs.next()){
				pName=rs.getString("permissionname");
				c=rs.getInt("c");
				if(pName.startsWith("android.permission.")){
					pCount.add(pName.trim()+";"+c);
					System.out.println(pName+";"+c);
				}
			}
			psts.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//插入permissionstatistics
		sql="insert into rootkit.permissionstatistics(pname,malware,normal,total) values (?,?,?,?)";
		try {
			psts= conn.prepareStatement(sql);
			String name=null;
			int count=0;
			for(String s:pCount){
				String[] parts=s.split(";");
				count=Integer.parseInt(parts[1]);
				psts.setString(1, parts[0]);
				psts.setInt(2, 0);
				psts.setInt(3, 0);
				psts.setInt(4, count);
				psts.addBatch(); 
			}
			
			psts.executeBatch();// 执行批量处理  	
			psts.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		db.close();		
	}
	
	public void countPermissionDepart(){
		conn=db.connect();
		PreparedStatement psts = null;
		ResultSet rs = null;
		//读取非恶意软件的permission数
		String sql="select permissionname,count(distinct apkname) as c from rootkit.apkpermission where property=1 group by permissionname";
		ArrayList<String> pCount=new ArrayList<String>();
		try {
			psts= conn.prepareStatement(sql);
			rs = psts.executeQuery();
			String pName=null;
			int c=0;
			while (rs.next()){
				pName=rs.getString("permissionname");
				c=rs.getInt("c");
				if(pName.startsWith("android.permission.")){
					pCount.add(pName.trim()+";"+c);
					System.out.println(pName+";"+c);
				}
			}
			psts.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		//更新permissionstatistics
		sql="update permissionstatistics set normal=? where pname=?";
		try {
			psts= conn.prepareStatement(sql);
			int count=0;
			for(String s:pCount){
				String[] parts=s.split(";");
				count=Integer.parseInt(parts[1]);
				psts.setInt(1, count);
				psts.setString(2, parts[0]);
				
				psts.addBatch(); 
			}
			
			psts.executeBatch();// 执行批量处理  	
			psts.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		db.close();	
	}
	
	/*
	 * 卡方检验，选出相关性最大的前
	 */
	public void calulatePermissionX2(){
		conn=db.connect();
		/*
		 * select distinct(apkname) from rootkit.apkpermission where property=1;
		 */
		int normalApk=375;
		/*
		 * select distinct(apkname) from rootkit.apkpermission where property=0;
		 */
		int malwareApk=349;
		PreparedStatement psts = null;
		ResultSet rs = null;
		
		ArrayList<String> tmp=new ArrayList<String>();
		String sql="select pname,malware,normal,total from rootkit.permissionstatistics";
		try {
			psts= conn.prepareStatement(sql);
			rs = psts.executeQuery();
			int pInMalware;//有此权限的恶意软件
			int pInNormal;//有此权限的非恶意软件
			int pNotInMalware;//没有此权限的恶意软件
			int pNotInNormal;//没有此权限的非恶意软件
			double x2;//卡方检验结果
			while (rs.next()){
				String name=rs.getString("pname").trim();
				pInMalware=rs.getInt("malware");
				pInNormal=rs.getInt("normal");
				pNotInMalware=malwareApk-pInMalware;
				pNotInNormal=normalApk-pInNormal;
				
				//校正
				if(pInMalware<5 || pInNormal<5 || pNotInMalware<5 || pNotInNormal<5){
					double t=pInMalware*pNotInNormal-pInNormal*pNotInMalware;
					if(t<0){
						t=-t;
					}
					x2=(t-(normalApk+malwareApk)/2)*(t-(normalApk+malwareApk)/2)*(normalApk+malwareApk)*1.0;
				}else{
					x2=(pInMalware*pNotInNormal-pInNormal*pNotInMalware)*(pInMalware*pNotInNormal-pInNormal*pNotInMalware)*(normalApk+malwareApk)*1.0;			
				}
				
				x2=x2/((pInMalware+pInNormal)*(pNotInMalware+pNotInNormal)*malwareApk*normalApk*1.0);
				tmp.add(name.trim()+";"+x2);
				System.out.println(name+";"+x2);
			}
			psts.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//更新permissionstatistics
		sql="update permissionstatistics set statisticsresult=? where pname=?";
		try {
			psts= conn.prepareStatement(sql);
			double count=0;
			for(String s:tmp){
				String[] parts=s.split(";");
				count=Double.parseDouble(parts[1]);
				psts.setDouble(1, count);
				psts.setString(2, parts[0]);
				
				psts.addBatch(); 
			}
			
			psts.executeBatch();// 执行批量处理  	
			psts.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	/*
	 * 计算概率，用于贝叶斯分类
	 */
	public void calulatePermissionRate(){
		conn=db.connect();
		/*
		 * select distinct(apkname) from rootkit.apkpermission where property=1;
		 */
		double normalApk=375;
		/*
		 * select distinct(apkname) from rootkit.apkpermission where property=0;
		 */
		double malwareApk=349;		
		
		PreparedStatement psts = null;
		ResultSet rs = null;
		
		ArrayList<String> tmp=new ArrayList<String>();
		String sql="select pname,malware,normal,total from rootkit.permissionstatistics "
				+ "where statisticsresult>6.6 and total>50 order by statisticsresult desc";
		try {
			psts= conn.prepareStatement(sql);
			rs = psts.executeQuery();
			double pinmalware;
			double pnotinmalware;
			double pinnormal;
			double pnotinnormal;
			String pname;
			
			while(rs.next()){
				pname=rs.getString("pname");
				pinmalware=rs.getInt("malware")*1.0/malwareApk;
				pnotinmalware=1.0-pinmalware;
				pinnormal=rs.getInt("normal")*1.0/normalApk;
				pnotinnormal=1.0-pinnormal;
				tmp.add(pname.trim()+";"+pinmalware+";"+pnotinmalware+";"+pinnormal+";"+pnotinnormal);
				System.out.println(pname.trim()+";"+pinmalware+";"+pnotinmalware+";"+pinnormal+";"+pnotinnormal);
			}
			psts.close();
			rs.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//插入permissionrate
		sql="insert into rootkit.permissionrate values (?,?,?,?,?)";
		try {
			psts= conn.prepareStatement(sql);
			String name=null;
			int count=0;
			for(String s:tmp){
				String[] parts=s.split(";");
				psts.setString(1, parts[0]);
				psts.setDouble(2, Double.parseDouble(parts[1]));
				psts.setDouble(3, Double.parseDouble(parts[2]));
				psts.setDouble(4, Double.parseDouble(parts[3]));
				psts.setDouble(5, Double.parseDouble(parts[4]));

				psts.addBatch(); 
			}
			
			psts.executeBatch();// 执行批量处理  	
			psts.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void countTotalApi(){
		conn=db.connect();
		PreparedStatement psts = null;
		ResultSet rs = null;
		//读取api总数
		String sql="select apiname,count(distinct apkname) as c from rootkit.apkapi group by apiname";
		ArrayList<String> pCount=new ArrayList<String>();
		try {
			psts= conn.prepareStatement(sql);
			rs = psts.executeQuery();
			String pName=null;
			int c=0;
			while (rs.next()){
				pName=rs.getString("apiname").trim();
				c=rs.getInt("c");
				
				if(pName.charAt(0)>='A' && pName.charAt(0)<='Z'){
					pCount.add(pName+";"+c);
				}				
			}
			psts.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//插入apistatistics
		sql="insert into rootkit.apistatistics(apiname,malware,normal,total) values (?,?,?,?)";
		try {
			psts= conn.prepareStatement(sql);
			int count=0;
			for(String s:pCount){
				String[] parts=s.split(";");
				count=Integer.parseInt(parts[1]);
				psts.setString(1, parts[0]);
				psts.setInt(2, 0);
				psts.setInt(3, 0);
				psts.setInt(4, count);
				psts.addBatch(); 
			}
			
			psts.executeBatch();// 执行批量处理  	
			psts.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		db.close();		
	}
	
	public void countApiDepart(){
		conn=db.connect();
		PreparedStatement psts = null;
		ResultSet rs = null;
		//读取非恶意软件的api数
		String sql="select apiname,count(distinct apkname) as c from rootkit.apkapi where property=1 group by apiname";
		ArrayList<String> pCount=new ArrayList<String>();
		try {
			psts= conn.prepareStatement(sql);
			rs = psts.executeQuery();
			String pName=null;
			int c=0;
			
			while (rs.next()){
				pName=rs.getString("apiname").trim();
				c=rs.getInt("c");
				
				if(pName.charAt(0)>='A' && pName.charAt(0)<='Z'){
					pCount.add(pName+";"+c);
				}				
			}
			psts.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		//更新apistatistics
		sql="update apistatistics set normal=? where apiname=?";
		try {
			psts= conn.prepareStatement(sql);
			int count=0;
			for(String s:pCount){
				String[] parts=s.split(";");
				count=Integer.parseInt(parts[1]);
				psts.setInt(1, count);
				psts.setString(2, parts[0]);
				
				psts.addBatch(); 
			}
			
			psts.executeBatch();// 执行批量处理  	
			psts.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		db.close();	
	}
	
	/*
	 * 卡方检验，选出相关性最大的前
	 */
	public void calulateApiX2(){
		conn=db.connect();
		/*
		 * select distinct(apkname) from rootkit.apkapi where property=1;
		 */
		double normalApk=322;
		/*
		 * select distinct(apkname) from rootkit.apkapi where property=0;
		 */
		double malwareApk=313;
		PreparedStatement psts = null;
		ResultSet rs = null;
		
		ArrayList<String> tmp=new ArrayList<String>();
		String sql="select apiname,malware,normal,total from rootkit.apistatistics";
		try {
			psts= conn.prepareStatement(sql);
			rs = psts.executeQuery();
			double pInMalware;//有此权限的恶意软件
			double pInNormal;//有此权限的非恶意软件
			double pNotInMalware;//没有此权限的恶意软件
			double pNotInNormal;//没有此权限的非恶意软件
			double x2;//卡方检验结果
			while (rs.next()){
				String name=rs.getString("apiname").trim();
				pInMalware=rs.getInt("malware");
				pInNormal=rs.getInt("normal");
				pNotInMalware=malwareApk-pInMalware;
				pNotInNormal=normalApk-pInNormal;
				
				//校正
				if(pInMalware<5 || pInNormal<5 || pNotInMalware<5 || pNotInNormal<5){
					double t=pInMalware*pNotInNormal-pInNormal*pNotInMalware;
					if(t<0){
						t=-t;
					}
					x2=(t*1.0-(normalApk+malwareApk)*1.0/2)*(t*1.0-(normalApk+malwareApk)*1.0/2)*(normalApk+malwareApk)*1.0;
				}else{
					x2=(pInMalware*pNotInNormal-pInNormal*pNotInMalware)*(pInMalware*pNotInNormal-pInNormal*pNotInMalware)*(normalApk+malwareApk)*1.0;			
				}
				
				x2=x2/((pInMalware+pInNormal)*(pNotInMalware+pNotInNormal)*malwareApk*normalApk*1.0);
				tmp.add(name.trim()+";"+x2);
				System.out.println(name+";"+x2);
			}
			psts.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//更新apistatistics
		sql="update apistatistics set statisticsresult=? where apiname=?";
		try {
			psts= conn.prepareStatement(sql);
			double count=0;
			for(String s:tmp){
				String[] parts=s.split(";");
				count=Double.parseDouble(parts[1]);
				psts.setDouble(1, count);
				psts.setString(2, parts[0]);
				
				psts.addBatch(); 
			}
			
			psts.executeBatch();// 执行批量处理  	
			psts.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	/*
	 * 计算概率，用于贝叶斯分类
	 */
	public void calulateApiRate(){
		conn=db.connect();
		/*
		 * select distinct(apkname) from rootkit.apkapi where property=1;
		 */
		double normalApk=322;
		/*
		 * select distinct(apkname) from rootkit.apkapi where property=0;
		 */
		double malwareApk=313;		
		
		PreparedStatement psts = null;
		ResultSet rs = null;
		
		ArrayList<String> tmp=new ArrayList<String>();
		String sql="select apiname,malware,normal,total from rootkit.apistatistics "
				+ "where statisticsresult>100 and total>100 order by statisticsresult desc limit 80";
		try {
			psts= conn.prepareStatement(sql);
			rs = psts.executeQuery();
			double pinmalware;
			double pnotinmalware;
			double pinnormal;
			double pnotinnormal;
			String pname;
			
			while(rs.next()){
				pname=rs.getString("apiname");
				pinmalware=rs.getInt("malware")*1.0/malwareApk;
				pnotinmalware=1.0-pinmalware;
				pinnormal=rs.getInt("normal")*1.0/normalApk;
				pnotinnormal=1.0-pinnormal;
				tmp.add(pname.trim()+";"+pinmalware+";"+pnotinmalware+";"+pinnormal+";"+pnotinnormal);
				System.out.println(pname.trim()+";"+pinmalware+";"+pnotinmalware+";"+pinnormal+";"+pnotinnormal);
			}
			psts.close();
			rs.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//插入apirate
		sql="insert into rootkit.apirate values (?,?,?,?,?)";
		try {
			psts= conn.prepareStatement(sql);
			for(String s:tmp){
				String[] parts=s.split(";");
				psts.setString(1, parts[0]);
				psts.setDouble(2, Double.parseDouble(parts[1]));
				psts.setDouble(3, Double.parseDouble(parts[2]));
				psts.setDouble(4, Double.parseDouble(parts[3]));
				psts.setDouble(5, Double.parseDouble(parts[4]));

				psts.addBatch(); 
			}
			
			psts.executeBatch();// 执行批量处理  	
			psts.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
