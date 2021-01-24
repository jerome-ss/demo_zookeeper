package com.demo._1api;

import java.io.IOException;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * 修改节点数据(异步)
 *
 * @author jerome
 */
public class _72UpdateNodeASync implements Watcher {

	private static ZooKeeper zooKeeper;

	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		zooKeeper = new ZooKeeper("192.168.10.5:2181", 5000, new _72UpdateNodeASync());
		Thread.sleep(Integer.MAX_VALUE);
	}

	private void doSomething(WatchedEvent event) {
		zooKeeper.setData("/node2", "234".getBytes(), -1, new IStatCallback(), null);
	}

	@Override
	public void process(WatchedEvent event) {
		if (event.getState() == KeeperState.SyncConnected) {
			if (event.getType() == EventType.None && null == event.getPath()) {
				doSomething(event);
			}
		}
	}

	static class IStatCallback implements AsyncCallback.StatCallback {

		@Override
		public void processResult(int rc, String path, Object ctx, Stat stat) {
			StringBuilder sb = new StringBuilder();
			sb.append("rc=" + rc).append("\n");
			sb.append("path" + path).append("\n");
			sb.append("ctx=" + ctx).append("\n");
			sb.append("Stat=" + stat).append("\n");
			System.out.println(sb.toString());
		}
	}

}