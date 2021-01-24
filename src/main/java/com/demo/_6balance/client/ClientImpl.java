package com.demo._6balance.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.demo._6balance.server.ServerData;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 客户端实现类
 * 
 * @author jerome
 */
public class ClientImpl implements Client {

	private final BalanceProvider<ServerData> provider;
	private EventLoopGroup group = null;
	private Channel channel = null;

	private final Logger log = LoggerFactory.getLogger(getClass());

	public ClientImpl(BalanceProvider<ServerData> provider) {
		this.provider = provider;
	}

	public BalanceProvider<ServerData> getProvider() {
		return provider;
	}

	@Override
	public void connect() {

		try {
			ServerData serverData = provider.getBalanceItem();

			System.out.println("client : connecting to " + serverData.getHost() + ":" + serverData.getPort()
					+ ", the balance is " + serverData.getBalance());

			group = new NioEventLoopGroup();
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline p = ch.pipeline();
					p.addLast(new ClientHandler());
				}
			});
			ChannelFuture channelFuture = b.connect(serverData.getHost(), serverData.getPort()).syncUninterruptibly();
			channel = channelFuture.channel();

			System.out.println("client : started success!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void disConnect() {

		try {
			if (channel != null) {
				channel.close().syncUninterruptibly();
			}

			group.shutdownGracefully();
			group = null;

			System.out.println("client : disconnected!");
			// log.debug("disconnected!");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}

}
