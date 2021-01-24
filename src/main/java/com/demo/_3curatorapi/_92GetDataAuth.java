package com.demo._3curatorapi;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.data.Stat;

/**
 * 权限 - 创建节点
 *
 * @author jerome
 */
public class _92GetDataAuth {

	public static void main(String[] args) throws Exception {

		RetryPolicy retryPolicy = new RetryUntilElapsed(5000, 1000);

		CuratorFramework client = CuratorFrameworkFactory
				.builder()
				.connectString("192.168.10.5:2181")
				.sessionTimeoutMs(5000)
				.authorization("digest", "jerome:123456".getBytes())
				.connectionTimeoutMs(5000)
				.retryPolicy(retryPolicy)
				.build();

		client.start();

		Stat stat = new Stat();

		byte[] ret = client.getData().storingStatIn(stat).forPath("/node3");

		System.out.println(new String(ret));

		System.out.println(stat);
	}
	
	
	// console:
	// 123
	// 3541,3541,1471871072020,1471871072020,0,0,0,0,3,0,3541

}
