package com.demo._3curatorapi;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.data.Stat;

/**
 * 修改节点数据
 *
 * @author jerome
 */
public class _6UpdateData {

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

		Stat stat = new Stat();
		client.getData().storingStatIn(stat).forPath("/node1");

		client.setData().withVersion(stat.getVersion()).forPath("/node1", "111".getBytes());
	}

	// console:
	// 
}
