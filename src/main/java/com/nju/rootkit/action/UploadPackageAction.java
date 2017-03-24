package com.nju.rootkit.action;

import com.nju.rootkit.util.ActionUtil;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by weiyilin on 17/3/21.
 * 获取Android端传来的packages.list文件
 */
public class UploadPackageAction extends ActionSupport {
    // 文件域
    private File packages;
    // 文件类型
    private String packagesContentType;
    // 文件名
    private String packagesName;
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
            File folder = new File(getSavePath());
            if (!(folder.exists() && folder.isDirectory()))
                folder.mkdirs();
            fos = new FileOutputStream(getSavePath() + "/"  + getPackagesName());

            fis = new FileInputStream(getPackages());
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            System.out.println("文件上传成功" + getSavePath());
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

    public File getPackages() {
        return packages;
    }

    public void setPackages(File packages) {
        this.packages = packages;
    }

    public String getPackagesContentType() {
        return packagesContentType;
    }

    public void setPackagesContentType(String packagesContentType) {
        this.packagesContentType = packagesContentType;
    }

    public String getPackagesName() {
        return packagesName;
    }

    public void setPackagesName(String packagesName) {
        this.packagesName = packagesName;
    }
}
