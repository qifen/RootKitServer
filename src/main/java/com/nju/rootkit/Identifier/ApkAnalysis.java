package com.nju.rootkit.Identifier;

import java.io.IOException;

public class ApkAnalysis {
	
	/*
	 * 反编译APK
	 */
	public void DecompileApk(String apkPath,String apkName){
		Runtime runtime = Runtime.getRuntime();
		String cmd="";
		
		try {
			runtime.exec("mspaint.exe");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
