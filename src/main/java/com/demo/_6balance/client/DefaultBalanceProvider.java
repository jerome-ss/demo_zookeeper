package com.demo._6balance.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

import com.demo._6balance.server.ServerData;

/**
 * 默认负载均衡算法实现
 * 
 * @author jerome
 */
public class DefaultBalanceProvider extends AbstractBalanceProvider<ServerData> {

	private final String zkServer;
	private final String serversPath;
	private final ZkClient zkClient;

	private static final Integer SESSION_TIME_OUT = 10000;
	private static final Integer CONNECT_TIME_OUT = 10000;

	public DefaultBalanceProvider(String zkServer, String serversPath) {
		this.serversPath = serversPath;
		this.zkServer = zkServer;
		this.zkClient = new ZkClient(this.zkServer, SESSION_TIME_OUT, CONNECT_TIME_OUT, new SerializableSerializer());
	}

	@Override
	protected ServerData balanceAlgorithm(List<ServerData> items) {
		if (items.size() > 0) {
			Collections.sort(items);
			return items.get(0);
		} else {
			return null;
		}
	}

	@Override
	protected List<ServerData> getBalanceItems() {
		List<ServerData> sdList = new ArrayList<ServerData>();
		List<String> children = zkClient.getChildren(this.serversPath);
		for (int i = 0; i < children.size(); i++) {
			ServerData serverData = zkClient.readData(serversPath + "/" + children.get(i));
			sdList.add(serverData);
		}
		return sdList;
	}

}
