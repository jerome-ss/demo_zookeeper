package com.demo._5subscribe;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.BytesPushThroughSerializer;

/**
 * 数据的发布和订阅
 * 负责驱动WordServer和ManageServer
 * 主要应用 配置管理、服务发现
 * 
 * @author jerome
 */
public class SubscribeZkClient {

	/** 客户端数量 */
	private static final int CLIENT_QTY = 5;

	/** Zookeeper服务器地址 */
	private static final String ZOOKEEPER_SERVER = "192.168.10.5:2181";

	/** 配置configs节点 */
	private static final String CONFIG_PATH = "/configs";
	/** commands命名发送节点 */
	private static final String COMMAND_PATH = "/commands";
	/** servers服务器注册节点 */
	private static final String SERVERS_PATH = "/servers";

	public static void main(String[] args) throws Exception {

		List<ZkClient> clients = new ArrayList<ZkClient>();
		List<WorkServer> workServers = new ArrayList<WorkServer>();
		ManageServer manageServer = null;

		try {
			ServerConfig initConfig = new ServerConfig();
			initConfig.setDbPwd("123456");
			initConfig.setDbUrl("jdbc:mysql://localhost:3306/mydb");
			initConfig.setDbUser("root");

			// 启动Manage Server
			ZkClient clientManage = new ZkClient(ZOOKEEPER_SERVER, 5000, 5000, new BytesPushThroughSerializer());
			manageServer = new ManageServer(SERVERS_PATH, COMMAND_PATH, CONFIG_PATH, clientManage, initConfig);
			manageServer.startServer();

			// 启动Work Server
			for (int i = 0; i < CLIENT_QTY; ++i) {
				ZkClient client = new ZkClient(ZOOKEEPER_SERVER, 5000, 5000, new BytesPushThroughSerializer());
				clients.add(client);
				
				ServerData serverData = new ServerData();
				serverData.setId(i);
				serverData.setName("WorkServer#" + i);
				serverData.setAddress("192.168.1." + i);

				WorkServer workServer = new WorkServer(CONFIG_PATH, SERVERS_PATH, serverData, client, initConfig);
				workServers.add(workServer);
				
				workServer.startServer();

			}
			
			Thread.sleep(500);
			System.out.println("敲回车键退出！\n");
			new BufferedReader(new InputStreamReader(System.in)).readLine();

		} finally {
			System.out.println("Shutting down...");

			for (WorkServer workServer : workServers) {
				try {
					workServer.stopServer();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			for (ZkClient client : clients) {
				try {
					client.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}
	
	/*
	console:
	start manage server.
	start work server.
	work server regist to /server.
	manage server : work server list changed, new list is 
	[192.168.1.0]
	
	start work server.
	work server regist to /server.
	manage server : work server list changed, new list is 
	[192.168.1.1, 192.168.1.0]
	
	start work server.
	work server regist to /server.
	manage server : work server list changed, new list is 
	[192.168.1.1, 192.168.1.0, 192.168.1.2]
	
	start work server.
	work server regist to /server.
	manage server : work server list changed, new list is 
	[192.168.1.1, 192.168.1.0, 192.168.1.3, 192.168.1.2]
	
	start work server.
	work server regist to /server.
	manage server : work server list changed, new list is 
	[192.168.1.1, 192.168.1.0, 192.168.1.3, 192.168.1.2, 192.168.1.4]
	
	敲回车键退出！
	*/
}
