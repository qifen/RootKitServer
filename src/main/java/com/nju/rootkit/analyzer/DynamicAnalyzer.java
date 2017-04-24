package com.nju.rootkit.analyzer;

import com.nju.rootkit.graphviz.Graph;
import com.nju.rootkit.graphviz.MyGraphViz;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xu on 2017/1/18.
 *
 *  动态分析器
 *
 */
public class DynamicAnalyzer {
    private List<Node> nodes;
    private List<Edge> edgs;
    private Map<Integer,Map<Integer,Node>> map;//<uid,<pid,node>> 只存储树叶，用于继续向后生长
//    private List<String> config;
    private Map<String,Integer> packageListName2ID;
    private Map<Integer,String> packageListID2Name;
    private List<Node> apps;
    private Graph graph;
    /**
     * 实现图生成算法，将原始日志信息转换成为初级行为图
     * 生成初级行为图：树形节点序列，节点内部保存节点之间的关系
     * pass
     */
    private void drawPrimaryGraph(File logFile) throws FileNotFoundException {
        List<String> logList = getLog(logFile);
        nodes.add(new Node());//root
        for (int logIndex = 1; logIndex < logList.size() ; logIndex++){//解析所有的日志为node list
            Node n = logParser(logList.get(logIndex));
            if( n != null){
                n.setIndex(nodes.size());
                nodes.add(n);
            }
        }
        String cloneFunc = "clone";
        for (int nodeIndex = 1; nodeIndex < nodes.size() ; nodeIndex ++){//遍历每一行日志
            Node node = nodes.get(nodeIndex);
//            if (map.containsKey(node.getUid()) && node.getUid() != 1000){//应用程序节点已经创建
            if (map.containsKey(node.getUid())){
                Map<Integer,Node> m = map.get(node.getUid());
                if (m.containsKey(node.getPid())){//进程节点已经创建
                    if (node.getFunc().equals(cloneFunc)){//clone方法,参数只有cid
                        int cid = Integer.parseInt(node.getParam());
                        build(m.get(node.getPid()),node,node.getUid(),node.getPid(),cid);
                    }else {//不是clone方法
                        build(m.get(node.getPid()),node,node.getUid(),node.getPid(),0);
                    }
                }else {//某应用的一个新进程
                    m.put(node.getPid(),node);
                    build(nodes.get(0),node,node.getUid(),node.getPid(),node.getCid());
                }
            }else {//第一个应用程序节点
                if (node.getUid() != 1000) {//不是scheduleReceiver方法，给应用程序创建一个节点，再把行为连在后面
                    String func = packageListID2Name.get(node.getUid());
                    if (func != null){//新的应用程序节点，直接连在根节点上
                        Node app = new Node();
                        app.setParam("");
                        app.setFunc(func);
                        app.setUid(node.getUid());
                        app.setPid(node.getPid());
                        app.setApp(1);
                        app.setIndex(0-(apps.size()+1));
                        apps.add(app);
                        Map<Integer,Node> m = new HashMap<>();
                        m.put(app.getPid(),app);  //加入第一个进程节点
                        map.put(app.getUid(),m);   //加入第一个应用程序节点
                        build(nodes.get(0),app,0,0,0);
                        //
                        build(app,node,node.getUid(),node.getPid(),node.getCid());
                    }
//                    Map<Integer,Node> m = map.get(node.getUid());
//                    build(m.get(node.getPid()),node,node.getUid(),node.getPid(),node.getCid());
                }else {//是schedule方法，把它连到根节点上
                    Map<Integer,Node> m = new HashMap<>();
                    m.put(node.getPid(),node);  //加入第一个进程节点
                    map.put(node.getUid(),m);   //加入第一个应用程序节点
                    build(nodes.get(0),node,0,0,0);
                }
            }
        }
        for (Node app : apps){
            app.setIndex(nodes.size());
            nodes.add(app);
        }
    }

    private void build(Node parent ,Node child , int uid , int pid , int cid){
        Edge e = new Edge();
        e.setTo(child);
        e.setNext(parent.getNext());
        parent.setNext(e);
        parent.setChild(parent.getChild()+1);
        child.setParent(parent);
        //将最新的动作（当前node）做为最新的树叶节点
        //也就是说，后续的动作跟在当前node后面，总是保存最新的动作
        if (uid != 0 && cid == 0){
            map.get(uid).remove(pid);
            map.get(uid).put(pid,child);
        }
        if (cid != 0){
            child.setCid(cid);
            map.get(uid).put(cid,child);
        }
        edgs.add(e);
//        graph.addLine(parent,child);
    }

    /**
     * 实现广播生命周期匹配算法
     * 生成语义更加丰富的行为图
     *
     * 监控scheduleRegisteredReceiver -> finishReceiver (动态注册的广播生命周期)
     *     scheduleReceiver -> finishReceiver (静态注册的广播生命周期)
     * pass
     */
    private void matchBroadCastLifeCycle(){
        String dynamicScheduleFunc = "scheduleRegisteredReceiver";
        String staticScheduleFunc = "scheduleReceiver";
        String finishFunc = "finishReceiver";
        List<Integer> queue = new LinkedList<>();//schdule方法对应节点在graph中的位置
        int isMatch = 0;
        Node cnode;                  //当前节点
        Node pnode;                  //当前节点的父节点
        int last = 0;                   //上一个广播周期结束节点
        for (int i = 1; i < nodes.size() ; i ++){//遍历所有node，注意 0 是root，不是实际node
            Node node = nodes.get(i);
            if (node.getFunc().equals(dynamicScheduleFunc) || node.getFunc().equals(staticScheduleFunc)) {
                //是schedule方法
                String[] params = getParams(node.getParam());
                if (node.getFunc().equals(dynamicScheduleFunc)){//获取pid
                    int id = Integer.parseInt(params[params.length-1]);
                    node.setMatchid(id);
                }else {//staticScheduleFunc，获取uid
                    Integer id = packageListName2ID.get(params[params.length-1]);
                    if (id != null){//成功匹配
                        node.setMatchid(id);
                    }else {//未匹配到
                        node.setMatchid(0);
                    }
                }
                queue.add(i);
            }else {
                if (node.getFunc().equals(finishFunc)){//是finish方法
                    Node scheduleNode = null;
                    int scheduleIndex = 0;
                    for (Integer index : queue){//遍历队列中的所有schedule方法
                        Node n = nodes.get(index);
                        scheduleNode = n;
                        scheduleIndex = index;
                        if (n.getFunc().equals(staticScheduleFunc)){
                            if (node.getUid() == n.getMatchid()){
                                //finish方法的uid与scheduleReceiver方法相匹配
                                isMatch = 1;
                                break;
                            }
                        }else {
                            if (node.getPid() == n.getMatchid()){
                                //finish方法的pid与scheduleRegisterReceiver方法相匹配
                                isMatch = 1;
                                break;
                            }
                        }
                    }
                    if (isMatch == 1){
                        cnode = pnode = node;
                        do {
                            cnode = pnode;
                            pnode = pnode.getParent();
                        }while (pnode.getPid() == node.getPid() && pnode.getApp() != 1 && pnode.getIndex() > last && pnode.getIndex() > scheduleIndex);
                        //上一行：此时，pnode是在寻找schedule的开始节点，i是finish节点，last是上一个schedule的结束节点
                        build(scheduleNode,cnode,0,0,0);
                        build(scheduleNode,node,0,0,0);
                        last = i;
                        queue.remove(scheduleNode);
                        isMatch = 0;
//                        break;    //我觉得应该是continue,不可能只匹配一次啊
                        continue;
                    }
                }
            }
        }
    }

    //将用逗号连接的参数分离开
    private String[] getParams(String params){
        String[] p = params.split(",");
        for (int i = 0 ; i < p.length ; i ++){
            p[i] = p[i].trim();
        }
        return p;
    }



    /**
     * 使用图精简算法消除图中的冗余信息
     * 同时画出精简后的分析图
     */
    private void  simplifyGraph(int u,Map<String,Integer> keywords,int[] inside,Graph graph){
        Node node = nodes.get(u);
        int flag = 0;
        if (node.isFinish()==false  && node.isSchedule() == false && node.getParent() != null && node.getParent().isSchedule()){
            //flag = 1标志着这是合并节点的第一个子节点，将开始进行合并
            flag = 1;
        }
        if (node.isFinish()){
            keywords.clear();
        }
        for(Edge e = node.getNext();e != null;e = e.getNext()){//遍历当前节点的直接子节点
            Node child = e.getTo();
            if (child.isFinish()){
                continue;
            }
            if (flag == 1){     //flag=1，接下来遍历到的节点，直到遇到finish节点都会被包裹进合并节点
                inside[0] = 1;  //标志着接下来的节点要被包裹
                simplifyGraph(child.getIndex(),keywords,inside,graph);
            }else {             //flag=0，当前节点需要收集关键字
                simplifyGraph(child.getIndex(),keywords,inside,graph);
                if (child.isFileAccess()){
                    int times = 0;
                    if (keywords.get("FILE_ACCESS") != null){
                        times = keywords.get("FILE_ACCESS");
                    }
                    keywords.put("FILE_ACCESS",times+1);
                }else if (child.isNetworkAccess()){
                    int times = 0;
                    if (keywords.get("NETWORK_ACCESS") != null){
                        times = keywords.get("NETWORK_ACCESS");
                    }
                    keywords.put("NETWORK_ACCESS",times+1);
                }else if (child.isCheckPermission()){
                    keywords.put(child.getParam(),1);
                }
                if (inside[0] == 0){
                    graph.addLine(node,child);
                }
            }
        }

        if (flag == 1){
            //当前节点是组合节点的第一个子节点，要把收集到的Keywords塞进组合节点做为func
            //其中，组合节点的第一个子节点做为组合节点的代言人
            String context = "";
            Set<Map.Entry<String,Integer>> entries = keywords.entrySet();
            for (Map.Entry<String,Integer> entry : entries){
                context += "\n" + entry.getKey() +"   "+ entry.getValue();
            }
            node.setFunc(context);
            node.setParam("");
            keywords.clear();   //清除已经收集好的关键字
            inside[0] = 0;      //标识着一次合并过程完成
        }
    }

    /**
     * 解析日志信息，生成孤立的node
     * @param log 一条日志信息
     * @return 图节点
     */
    private Node logParser(String log){
        String regexPattern = "\\[Rootkit\\]\\((\\d+)\\)\\((\\d+)\\)([^\\n(]*)\\((.*)\\)";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(log);
        if (matcher.find()){
            int pid = Integer.parseInt(matcher.group(1));
            int uid = Integer.parseInt(matcher.group(2));
            String method = matcher.group(3);
            String param = matcher.group(4);
            if (param.length() > 100){
                param = param.substring(0,100);
            }
            Node n = new Node();
            n.setUid(uid);
            n.setPid(pid);
            n.setFunc(method);
            n.setParam(param);
            return n;
        }
        return null;
    }

    //===========================================辅助函数=============================================

    public DynamicAnalyzer(){
        map = new HashMap<>();
        nodes = new LinkedList<>();
        edgs = new ArrayList<>();
//        config = new ArrayList<>();
        packageListID2Name = new HashMap<>();
        packageListName2ID = new HashMap<>();
        apps = new ArrayList<>();
//        graph = new MyGraphViz();
    }

    /**
     * 将日志读入内存
     * @param logFile 日志文件对象
     * @return 一条一条的日志信息
     */
    private List<String> getLog(File logFile) throws FileNotFoundException {
        List<String> logList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(logFile));
        System.out.println(logFile.getName());
        String log;
        int limit = 300;
        try {
            while ((log = reader.readLine()) != null){
                logList.add(log);
                limit--;
                if (limit == 0){
                    break;
                }
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("log 文件未找到");
            e.printStackTrace();
        }
        return logList;
    }

    /**
     * 获取抽象行为图，以父子树表示
     * @param logFile : log文件路径
     * @param packageList : packages.list文件路径
     * @return 抽象行为图
     * @throws FileNotFoundException
     */
    public File getGraph(File logFile,File packageList) throws FileNotFoundException {
        Properties config = getModuleConfig();
        getPackageList(packageList);
        graph = new MyGraphViz(config.getProperty("executable"),config.getProperty("tempDir"));
        // graph = new MyGraphViz();
        graph.start();
        drawPrimaryGraph(logFile);
        matchBroadCastLifeCycle();
        simplifyGraph(0,new TreeMap<>(),new int[]{0},graph);
        graph.end();
        
        return graph.getOutput(config.getProperty("out"),config.getProperty("outType"));
    }

    private Properties getModuleConfig(){
        Properties properties = null;
        try {
            InputStream inputStream = DynamicAnalyzer.class.getResourceAsStream("/config.properties");
            properties = new Properties();
            properties.load(inputStream);
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  properties;
    }


//    private void getConfig(String path){//读取config.ini内容放进list中,这个文件还可以优化一下
//        File file = new File(path);
//        try {
//            BufferedReader reader = new BufferedReader(new FileReader(file));
//            String line = null;
//            while((line = reader.readLine()) != null){
////                config.add(line);
//            }
//            reader.close();
//        } catch (IOException e) {
//            System.err.println("config.ini文件未找到");
//            e.printStackTrace();
//        }
//    }

    private void getPackageList(File packageList){//读取packages.list，内容放进map中
        File file = packageList;
        String regexPattern = "([0-9A-Za-z._]*) ([0-9]*)";
        Pattern pattern = Pattern.compile(regexPattern);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null){
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()){
                    String packageName = matcher.group(1);
                    int id = Integer.parseInt(matcher.group(2));
                    packageListName2ID.put(packageName,id);
                    packageListID2Name.put(id,packageName);
                }
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("packages.list 未找到");
            e.printStackTrace();
        }
    }

}
