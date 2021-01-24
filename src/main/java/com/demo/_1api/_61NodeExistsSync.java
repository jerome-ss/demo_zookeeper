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
 * 判断节点是否存在(同步)
 *
 * @author jerome
 */
public class _61NodeExistsSync implements Watcher {

	private static ZooKeeper zooKeeper;

	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		zooKeeper = new ZooKeeper("192.168.10.5:2181", 5000, new _61NodeExistsSync());
		Thread.sleep(Integer.MAX_VALUE);
	}

	private void doSomething(ZooKeeper zooKeeper) {
		try {
			// true 注册事件监听器
			Stat stat = zooKeeper.exists("/node2", true);
			System.out.println(stat);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
						System.out.println(zooKeeper.exists(event.getPath(), true));
					} else if (event.getType() == EventType.NodeDataChanged) {
						System.out.println(event.getPath() + " updated");
						System.out.println(zooKeeper.exists(event.getPath(), true));
					} else if (event.getType() == EventType.NodeDeleted) {
						System.out.println(event.getPath() + " deleted");
						System.out.println(zooKeeper.exists(event.getPath(), true));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/*
	输出：
	3126,3150,1471767602457,1471771708937,1,9,0,0,3,3,3172
	修改节点信息：
	[zk: 27.154.242.214:5091(CONNECTED) 16] set /node2 2           
	cZxid = 0xc36
	ctime = Sun Aug 21 16:20:02 CST 2016
	mZxid = 0xc67
	mtime = Sun Aug 21 18:11:04 CST 2016
	pZxid = 0xc64
	cversion = 9
	dataVersion = 2
	aclVersion = 0
	ephemeralOwner = 0x0
	dataLength = 1
	numChildren = 3
	输出：
	/node2 updated
	3126,3175,1471767602457,1471774264662,2,9,0,0,1,3,3172
	 */
}
