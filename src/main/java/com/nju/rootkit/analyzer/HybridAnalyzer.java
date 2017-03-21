package analyzer;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by xu on 2017/1/18.
 *
 * 混合分析器
 * 将DynamicAnalyzer的分析结果与静态分析结果相结合
 */
public class HybridAnalyzer implements Analyzer {
    //TODO 静态分析结合

    DynamicAnalyzer analyzer;
    @Override
    public File getGraph(File packageList,File log) {
        File file = null;
        try {
//            file = analyzer.getGraph("F:/sample.log","F:/config.ini","F:/packages.list");
            file = analyzer.getGraph(log,packageList,"F:/config.ini");
        } catch (FileNotFoundException e) {
            System.out.println("文件未找到！");
            e.printStackTrace();
        }
        return file;
    }

    public HybridAnalyzer(){
        analyzer = new DynamicAnalyzer();
    }
}
