package com.demo._1api;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * 获取节点数据(同步)
 *
 * @author jerome
 */
public class _41GetDataSync implements Watcher {

	private static ZooKeeper zooKeeper;
	private static Stat stat = new Stat();

	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		zooKeeper = new ZooKeeper("192.168.10.5:2181", 5000, new _41GetDataSync());
		Thread.sleep(Integer.MAX_VALUE);
	}

	private void doSomething(ZooKeeper zookeeper) {
		// zookeeper.addAuthInfo("digest", "jerome:123456".getBytes());
		try {
			// true false 要不要关注这个节点的子节点的变化
			System.out.println(new String(zooKeeper.getData("/node2", true, stat)));
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void process(WatchedEvent event) {
		if (event.getState() == KeeperState.SyncConnected) {
			if (event.getType() == EventType.None && null == event.getPath()) {
				doSomething(zooKeeper);
			} else {
				if (event.getType() == EventType.NodeDataChanged) {
					try {
						System.out.println(new String(zooKeeper.getData(event.getPath(), true, stat)));
						System.out.println("stat:" + stat);
					} catch (KeeperException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
