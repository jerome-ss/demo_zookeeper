package com.demo._1api;

import java.io.IOException;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/**
 * 创建节点(异步)
 *
 * @author jerome
 */
public class _22CreateNodeASync implements Watcher {

	private static ZooKeeper zookeeper;

	public static void main(String[] args) throws IOException, InterruptedException {
		zookeeper = new ZooKeeper("192.168.10.5:2181", 5000, new _22CreateNodeASync());
		System.out.println(zookeeper.getState());
		Thread.sleep(Integer.MAX_VALUE);
	}

	@Override
	public void process(WatchedEvent event) {
		System.out.println("收到事件：" + event);
		if (event.getState() == KeeperState.SyncConnected) {
			doSomething();
		}
	}

	private void doSomething() {
		zookeeper.create("/node3", "123".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, new IStringCallback(), "创建");
	}

	static class IStringCallback implements AsyncCallback.StringCallback {

		/**
		 * @param rc
		 *            返回码0表示成功
		 * @param path
		 *            我们需要创建的节点的完整路径
		 * @param ctx
		 *            上面传入的值("创建")
		 * @param name
		 *            服务器返回给我们已经创建的节点的真实路径,如果是顺序节点path和name是不一样的
		 */
		@Override
		public void processResult(int rc, String path, Object ctx, String name) {
			StringBuilder sb = new StringBuilder();
			sb.append("rc=" + rc).append("\n");
			sb.append("path=" + path).append("\n");
			sb.append("ctx=" + ctx).append("\n");
			sb.append("name=" + name);
			System.out.println(sb.toString());
		}
	}
	
	/*
	输出：
	CONNECTING
	收到事件：WatchedEvent state:SyncConnected type:None path:null
	rc=0
	path=/node3
	ctx=创建
	name=/node3
	*/

}
