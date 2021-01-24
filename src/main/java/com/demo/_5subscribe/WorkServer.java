package com.demo._5subscribe;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;

import com.alibaba.fastjson.JSON;

public class WorkServer {

	private ZkClient zkClient;
	private String configPath;
	private String serversPath;
	private ServerData serverData;
	private ServerConfig serverConfig;
	private IZkDataListener dataListener;

	public WorkServer(String configPath, String serversPath, ServerData serverData, ZkClient zkClient,ServerConfig initConfig) {
		
		this.zkClient = zkClient;
		this.serversPath = serversPath;
		this.configPath = configPath;
		this.serverConfig = initConfig;
		this.serverData = serverData;

		this.dataListener = new IZkDataListener() {

			public void handleDataDeleted(String dataPath) throws Exception {
			}
			
			/**
			 * 数据有变化(配置管理,订阅)
			 */
			public void handleDataChange(String dataPath, Object data) throws Exception {
				String retJson = new String((byte[]) data);
				ServerConfig serverConfigLocal = (ServerConfig) JSON.parseObject(retJson, ServerConfig.class);
				updateConfig(serverConfigLocal);
				System.out.println("Work server : new Work server config is = " + serverConfig.toString());
			}
		};

	}

	public void startServer() {
		System.out.println("start work server.");
		registMe();
		zkClient.subscribeDataChanges(configPath, dataListener);
	}

	public void stopServer() {
		System.out.println("stop work server.");
		zkClient.unsubscribeDataChanges(configPath, dataListener);
	}

	/**
	 * 注册自己的信息到Server节点(服务发现,发布)
	 * 
	 * @author jerome
	 */
	private void registMe() {
		System.out.println("work server regist to /server.");
		String mePath = serversPath.concat("/").concat(serverData.getAddress());
		try {
			// 创建临时节点
			zkClient.createEphemeral(mePath, JSON.toJSONString(serverData).getBytes());
		} catch (ZkNoNodeException e) {
			// 没有父节点 创建永久父节点
			zkClient.createPersistent(serversPath, true);
			registMe();
		}
	}

	private void updateConfig(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}

}
