
package com.demo._6balance.server;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 处理与客户端之间的连接
 * 客户端连接断开等触发此类
 * 
 * @author jerome
 */
public class ServerHandler extends ChannelHandlerAdapter {

	private final BalanceUpdateProvider balanceUpdater;
	/** 负载均衡累加数值 */
	private static final Integer BALANCE_STEP = 1;

	public ServerHandler(BalanceUpdateProvider balanceUpdater) {
		this.balanceUpdater = balanceUpdater;
	}

	public BalanceUpdateProvider getBalanceUpdater() {
		return balanceUpdater;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("one client connect...");
		balanceUpdater.addBalance(BALANCE_STEP);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("one client disconnect...");
		balanceUpdater.reduceBalance(BALANCE_STEP);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

}
