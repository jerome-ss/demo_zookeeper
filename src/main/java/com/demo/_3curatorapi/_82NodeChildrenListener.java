package com.demo._3curatorapi;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryUntilElapsed;

/**
 * 子节点监听
 *
 * @author jerome
 */
public class _82NodeChildrenListener {

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

		// true 表示是否同时获取变更的数据
		@SuppressWarnings("resource")
		final PathChildrenCache cache = new PathChildrenCache(client, "/node1", true);
		cache.start();
		cache.getListenable().addListener(new PathChildrenCacheListener() {

			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
				switch (event.getType()) {
				case CHILD_ADDED:
					System.out.println("CHILD_ADDED:" + event.getData());
					break;
				case CHILD_UPDATED:
					System.out.println("CHILD_UPDATED:" + event.getData());
					break;
				case CHILD_REMOVED:
					System.out.println("CHILD_REMOVED:" + event.getData());
					break;
				default:
					break;
				}
			}
		});

		Thread.sleep(Integer.MAX_VALUE);
	}
	
	// console:
	// CHILD_ADDED:ChildData{path='/node1/node11', stat=3502,3502,1471869518558,1471869518558,0,0,0,0,3,0,3502
	// , data=[49, 50, 51]}
	// CHILD_ADDED:ChildData{path='/node1/node12', stat=3503,3503,1471869522451,1471869522451,0,0,0,0,3,0,3503
	// , data=[49, 50, 51]}
	// ## 在控制台 新增节点数据: create /node1/node23 23
	// console:
	// CHILD_ADDED:ChildData{path='/node1/node23', stat=3539,3539,1471870863558,1471870863558,0,0,0,0,2,0,3539
	// , data=[50, 51]}


}
