package com.wsy.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author wangshuangyong 2021.3.26
 */

public class NettyRpcServer extends RpcServer {

    private static final Logger log = LoggerFactory.getLogger(NettyRpcServer.class);

    private Channel channel;

    private class ChannelRequestHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx){
            log.info("服务激活");
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            log.info("收到消息 : " + msg);
            ByteBuf messageBuf = (ByteBuf) msg;
            byte[] req = new byte[messageBuf.readableBytes()];
            messageBuf.readBytes(req);
            byte[] res = getRequestHandler().handleRequest(req);
            log.info("发送响应 : " + msg);
            ByteBuf resBuf = Unpooled.buffer(res.length);
            resBuf.writeBytes(res);
            ctx.write(resBuf);
        }
    }

    public NettyRpcServer() throws IOException, InterruptedException, KeeperException {
        this(10032, new RequestHandler());
    }

    public NettyRpcServer (int port, RequestHandler requestHandler) {
        this(port, requestHandler.getMessageProtocol().getProtocolName(), requestHandler);
    }

    public NettyRpcServer(int port, String protocol, RequestHandler requestHandler) {
        super(port, protocol, requestHandler);
    }

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new ChannelRequestHandler());
                    }
                });
        try {
            ChannelFuture channelFuture = serverBootstrap.bind(10032).sync();
            log.info("服务端端口绑定和启动完成!!!");
            channel = channelFuture.channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void stop() {
        channel.close();
    }
}
