package com.demo._8queue;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

/**
 * 简单的分布式队列(阻塞) 比较合理的做法
 *
 * @author jerome
 * @date 2016/8/30 20:19
 */
public class DistributedBlockingQueue<T> extends DistributedSimpleQueue<T> {

    public DistributedBlockingQueue(ZkClient zkClient, String root) {
        super(zkClient, root);
    }

    /**
     * 一直阻塞 一有数据就消费然后继续阻塞
     *
     * @return
     * @throws Exception
     */
    @Override
    public T poll() throws Exception {

        while (true) {

            final CountDownLatch latch = new CountDownLatch(1);
            final IZkChildListener childListener = new IZkChildListener() {

                public void handleChildChange(String parentPath, List<String> currentChilds)
                        throws Exception {
                    latch.countDown();

                }
            };

            zkClient.subscribeChildChanges(root, childListener);

            try {
                T node = super.poll();
                if (node != null) {
                    return node;
                } else {
                    latch.await();
                }
            } finally {
                zkClient.unsubscribeChildChanges(root, childListener);
            }
        }
    }


}
