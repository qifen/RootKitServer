package com.nju.rootkit.graphviz;

import com.nju.rootkit.analyzer.Node;

import java.io.File;
import java.util.*;

/**
 * Created by xu on 2017/3/5.
 */
public class MyGraphViz extends GraphViz implements Graph{
    private Map<Integer,GNode> nodes;
    private List<GNode> nodeList;

    public MyGraphViz(String executablePath , String tempDir){
        super(executablePath,tempDir);
        nodes = new HashMap<>();
    }

    public MyGraphViz(){
        super();
        nodes = new HashMap<>();
        nodeList = new ArrayList<>();
    }

    public void addLine(Node parent , Node child){
        GNode p = nodes.get(parent.getIndex());
        if (p == null){
            p = node2String(parent);
            nodes.put(p.getIndex(),p);
        }
        GNode c = nodes.get(child.getIndex());
        if (c == null){
            c = node2String(child);
            nodes.put(c.getIndex(),c);
        }

        this.addln(p.getIndex()+"->"+c.getIndex()+";");
    }

    private GNode node2String(Node node){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(node.getIndex());
        stringBuilder.append('\n');
        stringBuilder.append(node.getFunc());
        stringBuilder.append('\n');
        String parameter = node.getParam();
        for (int i = 0 ; i < parameter.length() ; i+=50){
            int start = (i/50)*50;
            int end = (i/50+1)*50;
            if (end > parameter.length()){
                end = parameter.length();
            }
            stringBuilder.append(parameter.substring(start,end));
            stringBuilder.append('\n');
        }
        GNode gNode = new GNode();
        gNode.setIndex(node.getIndex());
        gNode.setLabel(stringBuilder.toString());
        return gNode;
    }

    public void start(){
        this.addln(this.start_graph());
    }

    public void end(){
        Set<Map.Entry<Integer,GNode>> entries = this.nodes.entrySet();
        for (Map.Entry<Integer,GNode> entry:entries){
            addln(entry.getValue().toDOTSentence());
        }
        this.addln(this.end_graph());
    }

    public File getOutput(String path,String type){
        File out = new File(path);
        this.writeGraphToFile( this.getGraph(this.getDotSource(), type, "dot"), out );
        return  out;
    }
    
    public void print(){
    	for(int i=0;i<nodes.size();i++){
    		GNode n=nodes.get(i);
    		System.out.println(i+"："+n.getLabel()+"；"+n.getIndex()+"；");
    	}
    }
}
