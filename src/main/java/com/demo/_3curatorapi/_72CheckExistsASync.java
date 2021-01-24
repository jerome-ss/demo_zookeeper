package com.demo._3curatorapi;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.data.Stat;

/**
 * 判断节点是否存在(异步)
 *
 * @author jerome
 */
public class _72CheckExistsASync {

	public static void main(String[] args) throws Exception {
		
		// 异步调用每次都是创建一个线程，如果系统执行的异步调用比较多，会创建比较多的线程，这里我们需要使用线程池
		ExecutorService es = Executors.newFixedThreadPool(5);

		RetryPolicy retryPolicy = new RetryUntilElapsed(5000, 1000);

		CuratorFramework client = CuratorFrameworkFactory
				.builder()
				.connectString("192.168.10.5:2181")
				.sessionTimeoutMs(5000)
				.connectionTimeoutMs(5000)
				.retryPolicy(retryPolicy)
				.build();

		client.start();

		client.checkExists().inBackground(new BackgroundCallback() {

			public void processResult(CuratorFramework arg0, CuratorEvent arg1) throws Exception {
				Stat stat = arg1.getStat();
				System.out.println(stat);
				System.out.println("ResultCode = " + arg1.getResultCode());
				System.out.println("Context = " + arg1.getContext());
			}
		}, "Context Value", es).forPath("/node1");

		Thread.sleep(Integer.MAX_VALUE);
	}
	
	// console:
	// 3501,3530,1471869513734,1471870458821,2,2,0,0,3,2,3503
	// 
	// ResultCode = 0
	// Context = Context Value

}
