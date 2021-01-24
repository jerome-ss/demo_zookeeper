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
 * 修改节点数据(同步)
 *
 * @author jerome
 */
public class _71UpdateNodeSync implements Watcher {

	private static ZooKeeper zooKeeper;

	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		zooKeeper = new ZooKeeper("192.168.10.5:2181", 5000, new _71UpdateNodeSync());
		Thread.sleep(Integer.MAX_VALUE);
	}

	private void doSomething(ZooKeeper zooKeeper) {
		try {
			Stat stat = zooKeeper.setData("/node2", "123".getBytes(), -1);
			System.out.println("stat:" + stat);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (KeeperException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void process(WatchedEvent event) {
		if (event.getState() == KeeperState.SyncConnected) {
			if (event.getType() == EventType.None && null == event.getPath()) {
				doSomething(zooKeeper);
			}
		}
	}
	
	/*
	输出：
	stat:3126,3184,1471767602457,1471776116006,5,9,0,0,3,3,3172
	*/

}