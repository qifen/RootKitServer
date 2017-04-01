package com.nju.rootkit.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
    	System.out.println("enter UploadLogFileAction");
    	
        HttpServletRequest request=ServletActionContext.getRequest();

        FileOutputStream fos = null;
        FileInputStream fis = null;
        String packageName=null;
        try {
            request.setCharacterEncoding("UTF-8");

            //File folder = new File(getSavePath());
            File folder = new File("F:/AndroidTools/log");
            if (!(folder.exists() && folder.isDirectory()))
                folder.mkdirs();
            //fos = new FileOutputStream(getSavePath() + "/"  + getFileName());
            packageName=(String)request.getAttribute("PackageName");
            fos = new FileOutputStream("F:/AndroidTools/log" + "/"  + packageName.trim());

            File in=(File)request.getAttribute("logFile");
            fis = new FileInputStream(in);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            System.out.println("接收日志文件成功！" + getSavePath());
        } catch (Exception e) {
            System.out.println("接收日志文件失败！");
            e.printStackTrace();
        } finally {
            ActionUtil.close(fos, fis);
        }

        // TODO: 17/3/19  调用生成行为图方法
        Analyzer analyzer = new HybridAnalyzer();
        File log = null;
		try {
			log = new File("F:/AndroidTools/log" + "/"  + packageName.trim());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        File packageList = new File("F:/AndroidTools/package/packages.list");
        File out = analyzer.getGraph(packageList,log);

        //返回生成图的流
        HttpServletResponse response=ServletActionContext.getResponse();
        ServletOutputStream sos=null;
        fis = null;
        try {
			sos=response.getOutputStream();
			//fis=new FileInputStream(savePath + "/pic/" + fileName);
			fis=new FileInputStream(out);
			InputStream input = new BufferedInputStream(fis);
			
	        byte[] bt = new byte[1024];  
	        while (input.read(bt) > 0) {  
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
