package com.demo._2zkclientapi;

import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

/**
 * 订阅节点的子节点变化（可以监听不存在的节点当他创建的时候接收到通知）
 *
 * @author jerome
 */
public class _8SubscribeChildChanges {

	public static void main(String[] args) throws InterruptedException {
		ZkClient zc = new ZkClient("192.168.10.5:2181", 10000, 10000, new SerializableSerializer());
		System.out.println("conneted ok!");
		zc.subscribeChildChanges("/node2", new ZkChildListener());
		Thread.sleep(Integer.MAX_VALUE);
	}
	
	private static class ZkChildListener implements IZkChildListener {
		public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
			System.out.println(parentPath);
			System.out.println(currentChilds.toString());
		}
	}
	
	// 在shell操作，可以看到对应的输出

}
