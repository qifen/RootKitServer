package com.nju.rootkit;

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

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

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
        ServletInputStream sis = null;
        try {
            
            fileName=request.getParameter("LogFile").trim();
            
            fos = new FileOutputStream(getSavePath() + "/log/" + fileName);
            sis = request.getInputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = sis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            System.out.println("文件上传成功");
        } catch (Exception e) {
            System.out.println("文件上传失败");
            e.printStackTrace();
        } finally {
            close(fos, sis);
        }
        
        //生成行为图部分
        /*
         * 
         */
        
        //返回图片的流
        HttpServletResponse response=ServletActionContext.getResponse();
        ServletOutputStream sos=null;
        FileInputStream fis = null;
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

    private void close(FileOutputStream fos, ServletInputStream fis) {
        if (fis != null) {
            try {
                fis.close();
                fis=null;
            } catch (IOException e) {
                System.out.println("ServletInputStream关闭失败");
                e.printStackTrace();
            }
        }
        if (fos != null) {
            try {
                fos.close();
                fis=null;
            } catch (IOException e) {
                System.out.println("FileOutputStream关闭失败");
                e.printStackTrace();
            }
        }
    }

}
