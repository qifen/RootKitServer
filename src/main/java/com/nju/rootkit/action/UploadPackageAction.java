package com.nju.rootkit.action;

import com.nju.rootkit.util.ActionUtil;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by weiyilin on 17/3/21.
 * 鑾峰彇Android绔紶鏉ョ殑packages.list鏂囦欢
 */
public class UploadPackageAction extends ActionSupport {
    // 鏂囦欢鍩�
    private File packages;
    // 鏂囦欢绫诲瀷
    private String packagesContentType;
    // 鏂囦欢鍚�
    private String packagesName;
    // 鎺ュ彈渚濊禆娉ㄥ叆鐨勫睘鎬�
    private String savePath;

    @Override
    public String execute() {
    	
    	System.out.println("enter UploadPackageAction");
    	
        HttpServletRequest request= ServletActionContext.getRequest();
        FileOutputStream fos = null;
        FileInputStream fis = null;
        try {
            request.setCharacterEncoding("UTF-8");

            //鍒涘缓鐩綍
            //File folder = new File(getSavePath());
            File folder = new File("F:/AndroidTools/package");
            if (!(folder.exists() && folder.isDirectory()))
                folder.mkdirs();
            //fos = new FileOutputStream(getSavePath() + "/"  + getPackagesName());
            fos = new FileOutputStream("F:/AndroidTools/package" + "/"  + "packages.list");

            /*
            File a=(File)request.getAttribute("packages");
            fis = new FileInputStream(a);            
            
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
            	System.out.println("-----"+len+"-----");
                fos.write(buffer, 0, len);
            }
            */
            
            //String fileContent=(String)request.getAttribute("packagesContent");
            String fileContent = request.getParameter("packagesContent").trim();
            byte[] bytes=fileContent.getBytes();
            fos.write(bytes);
            System.out.println("上传packages.list成功！" + getSavePath());
            return SUCCESS;
        } catch (Exception e) {
            System.out.println("上传packages.list失败！");
            e.printStackTrace();
            return ERROR;
        } finally {
            //ActionUtil.close(fos, fis);
        	try {
				fos.close();
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    /**
     * 鏂囦欢瀛樻斁鐩綍
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
