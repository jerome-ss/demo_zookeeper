package com.demo._3curatorapi;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;

/**
 * 创建连接
 *
 * @author jerome
 */
public class _1CreateSession {
	
	public static void main(String[] args) throws InterruptedException {
		// retryPolicy 重试策略
		
		// 刚开始的重试事件是1000，后面一直增加，最多不超过三次
		// RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		
		// 最多重试5次，每次停顿1000ms
		// RetryPolicy retryPolicy = new RetryNTimes(5, 1000);
		
		// 重试过程不能超过5000ms，每次间隔1000s
		RetryPolicy retryPolicy = new RetryUntilElapsed(5000, 1000);
		
		// CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.1.105:2181",5000,5000, retryPolicy);

		// Fluent风格
		CuratorFramework client = CuratorFrameworkFactory
				.builder()
				.connectString("192.168.10.5:2181")
				.sessionTimeoutMs(5000)
				.connectionTimeoutMs(5000)
				.retryPolicy(retryPolicy)
				.build();

		client.start();
		
		System.out.println("conneted ok!");
		
		Thread.sleep(Integer.MAX_VALUE);
	}
	
	// console：
	// conneted ok!
}
