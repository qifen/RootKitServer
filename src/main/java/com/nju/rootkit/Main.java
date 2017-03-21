import analyzer.Analyzer;
import analyzer.HybridAnalyzer;

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
        File log = new File("/sample2.log");
        File packageList = new File("F:/packages1.list");
        File out = analyzer.getGraph(packageList,log);
        System.out.println(out);
    }
}
