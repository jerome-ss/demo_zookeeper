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
 * 根据权限获取数据
 *
 * @author jerome
 */
public class _82GetDataSyncAuth implements Watcher {

	private static ZooKeeper zooKeeper;
	private static Stat stat = new Stat();

	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		zooKeeper = new ZooKeeper("192.168.10.5:2181", 5000, new _82GetDataSyncAuth());
		Thread.sleep(Integer.MAX_VALUE);
	}

	private void doSomething(ZooKeeper zookeeper) {
		zooKeeper.addAuthInfo("digest", "jerome:1234".getBytes());
		try {
			//zooKeeper.delete("/node1", -1);
			System.out.println(new String(zooKeeper.getData("/node1", true, stat)));
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
