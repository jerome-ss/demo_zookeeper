package com.demo._3curatorapi;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.data.Stat;

/**
 * 获取节点的数据
 *
 * @author jerome
 */
public class _5GetData {

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

		byte[] ret = client.getData().storingStatIn(stat).forPath("/node1");

		System.out.println(new String(ret));

		System.out.println(stat);
	}
	
	// console:
	// 111
	// 3501,3509,1471869513734,1471869728656,1,2,0,0,3,2,3503

}
