package com.demo._6balance.server;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkBadVersionException;
import org.apache.zookeeper.data.Stat;

/**
 * 默认负载均衡更新器
 * 
 * @author jerome
 */
public class DefaultBalanceUpdateProvider implements BalanceUpdateProvider {

	private String serverPath;
	private ZkClient zkClient;

	public DefaultBalanceUpdateProvider(String serverPath, ZkClient zkClient) {
		this.serverPath = serverPath;
		this.zkClient = zkClient;
	}

	/**
	 * 有客户端连接
	 */
	public boolean addBalance(Integer step) {
		Stat stat = new Stat();
		ServerData serverData;

		while (true) {
			try {
				serverData = zkClient.readData(this.serverPath, stat);
				serverData.setBalance(serverData.getBalance() + step);
				zkClient.writeData(this.serverPath, serverData, stat.getVersion());
				return true;
			} catch (ZkBadVersionException e) {
				e.printStackTrace();
			} catch (Exception e) {
				return false;
			}
		}

	}

	/**
	 * 客户端断开连接
	 */
	public boolean reduceBalance(Integer step) {
		Stat stat = new Stat();
		ServerData serverData;

		while (true) {
			try {
				serverData = zkClient.readData(this.serverPath, stat);
				final Integer currBalance = serverData.getBalance();
				serverData.setBalance(currBalance > step ? currBalance - step : 0);
				zkClient.writeData(this.serverPath, serverData, stat.getVersion());
				return true;
			} catch (ZkBadVersionException e) {
				e.printStackTrace();
			} catch (Exception e) {
				return false;
			}
		}
	}

}
