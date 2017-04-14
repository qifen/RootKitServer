package com.nju.rootkit.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nju.rootkit.analyzer.Analyzer;
import com.nju.rootkit.analyzer.HybridAnalyzer;
import com.nju.rootkit.util.ActionUtil;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class UploadLogFileAction extends ActionSupport{

    private File logFile;

    private String fileContentType;

    private String fileName;

    private String savePath;

    @Override
    public String execute() {
    	
        HttpServletRequest request=ServletActionContext.getRequest();
        try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}

        FileOutputStream fos = null;
        FileInputStream fis = null;
        String packageName=(String)request.getAttribute("PackageName");
        String packageName2=request.getParameter("PackageName");
        
        
        //File logFile=new File("F:/AndroidTools/log" + "/"  + packageName.trim());
        File logFile=new File("F:/AndroidTools/log" + "/"  + "sample");
        if(logFile.exists()){
        	logFile.delete();
        }
        try {
            File folder = new File("F:/AndroidTools/log");
            if (!(folder.exists() && folder.isDirectory()))
                folder.mkdirs();
			logFile.createNewFile();
		} catch (IOException e2) {
			System.out.println("创建日志文件失败！");
			e2.printStackTrace();
		}
        
        try {

            //fos = new FileOutputStream(getSavePath() + "/"  + getFileName());
            fos = new FileOutputStream(logFile);

            File in=(File)request.getAttribute("LogFile");
            fis = new FileInputStream(in);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            System.out.println("接收日志文件成功！" + getSavePath());
        } catch (Exception e) {
            System.out.println("接收日志文件失败！"+e.toString());
            e.printStackTrace();
        } finally {
            ActionUtil.close(fos, fis);
        }

        // TODO: 17/3/19  调用生成行为图方法
        Analyzer analyzer = new HybridAnalyzer();
        File packageList = new File("F:/AndroidTools/package/packages.list");
        File out = analyzer.getGraph(packageList,logFile);
        
        //File out = new File("F:/AndroidTools/pic/out.png");

        //返回生成图的流
        HttpServletResponse response=ServletActionContext.getResponse();
        response.setCharacterEncoding("UTF-8");
        ServletOutputStream sos=null;
        fis = null;
        try {
			sos=response.getOutputStream();
			//fis=new FileInputStream(savePath + "/pic/" + fileName);
			fis=new FileInputStream(out);
			InputStream input = new BufferedInputStream(fis);
			
	        byte[] bt = new byte[1024]; 
	        
	        while (input.read(bt) > 0) {  
	        	System.out.println("returnPic-----");
	        	sos.write(bt);
	        }

	        ServletActionContext.setResponse(response);
	        
	        System.out.println("返回图片流成功！");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("返回图片流失败！"+e.toString());
			e.printStackTrace();
		} finally{
			try {
				fis.close();
				sos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
        return SUCCESS;
    }

    /**
     * 
     *
     * @return
     */
    public String getSavePath() throws Exception{
        return ServletActionContext.getServletContext().getRealPath(savePath); 
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public File getLogFile() {
        return logFile;
    }

    public void setLogFile(File logFile) {
        this.logFile = logFile;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
