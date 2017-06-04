package com.nju.rootkit.action;

import com.nju.rootkit.Identifier.Identifier;
import com.nju.rootkit.util.ActionUtil;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Created by weiyilin on 17/3/19.
 * 获取Android端传来的apk文件
 */
@SuppressWarnings("serial")
public class UploadApkAction extends ActionSupport {
    // 文件域
    private File apk;
    // 文件类型
    private String apkContentType;
    // 文件名
    private String apkName;
    // 接受依赖注入的属性
    private String savePath;

    @Override
    public String execute() {
        HttpServletRequest request= ServletActionContext.getRequest();
        FileOutputStream fos = null;
        FileInputStream fis = null;
        try {
            request.setCharacterEncoding("UTF-8");

            //创建目录
//            File folder = new File(getSavePath());
//            if (!(folder.exists() && folder.isDirectory()))
//                folder.mkdirs();
    		//生成随机数，形成文件名
    		Random random = new Random();
            int a=random.nextInt(5000);
            fos = new FileOutputStream("E:\\AndroidTools\\apk\\"+a+"-"+getApkName());

            fis = new FileInputStream(getApk());
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            System.out.println("文件上传成功!"+getApkName());
            
            Identifier identifier=new Identifier();
            String result=identifier.getAnalysisResult(a+"-"+getApkName());
                        
            return SUCCESS;
        } catch (Exception e) {
            System.out.println("文件上传失败");
            e.printStackTrace();
            return ERROR;
        } finally {
            ActionUtil.close(fos, fis);
        }
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

    public File getApk() {
        return apk;
    }

    public void setApk(File apk) {
        this.apk = apk;
    }

    public String getApkContentType() {
        return apkContentType;
    }

    public void setApkContentType(String apkContentType) {
        this.apkContentType = apkContentType;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }
}
