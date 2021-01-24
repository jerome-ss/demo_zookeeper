package com.demo._3curatorapi;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;

/**
 * 删除节点
 *
 * @author jerome
 */
public class _3DelNode {

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

		// guaranteed 保障机制，只要连接还在，就算删除失败了也回一直在后台尝试删除
		client.delete().guaranteed().deletingChildrenIfNeeded().withVersion(-1).forPath("/node2");

		Thread.sleep(Integer.MAX_VALUE);
	}
	
	// console:
	// 
}
