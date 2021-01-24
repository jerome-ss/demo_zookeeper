package com.demo._5subscribe;

import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import com.alibaba.fastjson.JSON;

public class ManageServer {

	private String serversPath;
	private String commandPath;
	private String configPath;
	private ZkClient zkClient;
	private ServerConfig config;
	private IZkChildListener childListener;
	private IZkDataListener dataListener;
	private List<String> workServerList;

	public ManageServer(String serversPath, String commandPath, String configPath, ZkClient zkClient, ServerConfig config) {
		
		this.serversPath = serversPath;
		this.commandPath = commandPath;
		this.zkClient = zkClient;
		this.config = config;
		this.configPath = configPath;
		
		/**
		 * 监听server子节点列表的变化(服务发现,订阅)
		 */
		this.childListener = new IZkChildListener() {
			public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
				workServerList = currentChilds;
				System.out.println("manage server : work server list changed, new list is ");
				execList();
			}
		};
		
		/**
		 * 监听config节点数据的变化(配置管理,发布)
		 */
		this.dataListener = new IZkDataListener() {
			
			public void handleDataDeleted(String dataPath) throws Exception {
			}
			
			/**
			 * 数据改变触发(配置管理,发布)
			 */
			public void handleDataChange(String dataPath, Object data) throws Exception {
				String cmd = new String((byte[]) data);
				System.out.println("manage server : cmd = " + cmd);
				exeCmd(cmd);
			}
		};

	}

	/*
	 * 执行命令
	 * 1: list 2: create 3: modify
	 */
	private void exeCmd(String cmdType) {
		if ("list".equals(cmdType)) {
			execList();
		} else if ("create".equals(cmdType)) {
			execCreate();
		} else if ("modify".equals(cmdType)) {
			execModify();
		} else {
			System.out.println("mange server ：error command! cmdType = " + cmdType);
		}
	}

	private void execList() {
		System.out.println(workServerList.toString() + "\n");
	}

	private void execCreate() {
		if (!zkClient.exists(configPath)) {
			try {
				zkClient.createPersistent(configPath, JSON.toJSONString(config).getBytes());
			} catch (ZkNodeExistsException e) {
				zkClient.writeData(configPath, JSON.toJSONString(config).getBytes());
			} catch (ZkNoNodeException e) {
				String parentDir = configPath.substring(0, configPath.lastIndexOf('/'));
				zkClient.createPersistent(parentDir, true);
				execCreate();
			}
		}else{
			System.out.println("manage server : " + configPath + " is exist.");
		}
	}

	private void execModify() {
		config.setDbUser(config.getDbUser() + "_modify");

		try {
			zkClient.writeData(configPath, JSON.toJSONString(config).getBytes());
		} catch (ZkNoNodeException e) {
			execCreate();
		}
	}

	public void startServer() {
		System.out.println("start manage server.");
		zkClient.subscribeChildChanges(serversPath, childListener);
		zkClient.subscribeDataChanges(commandPath, dataListener);
	}

	public void stopServer() {
		System.out.println("stop manage server.");
		zkClient.unsubscribeChildChanges(serversPath, childListener);
		zkClient.unsubscribeDataChanges(commandPath, dataListener);
	}
}
