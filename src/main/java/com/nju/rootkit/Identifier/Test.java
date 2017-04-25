package com.nju.rootkit.Identifier;

public class Test {
	
	public static void main(String[] args){
		ApkAnalysis a=new ApkAnalysis();
		
		//a.decompileApk("E:\\AndroidTools\\APKTool\\APKs", "com.sinelife.theone.apk");
		//a.obtainPermission();
		a.obtainAPI();
		
		APIExtracting b=new APIExtracting();
		//b.extractAPIs("E:\\AndroidTools\\Android-19-API\\android");
	}

}
