package com.demo._3curatorapi;

import java.util.ArrayList;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Perms;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

/**
 * 权限 - 创建节点
 *
 * @author jerome
 */
public class _91CreateNodeAuth {

	public static void main(String[] args) throws Exception {

		RetryPolicy retryPolicy = new RetryUntilElapsed(5000, 1000);

		CuratorFramework client = CuratorFrameworkFactory
				.builder()
				.connectString("192.168.10.5:2181")
				.sessionTimeoutMs(5000)
				.connectionTimeoutMs(5000)
				.retryPolicy(retryPolicy)
				.build();

		client.start();

		ACL aclIp = new ACL(Perms.READ, new Id("ip", "27.154.242.214"));
		ACL aclDigest = new ACL(Perms.READ | Perms.WRITE,
				new Id("digest", DigestAuthenticationProvider.generateDigest("jerome:123456")));
		
		ArrayList<ACL> acls = new ArrayList<ACL>();
		acls.add(aclDigest);
		acls.add(aclIp);

		String path = client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).withACL(acls)
				.forPath("/node3", "123".getBytes());

		System.out.println(path);

		Thread.sleep(Integer.MAX_VALUE);
	}
	
	// console:
	// /node3

}
