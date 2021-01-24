package com.demo._7lock;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;

/**
 * 分布式锁 基础类
 * 主要用于和Zookeeper交互
 *
 * @author jerome
 * @date 2016/8/26 22:51
 */
public class BaseLock {

    private final ZkClient client;
    private final String path;
    private final String basePath;
    private final String lockName;

    /** 重试获取锁次数 */
    private static final Integer MAX_RETRY_COUNT = 10;

    public BaseLock(ZkClient client, String path, String lockName) {
        this.client = client;
        this.basePath = path;
        this.path = path.concat("/").concat(lockName);
        this.lockName = lockName;
    }

    /**
     * 等待获取锁
     * @param startMillis
     * @param millisToWait
     * @param ourPath
     * @return
     * @throws Exception
     */
    private boolean waitToLock(long startMillis, Long millisToWait, String ourPath) throws Exception {

        // 是否得到锁
        boolean haveTheLock = false;
        // 是否需要删除当前锁的节点
        boolean doDeleteOurPath = false;

        try {

            while (!haveTheLock) {

                // 获取所有锁节点(/locker下的子节点)并排序(从小到大)
                List<String> children = getSortedChildren();

                // 获取顺序节点的名字 如:/locker/lock-0000000013 > lock-0000000013
                String sequenceNodeName = ourPath.substring(basePath.length() + 1);

                // 判断该该节点是否在所有子节点的第一位 如果是就已经获得锁
                int ourIndex = children.indexOf(sequenceNodeName);
                if (ourIndex < 0) {
                    // 可能网络闪断 抛给上层处理
                    throw new ZkNoNodeException("节点没有找到: " + sequenceNodeName);
                }

                boolean isGetTheLock = (ourIndex == 0);

                if (isGetTheLock) {
                    // 如果第一位 已经获得锁
                    haveTheLock = true;
                } else {
                    // 如果不是第一位,监听比自己小的那个节点的删除事件
                    String pathToWatch = children.get(ourIndex - 1);
                    String previousSequencePath = basePath.concat("/").concat(pathToWatch);
                    final CountDownLatch latch = new CountDownLatch(1);
                    final IZkDataListener previousListener = new IZkDataListener() {

                        public void handleDataDeleted(String dataPath) throws Exception {
                            latch.countDown();
                        }

                        public void handleDataChange(String dataPath, Object data) throws Exception {
                        }
                    };

                    try {
                        client.subscribeDataChanges(previousSequencePath, previousListener);

                        if (millisToWait != null) {
                            millisToWait -= (System.currentTimeMillis() - startMillis);
                            startMillis = System.currentTimeMillis();
                            if (millisToWait <= 0) {
                                doDeleteOurPath = true;
                                break;
                            }

                            latch.await(millisToWait, TimeUnit.MICROSECONDS);
                        } else {
                            latch.await();
                        }
                    } catch (ZkNoNodeException e) {
                        e.printStackTrace();
                    } finally {
                        client.unsubscribeDataChanges(previousSequencePath, previousListener);
                    }

                }
            }
        } catch (Exception e) {
            //发生异常需要删除节点
            doDeleteOurPath = true;
            throw e;
        } finally {
            //如果需要删除节点
            if (doDeleteOurPath) {
                deleteOurPath(ourPath);
            }
        }

        return haveTheLock;
    }

    private String getLockNodeNumber(String str, String lockName) {
        int index = str.lastIndexOf(lockName);
        if (index >= 0) {
            index += lockName.length();
            return index <= str.length() ? str.substring(index) : "";
        }
        return str;
    }

    /**
     * 获取所有锁节点(/locker下的子节点)并排序
     *
     * @return
     * @throws Exception
     */
    private List<String> getSortedChildren() throws Exception {
        try {

            List<String> children = client.getChildren(basePath);
            Collections.sort
                    (
                            children,
                            new Comparator<String>() {
                                public int compare(String lhs, String rhs) {
                                    return getLockNodeNumber(lhs, lockName).compareTo(getLockNodeNumber(rhs, lockName));
                                }
                            }
                    );
            return children;

        } catch (ZkNoNodeException e) {
            client.createPersistent(basePath, true);
            return getSortedChildren();

        }
    }

    protected void releaseLock(String lockPath) throws Exception {
        deleteOurPath(lockPath);
    }

    /**
     * 尝试获取锁
     * @param timeOut
     * @param timeUnit
     * @return 锁节点的路径没有获取到锁返回null
     * @throws Exception
     */
    protected String tryGetLock(long timeOut, TimeUnit timeUnit) throws Exception {

        long startMillis = System.currentTimeMillis();
        Long millisToWait = (timeUnit != null) ? timeUnit.toMillis(timeOut) : null;

        String ourPath = null;
        boolean hasTheLock = false;
        boolean isDone = false;
        int retryCount = 0;

        //网络闪断需要重试一试
        while (!isDone) {
            isDone = true;

            try {
                // 在/locker下创建临时的顺序节点
                ourPath = createLockNode(client, path);

                // 判断你自己是否获得了锁，如果没获得那么我们等待直到获取锁或者超时
                hasTheLock = waitToLock(startMillis, millisToWait, ourPath);
            } catch (ZkNoNodeException e) {
                if (retryCount++ < MAX_RETRY_COUNT) {
                    isDone = false;
                } else {
                    throw e;
                }
            }
        }

        if (hasTheLock) {
            return ourPath;
        }

        return null;
    }

    private void deleteOurPath(String ourPath) throws Exception {
        client.delete(ourPath);
    }

    private String createLockNode(ZkClient client, String path) throws Exception {
        // 创建临时循序节点
        return client.createEphemeralSequential(path, null);
    }
}
