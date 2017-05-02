package com.nju.rootkit.Identifier;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
	
    // 驱动程序名
    String driver = "com.mysql.jdbc.Driver";

    // URL指向要访问的数据库名
    String url = "jdbc:mysql://127.0.0.1:3306/rootkit";

    // MySQL配置时的用户名
    String user = "root"; 

    // MySQL配置时的密码
    String password = "";
    
    Connection conn=null;
    
    public Connection connect(){
              
        try {
        	 // 加载驱动程序
        	Class.forName(driver);
        	 // 连接数据库
			conn= DriverManager.getConnection(url, user, password);
			
			System.out.println("-----连接到数据库：rootkit");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        return conn;
    }
    
    public void close(){
    	try {
			conn.close();
			System.out.println("-----关闭与数据库的连接：rootkit");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }

}
