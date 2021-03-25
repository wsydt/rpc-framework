package com.wsy.client.net;

import com.wsy.discovery.ServiceInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * @author wangshuangyong 2021.3.22
 */

public class NettyNetClient implements NetClient {

    private Logger logger = LoggerFactory.getLogger(NettyNetClient.class);

    private class SendHandler extends ChannelInboundHandlerAdapter {

        private byte[] data;

        private CountDownLatch cdl;

        private Object result;

        public SendHandler(byte[] data) {
            this.data = data;
            this.cdl = new CountDownLatch(1);
        }

        public Object getResult() throws InterruptedException {
            cdl.await();
            return result;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            logger.info("连接服务器成功 : " + ctx);
            ByteBuf reqBuf = Unpooled.buffer(data.length);
            reqBuf.writeBytes(data);
            logger.info("客户端发送消息 : " + reqBuf);
            ctx.writeAndFlush(reqBuf);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            logger.info("client read message : " + msg);
            ByteBuf message = (ByteBuf) msg;
            byte[] response = new byte[message.readableBytes()];
            message.readBytes(response);
            result = response;
            cdl.countDown();
        }
    }

    @Override
    public byte[] sendRequest(byte[] data, ServiceInfo service) throws Throwable {
        String[] address = service.getAddress().split(":");
        SendHandler sendHandler = new SendHandler(data);
        byte[] responseData = null;
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(sendHandler);
            bootstrap.connect(address[0], Integer.parseInt(address[1])).sync();
            responseData = sendHandler.data;
        } finally {
            group.shutdownGracefully();
        }
        return responseData;
    }
}
