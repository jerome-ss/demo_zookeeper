package com.demo._6balance.server;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;

/**
 * 默认的服务端启动时的注册过程
 * 
 * @author jerome
 */
public class DefaultRegistProvider implements RegistProvider {

	/**
	 * 注册到Zookeeper
	 */
	public void regist(Object context) throws Exception {

		ZooKeeperRegistContext registContext = (ZooKeeperRegistContext) context;
		String path = registContext.getPath();
		ZkClient zkClient = registContext.getZkClient();

		try {
			// 创建临时节点
			zkClient.createEphemeral(path, registContext.getData());
		} catch (ZkNoNodeException e) {
			String parentDir = path.substring(0, path.lastIndexOf('/'));
			zkClient.createPersistent(parentDir, true);
			regist(registContext);
		}
	}

	public void unRegist(Object context) throws Exception {
		return;
	}

}
