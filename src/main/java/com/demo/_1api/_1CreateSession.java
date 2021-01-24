package com.demo._1api;

import java.io.IOException;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

/**
 * 建立连接
 * 
 * @author jerome
 */
public class _1CreateSession implements Watcher {

	private static ZooKeeper zookeeper;

	public static void main(String[] args) throws IOException, InterruptedException {
		// 需要传递一个事件监听器，通过事件监听器来介绍zk的事件通知
		// 这里为了演示方便 直接实现watcher
		zookeeper = new ZooKeeper("192.168.10.5:2181", 5000, new _1CreateSession());

		// 获取zk状态并输出事件接收到的数据
		System.out.println(zookeeper.getState());

		// 因为main还没等到建立好连接就执行完退出了
		// 需要sleep,下面的监听事件才可以执行
		Thread.sleep(Integer.MAX_VALUE);
	}

	@Override
	public void process(WatchedEvent event) {
		System.out.println("收到事件：" + event);
		if (event.getState() == KeeperState.SyncConnected) {
			doSomething();
		}
	}

	private void doSomething() {
		System.out.println("do something");
	}
	
	/*
	输出：
	CONNECTING
	收到事件：WatchedEvent state:SyncConnected type:None path:null
	do something
	*/
}
