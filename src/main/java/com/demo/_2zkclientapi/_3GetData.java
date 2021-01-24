package com.demo._2zkclientapi;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.zookeeper.data.Stat;

import com.demo._2zkclientapi.model.User;

/**
 * 获取节点数据
 *
 * @author jerome
 */
public class _3GetData {

	public static void main(String[] args) {
		// 反序列化数据
		ZkClient zc = new ZkClient("192.168.10.5:2181", 10000, 10000, new SerializableSerializer());
		System.out.println("conneted ok!");

		Stat stat = new Stat();
		User u = zc.readData("/node1", stat);
		System.out.println(u.toString());
		System.out.println(stat);

	}
	
	/*
	conneted ok!
	User [id=2, name=test2]
	3428,3456,1471866939700,1471867629717,1,3,0,0,189,1,3450
	*/
}
