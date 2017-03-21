package com.nju.rootkit.util;

import javax.servlet.ServletInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by weiyilin on 17/3/19.
 */
public class ActionUtil {
    public static void close(FileOutputStream fos, ServletInputStream sis) {
        if (sis != null) {
            try {
                sis.close();
                sis=null;
            } catch (IOException e) {
                System.out.println("ServletInputStream关闭失败");
                e.printStackTrace();
            }
        }
        if (fos != null) {
            try {
                fos.close();
                fos=null;
            } catch (IOException e) {
                System.out.println("FileOutputStream关闭失败");
                e.printStackTrace();
            }
        }
    }
}
