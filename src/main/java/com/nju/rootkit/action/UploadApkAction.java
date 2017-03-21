package com.nju.rootkit.action;

import com.nju.rootkit.util.ActionUtil;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by weiyilin on 17/3/19.
 * 获取Android端传来的apk文件
 */
@SuppressWarnings("serial")
public class UploadApkAction extends ActionSupport {
    // 文件域
    private File file;
    // 文件类型
    private String fileContentType;
    // 文件名
    private String fileName;
    // 接受依赖注入的属性
    private String savePath;

    @Override
    public String execute() {
        HttpServletRequest request= ServletActionContext.getRequest();
        FileOutputStream fos = null;
        ServletInputStream sis = null;
        try {
            request.setCharacterEncoding("UTF-8");
            fileName = request.getParameter("APK").trim();

            fos = new FileOutputStream(getSavePath()  + fileName);
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
            ActionUtil.close(fos, sis);
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


}
