package com.hbu;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author chenwei
 *
 * @date 2018/6/19
 */
public class WebsocketChatServer {
	private int port;

	public WebsocketChatServer(int port) {
		this.port = port;
	}

	public void run() throws Exception {
		//NioEventLoopGroup 是用来处理I/O操作的多线程事件循环器
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			//ServerBootstrap 是一个启动 NIO 服务的辅助启动类。
			//你可以在这个服务中直接使用 Channel，但是这会是一个复杂的处理过程，在很多情况下你并不需要这样做。
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					//这里我们指定使用 NioServerSocketChannel 类来举例说明一个新的 Channel 如何接收进来的连接。
					.channel(NioServerSocketChannel.class)
					//这里的事件处理类经常会被用来处理一个最近的已经接收的 Channel。SimpleChatServerInitializer
					// 继承自ChannelInitializer 是一个特殊的处理类，他的目的是帮助使用者配置一个新的 Channel。
					// 也许你想通过增加一些处理类比如 SimpleChatServerHandler 来配置一个新的 Channel 或者其对应的ChannelPipeline 来实现你的网络程序。
					// 当你的程序变的复杂时，可能你会增加更多的处理类到 pipline 上，然后提取这些匿名类到最顶层的类上。
					.childHandler(new WebsocketChatServerInitializer())
					//Channel 实现的配置参数。我们正在写一个TCP/IP 的服务端，因此我们被允许设置 socket 的参数选项比如tcpNoDelay 和 keepAlive
					.option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true);

			System.out.println("WebsocketChatServer 启动了");

			// 绑定端口，开始接收进来的连接
			ChannelFuture f = b.bind(port).sync();

			// 等待服务器  socket 关闭 。
			// 在这个例子中，这不会发生，但你可以优雅地关闭你的服务器。
			f.channel().closeFuture().sync();

		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();

			System.out.println("WebsocketChatServer 关闭了");
		}
	}

	public static void main(String[] args) throws Exception {
		int port;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		} else {
			port = 8080;
		}
		new WebsocketChatServer(port).run();

	}
}
