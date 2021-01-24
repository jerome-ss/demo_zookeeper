package com.demo._1api;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * 获取节点(异步)
 *
 * @author jerome
 */
public class _32GetChildrenASync implements Watcher {

	private static ZooKeeper zooKeeper;

	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		zooKeeper = new ZooKeeper("192.168.10.5:2181", 5000, new _32GetChildrenASync());
		Thread.sleep(Integer.MAX_VALUE);
	}

	@Override
	public void process(WatchedEvent event) {
		if (event.getState() == KeeperState.SyncConnected) {
			if (event.getType() == EventType.None && null == event.getPath()) {
				doSomething(zooKeeper);
			} else {
				if (event.getType() == EventType.NodeChildrenChanged) {
					zooKeeper.getChildren(event.getPath(), true, new IChildren2Callback(), null);
				}
			}
		}
	}

	private void doSomething(ZooKeeper zookeeper) {
		try {
			zooKeeper.getChildren("/node2", true, new IChildren2Callback(), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static class IChildren2Callback implements AsyncCallback.Children2Callback {

		@Override
		public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
			StringBuilder sb = new StringBuilder();
			sb.append("rc=" + rc).append("\n");
			sb.append("path=" + path).append("\n");
			sb.append("ctx=" + ctx).append("\n");
			sb.append("children=" + children).append("\n");
			sb.append("stat=" + stat).append("\n");
			System.out.println(sb.toString());
		}
	}

}
