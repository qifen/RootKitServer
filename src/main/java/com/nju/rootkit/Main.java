package com.nju.rootkit;

import com.nju.rootkit.analyzer.*;

import java.io.File;

/**
 * Created by xu on 2017/3/11.
 */

/**
 * 测试类
 */
public class Main {
    /**
     * 测试方法
     * @param args
     */
    public static void main(String[] args) {
        Analyzer analyzer = new HybridAnalyzer();
        File log = new File("F:/AndroidTools/log/sample.log");
        File packageList = new File("F:/AndroidTools/package/packages.list");
        File out = analyzer.getGraph(packageList,log);
        
        System.out.println(out);

    }
}
