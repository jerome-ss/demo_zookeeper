package com.demo._4master;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkException;
import org.I0Itec.zkclient.exception.ZkInterruptedException;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.zookeeper.CreateMode;

/**
 * master选举 主工作类
 *
 * @author jerome
 */
public class WorkServer {

	/** 服务器是否在运行 */
	private volatile boolean running = false;

	private ZkClient zkClient;

	/** 主节点路径 */
	private static final String MASTER_PATH = "/master";

	/** 订阅节点的子节点内容的变化 */
	private IZkDataListener dataListener;

	/** 从节点 */
	private RunningData serverData;

	/** 主节点 */
	private RunningData masterData;

	/** 延迟执行 */
	private ScheduledExecutorService delayExector = Executors.newScheduledThreadPool(1);
	// private int delayTime = 5;

	/**
	 * 构造函数
	 * 
	 * @param runningData
	 */
	public WorkServer(RunningData runningData) {

		this.serverData = runningData;
		this.dataListener = new IZkDataListener() {

			/**
			 * 节点删除触发
			 */
			public void handleDataDeleted(String dataPath) throws Exception {
				takeMaster();
				
				// 对应网络抖动的方法
				// 由于网络抖动，可能误删了master节点导致重新选举，如果master还未宕机，而被其他节点抢到了，
				// 会造成可能有写数据重新生成等资源的浪费。我们这里，增加一个判断，如果上次自己不是master就等待5s在开始争抢master，
				// 这样就能保障没有宕机的master能再次选中为master。
				/*if (masterData != null && masterData.getName().equals(serverData.getName())) {
					takeMaster();
				} else {
					// 延迟5s再争抢
					delayExector.schedule(new Runnable() {
						public void run() {
							takeMaster();
						}
					}, delayTime, TimeUnit.SECONDS);
				}*/

			}

			/**
			 * 节点数据修改触发
			 */
			public void handleDataChange(String dataPath, Object data) throws Exception {

			}
		};
	}

	public void startServer() throws Exception {
		System.out.println(this.serverData.getName() + "is start!");
		
		if (running) {
			throw new Exception("server has startup...");
		}
		
		running = true;
		
		// 订阅删除事件
		zkClient.subscribeDataChanges(MASTER_PATH, dataListener);
		
		takeMaster();

	}

	/**
	 * 关闭服务器
	 * 
	 * @author jerome
	 * @throws Exception
	 */
	public void stop() throws Exception {
		if (!running) {
			throw new Exception("server has stoped");
		}
		running = false;

		delayExector.shutdown();

		// 取消订阅删除事件
		zkClient.unsubscribeDataChanges(MASTER_PATH, dataListener);

		releaseMaster();

	}

	/**
	 * 争抢master节点
	 * 
	 * @author jerome
	 */
	private void takeMaster() {
		
		if (!running) {
			return;
		}

		try {
			// 创建临时节点
			zkClient.create(MASTER_PATH, serverData, CreateMode.EPHEMERAL);
			masterData = serverData;
			System.out.println(serverData.getName() + " is master");
			
			// 测试: 5s后判断是否是master节点,是的话 释放master节点
			// 释放后,其他节点都是有监听删除事件的,会争抢master
			delayExector.schedule(new Runnable() {
				public void run() {
					if (checkIsMaster()) {
						releaseMaster();
					}
				}
			}, 5, TimeUnit.SECONDS);

		} catch (ZkNodeExistsException e) {
			RunningData runningData = zkClient.readData(MASTER_PATH, true);
			if (runningData == null) {
				takeMaster();
			} else {
				masterData = runningData;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 释放master
	 * 
	 * @author jerome
	 */
	private void releaseMaster() {
		if (checkIsMaster()) {
			zkClient.delete(MASTER_PATH);
		}
	}

	/**
	 * 检查是否是master
	 * 
	 * @author jerome
	 * @return
	 */
	private boolean checkIsMaster() {
		try {
			RunningData eventData = zkClient.readData(MASTER_PATH);
			masterData = eventData;
			if (masterData.getName().equals(serverData.getName())) {
				return true;
			}
			return false;
		} catch (ZkNoNodeException e) {
			return false;
		} catch (ZkInterruptedException e) {
			return checkIsMaster();
		} catch (ZkException e) {
			return false;
		}
	}
	
	public ZkClient getZkClient() {
		return zkClient;
	}

	public void setZkClient(ZkClient zkClient) {
		this.zkClient = zkClient;
	}

}
