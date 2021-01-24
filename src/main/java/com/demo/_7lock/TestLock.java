package com.demo._7lock;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.BytesPushThroughSerializer;

import java.util.concurrent.TimeUnit;

/**
 * 测试分布式锁
 *
 * @author jerome
 * @date 2016/8/26 22:53
 */
public class TestLock {

    public static void main(String[] args) {

        // 需要手动创建节点 /locker

        ZkClient zkClient1 = new ZkClient("192.168.10.5:2181", 5000, 5000, new BytesPushThroughSerializer());
        LockImpl lock1 = new LockImpl(zkClient1, "/locker");

        ZkClient zkClient2 = new ZkClient("192.168.10.5:2181", 5000, 5000, new BytesPushThroughSerializer());
        final LockImpl lock2 = new LockImpl(zkClient2, "/locker");

        try {
            lock1.getLock();
            System.out.println("Client1 is get lock!");
            Thread client2Thd = new Thread(new Runnable() {

                public void run() {
                    try {
                        lock2.getLock();
//                        lock2.getLock(500, TimeUnit.SECONDS);
                        System.out.println("Client2 is get lock");
                        lock2.releaseLock();
                        System.out.println("Client2 is released lock");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client2Thd.start();

            // 5s 后lock1释放锁
            Thread.sleep(5000);
            lock1.releaseLock();
            System.out.println("Client1 is released lock");

            client2Thd.join();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /* console:
    Client1 is get lock!
    Client1 is released lock
    Client2 is get lock
    Client2 is released lock
    */
}
