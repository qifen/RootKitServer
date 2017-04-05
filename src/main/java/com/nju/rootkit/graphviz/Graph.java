package com.nju.rootkit.graphviz;

import com.nju.rootkit.analyzer.Node;

import java.io.File;

/**
 * Created by xu on 2017/3/11.
 */
public interface Graph {
    public void addLine(Node parent , Node child);
    public void start();
    public void end();
    public File getOutput(String path, String type);
    public void print();//修改
}
