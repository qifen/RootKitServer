package com.nju.rootkit.action;

import com.nju.rootkit.graphviz.Identify;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by wei on 2017/4/10.
 */
public class UploadIdentifyAction extends ActionSupport {

    @Override
    public String execute() throws Exception {
        HttpServletResponse response= ServletActionContext.getResponse();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        System.out.println("start UploadIdentifyAction");

        int status = 1;
        if (Identify.content.equals("")){
            status = 0;
        }
        String jsonStr = "{\"content\":\"" + Identify.content +"\",\"status\":\"" + status + "\"}";
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.write(jsonStr);
            System.out.println("finish UploadIdentifyAction");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return SUCCESS;
    }
}
