package com.demo._9nameservice;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.I0Itec.zkclient.serialize.BytesPushThroughSerializer;

/**
 * 分布式的id生成器
 *
 * @author jerome
 * @date 2016/8/27 9:46
 */
public class IdMaker {

    private ZkClient client = null;

    /** 记录Zookeeper服务器的地址 */
    private final String server;

    /** 记录父节点的路径 */
    private final String root;

    /** 顺序节点的名称前缀 */
    private final String nodeName;

    /** 标识当前服务是否运行 */
    private volatile boolean isRunning = false;

    /** 使用线程池 */
    private ExecutorService cleanExector = null;

    public IdMaker(String zkServer, String root, String nodeName) {
        this.root = root;
        this.server = zkServer;
        this.nodeName = nodeName;
    }

    public void start() throws Exception {
        if (isRunning){
            throw new Exception("server has stated...");
        }
        isRunning = true;
        init();
    }


    public void stop() throws Exception {
        if (!isRunning)
            throw new Exception("server has stopped...");
        isRunning = false;
        freeResource();
    }


    private void init() {
        client = new ZkClient(server, 5000, 5000, new BytesPushThroughSerializer());
        // 实例化线程池
        cleanExector = Executors.newFixedThreadPool(10);
        try {
            // 创建持久节点
            client.createPersistent(root, true);
        } catch (ZkNodeExistsException e) {
            e.printStackTrace();
        }
    }

    private void freeResource() {
        // 释放线程池
        cleanExector.shutdown();
        try {
            cleanExector.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            cleanExector = null;
        }

        if (client != null) {
            client.close();
            client = null;
        }
    }

    public String generateId(RemoveMethodEnum removeMethod) throws Exception {
        final String fullNodePath = root.concat("/").concat(nodeName);
        // 创建持久顺序节点
        final String ourPath = client.createPersistentSequential(fullNodePath, null);

        if (removeMethod.equals(RemoveMethodEnum.IMMEDIATELY)) {
            client.delete(ourPath);
        } else if (removeMethod.equals(RemoveMethodEnum.DELAY)) {
            cleanExector.execute(new Runnable() {
                public void run() {
                    client.delete(ourPath);
                }
            });
        }

        // ID0000000001 > 0000000001
        return ExtractId(ourPath);
    }

    /**
     * 抽取ID
     *
     * @param str
     * @return
     */
    private String ExtractId(String str) {
        int index = str.lastIndexOf(nodeName);
        if (index >= 0) {
            index += nodeName.length();
            return index <= str.length() ? str.substring(index) : "";
        }
        return str;
    }

}
