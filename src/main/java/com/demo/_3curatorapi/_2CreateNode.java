package com.demo._3curatorapi;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.CreateMode;

/**
 * 创建节点
 *
 * @author jerome
 */
public class _2CreateNode {

	public static void main(String[] args) throws Exception {

		RetryPolicy retryPolicy = new RetryUntilElapsed(5000, 1000);

		CuratorFramework client = CuratorFrameworkFactory
				.builder()
				.connectString("192.168.10.5:2181")
				.sessionTimeoutMs(5000)
				.connectionTimeoutMs(5000)
				.retryPolicy(retryPolicy)
				.build();

		client.start();

		String path = client
				.create()
				.creatingParentsIfNeeded()
				.withMode(CreateMode.EPHEMERAL)
				.forPath("/node2",
				"123".getBytes());

		System.out.println(path);

		Thread.sleep(Integer.MAX_VALUE);
	}
	
	// console:
	// /node2
	
}
