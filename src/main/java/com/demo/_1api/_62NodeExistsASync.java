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
 * 判断节点是否存在(异步)
 *
 * @author jerome
 */
public class _62NodeExistsASync implements Watcher {

	private static ZooKeeper zooKeeper;

	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		zooKeeper = new ZooKeeper("192.168.10.5:2181", 5000, new _62NodeExistsASync());
		Thread.sleep(Integer.MAX_VALUE);
	}

	private void doSomething(ZooKeeper zookeeper) {
		zooKeeper.exists("/node2", true, new IStateCallback(), null);
	}

	@Override
	public void process(WatchedEvent event) {
		if (event.getState() == KeeperState.SyncConnected) {
			if (event.getType() == EventType.None && null == event.getPath()) {
				doSomething(zooKeeper);
			} else {
				try {
					if (event.getType() == EventType.NodeCreated) {
						System.out.println(event.getPath() + " created");
						zooKeeper.exists(event.getPath(), true, new IStateCallback(), null);
					} else if (event.getType() == EventType.NodeDataChanged) {
						System.out.println(event.getPath() + " updated");
						zooKeeper.exists(event.getPath(), true, new IStateCallback(), null);
					} else if (event.getType() == EventType.NodeDeleted) {
						System.out.println(event.getPath() + " deleted");
						zooKeeper.exists(event.getPath(), true, new IStateCallback(), null);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	static class IStateCallback implements AsyncCallback.StatCallback {

		@Override
		public void processResult(int rc, String path, Object ctx, Stat stat) {
			System.out.println("rc:" + rc);
		}
	}

	/*
	输出：
	rc:0
	*/
}
