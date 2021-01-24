package com.demo._6balance.server;

import java.util.ArrayList;
import java.util.List;

/**
 * 调度类
 * 
 * @author jerome
 */
public class ServerRunner {

	/** 服务器个数 */
	private static final int SERVER_QTY = 2;
	/** Zookeeper服务器地址 */
	private static final String ZOOKEEPER_SERVER = "192.168.10.5:2181";
	/** 服务注册节点 */
	private static final String SERVERS_PATH = "/servers";

	public static void main(String[] args) {

		List<Thread> threadList = new ArrayList<Thread>();

		for (int i = 0; i < SERVER_QTY; i++) {

			final Integer count = i;

			Thread thread = new Thread(new Runnable() {
				public void run() {
					ServerData serverData = new ServerData();
					serverData.setBalance(0);
					serverData.setHost("127.0.0.1");
					serverData.setPort(6000 + count);
					Server server = new ServerImpl(ZOOKEEPER_SERVER, SERVERS_PATH, serverData);
					server.bind();
				}
			});
			threadList.add(thread);

			thread.start();
		}

		for (int i = 0; i < threadList.size(); i++) {
			try {
				// 等待该线程终止
				threadList.get(i).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
