package com.nju.rootkit;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by weiyilin on 17/3/19.
 * 获取Android端传来的apk文件
 */
@SuppressWarnings("serial")
public class UploadApkAction extends ActionSupport {
//    // 文件域
//    private File file;
//    // 文件类型
//    private String fileContentType;
//    // 文件名
//    private String fileName;
//    // 接受依赖注入的属性
//    private String savePath;
//    // 包名
//    private String packageName;
//
//    @Override
//    public String execute() {
//        HttpServletRequest request= ServletActionContext.getRequest();
//        FileOutputStream fos = null;
//        FileInputStream fis = null;
//        try {
//            request.setCharacterEncoding("UTF-8");
//            packageName = request.getParameter("PackageName");
//
//            fos = new FileOutputStream(getSavePath() + "/" + getFileName());
//            fis = new FileInputStream(getFile());
//            byte[] buffer = new byte[1024];
//            int len = 0;
//            while ((len = fis.read(buffer)) != -1) {
//                fos.write(buffer, 0, len);
//            }
//            System.out.println("文件上传成功");
//        } catch (Exception e) {
//            System.out.println("文件上传失败");
//            e.printStackTrace();
//        } finally {
//            close(fos, fis);
//        }
//        return SUCCESS;
//    }
//
//    /**
//     * 文件存放目录
//     *
//     * @return
//     */
//    public String getSavePath() throws Exception{
//        return ServletActionContext.getServletContext().getRealPath(savePath);
//    }
//
//    public void setSavePath(String savePath) {
//        this.savePath = savePath;
//    }
//
//    public File getFile() {
//        return file;
//    }
//
//    public void setFile(File file) {
//        this.file = file;
//    }
//
//    public String getFileContentType() {
//        return fileContentType;
//    }
//
//    public void setFileContentType(String fileContentType) {
//        this.fileContentType = fileContentType;
//    }
//
//    public String getFileName() {
//        return fileName;
//    }
//
//    public void setFileName(String fileName) {
//        this.fileName = fileName;
//    }
//
//    public String getPackageName() {
//        return packageName;
//    }
//
//    public void setPackageName(String packageName) {
//        this.packageName = packageName;
//    }
//
//    private void close(FileOutputStream fos, FileInputStream fis) {
//        if (fis != null) {
//            try {
//                fis.close();
//                fis=null;
//            } catch (IOException e) {
//                System.out.println("FileInputStream关闭失败");
//                e.printStackTrace();
//            }
//        }
//        if (fos != null) {
//            try {
//                fos.close();
//                fis=null;
//            } catch (IOException e) {
//                System.out.println("FileOutputStream关闭失败");
//                e.printStackTrace();
//            }
//        }
//    }
}
