package com.demo._2zkclientapi;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

/**
 * 检测节点是否存在
 *
 * @author jerome
 */
public class _5NodeExists {

	public static void main(String[] args) {
		ZkClient zc = new ZkClient("192.168.10.5:2181", 10000, 10000, new SerializableSerializer());
		System.out.println("conneted ok!");
		boolean e = zc.exists("/node1");
		System.out.println(e);
	}
	
	/*
	console:
	conneted ok!
	true    
	*/
}
