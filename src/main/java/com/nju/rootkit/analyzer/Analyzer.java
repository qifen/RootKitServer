package analyzer;

import java.io.File;

/**
 * Created by xu on 2017/1/18.
 * 分析模块的对外接口
 */
public interface Analyzer {
    // TODO: 2017/3/5  定义行为图种类
    File getGraph(File packageList,File log);
}
