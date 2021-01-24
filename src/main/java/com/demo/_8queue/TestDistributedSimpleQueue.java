package com.demo._8queue;

import com.demo._8queue.model.User;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

/**
 * 测试简单的分布式队列
 *
 * @author jerome
 * @date 2016/8/30 19:48
 */
public class TestDistributedSimpleQueue {

    public static void main(String[] args) {

        ZkClient zkClient = new ZkClient("192.168.10.5:2181", 5000, 5000, new SerializableSerializer());
        DistributedSimpleQueue<User> queue = new DistributedSimpleQueue<>(zkClient, "/queue");

        User user1 = new User();
        user1.setId("1");
        user1.setName("jerome1");

        User user2 = new User();
        user2.setId("2");
        user2.setName("jerome2");

        try {
            queue.offer(user1);
            queue.offer(user2);
            System.out.println("queue.offer end!");

            User u1 = queue.poll();
            User u2 = queue.poll();
            System.out.println("queue.poll() u1 = " + u1.toString());
            System.out.println("queue.poll() u2 = " + u2.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /* console:
    queue.offer end!
    queue.poll() u1 = User{name='jerome1', id='1'}
    queue.poll() u2 = User{name='jerome2', id='2'}
    */
}
