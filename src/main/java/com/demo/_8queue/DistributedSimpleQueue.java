package com.demo._8queue;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import org.I0Itec.zkclient.ExceptionUtil;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;

/**
 * 简单的分布式队列
 *
 * @author jerome
 * @date 2016/8/30 20:19
 */
public class DistributedSimpleQueue<T> {

    protected final ZkClient zkClient;

    /**
     * 根节点路径
     */
    protected final String root;

    /**
     * 顺序节点的前缀
     */
    protected static final String Node_NAME = "n_";


    public DistributedSimpleQueue(ZkClient zkClient, String root) {
        this.zkClient = zkClient;
        this.root = root;
    }

    /**
     * 获取队列的大小
     *
     * @return
     */
    public int getQueueSize() {
        return zkClient.getChildren(root).size();
    }

    /**
     * 向队列提交数据
     *
     * @param element 提交的数据
     * @return
     * @throws Exception
     */
    public boolean offer(T element) throws Exception {

        String nodeFullPath = root.concat("/").concat(Node_NAME);
        try {
            // 创建持久的顺序节点
            zkClient.createPersistentSequential(nodeFullPath, element);
        } catch (ZkNoNodeException e) {
            zkClient.createPersistent(root);
            offer(element);
        } catch (Exception e) {
            throw ExceptionUtil.convertToRuntimeException(e);
        }
        return true;
    }


    /**
     * 从队列获取数据
     *
     * @return
     * @throws Exception
     */
    public T poll() throws Exception {

        try {

            List<String> list = zkClient.getChildren(root);
            if (list.size() == 0) {
                return null;
            }

            // 排序队列 根据名称由小到大
            Collections.sort(list, new Comparator<String>() {
                public int compare(String lhs, String rhs) {
                    return getNodeNumber(lhs, Node_NAME).compareTo(getNodeNumber(rhs, Node_NAME));
                }
            });

            for (String nodeName : list) {

                String nodeFullPath = root.concat("/").concat(nodeName);
                try {
                    T node = (T) zkClient.readData(nodeFullPath);
                    zkClient.delete(nodeFullPath);
                    return node;
                } catch (ZkNoNodeException e) {
                    // 其他客户端消费了 继续循环
                }
            }

            return null;

        } catch (Exception e) {
            throw ExceptionUtil.convertToRuntimeException(e);
        }

    }

    private String getNodeNumber(String str, String nodeName) {
        int index = str.lastIndexOf(nodeName);
        if (index >= 0) {
            index += Node_NAME.length();
            return index <= str.length() ? str.substring(index) : "";
        }
        return str;
    }

}
