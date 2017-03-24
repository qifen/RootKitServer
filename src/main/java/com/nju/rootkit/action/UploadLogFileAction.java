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

import com.nju.rootkit.util.ActionUtil;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class UploadLogFileAction extends ActionSupport{
    // 上传文件域
    private File logFile;
    // 上传文件类型
    private String fileContentType;
    // 封装上传文件名
    private String fileName;
    // 接受依赖注入的属性
    private String savePath;

    @Override
    public String execute() {
        HttpServletRequest request=ServletActionContext.getRequest();
        //接收日志文件
        FileOutputStream fos = null;
        FileInputStream fis = null;
        try {
            request.setCharacterEncoding("UTF-8");

            //创建目录
            File folder = new File(getSavePath());
            if (!(folder.exists() && folder.isDirectory()))
                folder.mkdirs();
            fos = new FileOutputStream(getSavePath() + "/"  + getFileName());

            fis = new FileInputStream(getLogFile());
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            System.out.println("文件上传成功" + getSavePath());
        } catch (Exception e) {
            System.out.println("文件上传失败");
            e.printStackTrace();
        } finally {
            ActionUtil.close(fos, fis);
        }

        // TODO: 17/3/19  生成行为图部分
        /*
         *
         */

        //返回图片的流
        HttpServletResponse response=ServletActionContext.getResponse();
        ServletOutputStream sos=null;
        fis = null;
        try {
			sos=response.getOutputStream();
			fis=new FileInputStream(savePath + "/pic/" + fileName);
			InputStream input = new BufferedInputStream(fis);
			
	        byte[] bt = new byte[1024];  
	        while (input.read(bt) > 0) {  
	        	sos.write(bt);
	        }

            //不确定要不要
	        ServletActionContext.setResponse(response);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
     * 文件存放目录
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
