package com.demo._3curatorapi;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.data.Stat;

/**
 * 判断节点是否存在
 *
 * @author jerome
 */
public class _71CheckExists {

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
		
		Stat s = client.checkExists().forPath("/node1");
		
		System.out.println(s);
		// 不存在返回null
	}
	
	// console:
	// 3501,3530,1471869513734,1471870458821,2,2,0,0,3,2,3503

}
