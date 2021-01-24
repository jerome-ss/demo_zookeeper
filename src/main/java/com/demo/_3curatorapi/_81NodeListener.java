package com.demo._3curatorapi;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.RetryUntilElapsed;

/**
 * 节点监听
 *
 * @author jerome
 */
public class _81NodeListener {

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

		@SuppressWarnings("resource")
		final NodeCache cache = new NodeCache(client, "/node1");
		cache.start();
		cache.getListenable().addListener(new NodeCacheListener() {
			public void nodeChanged() throws Exception {
				byte[] ret = cache.getCurrentData().getData();
				System.out.println("new data:" + new String(ret));
			}
		});

		Thread.sleep(Integer.MAX_VALUE);
	}
	
	// console:
	// new data:111
	// ## 在控制台 修改节点数据: set /node1 222
	// console:
	// new data:222

}
