package com.zeal.rpc.client;

import com.zeal.rpc.common.code.RpcDecoder;
import com.zeal.rpc.common.code.RpcEncoder;
import com.zeal.rpc.common.protocol.RpcRequest;
import com.zeal.rpc.common.protocol.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * WHAT THE ZZZZEAL
 * rpc客户端
 * @Author zeal
 * @Date 2022/10/5 10:01
 * @Version 1.0
 */
public class RpcClient extends SimpleChannelInboundHandler<RpcResponse> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

    private String host;
    
    private int port;
    
    private int timeout = 1000;
    
    private Object obj = new Object();
    
    private RpcResponse response;

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        this.response = rpcResponse;
        synchronized (obj) {
            obj.notifyAll();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("something wrong on client" , cause);
        ctx.close();
    }
    
    public RpcResponse send(RpcRequest request) throws Throwable {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(new RpcEncoder(RpcRequest.class)) // 将 RPC 请求进行编码（为了发送请求）
                                    .addLast(new RpcDecoder(RpcResponse.class)) // 将 RPC 响应进行解码（为了处理响应）
                                    .addLast(RpcClient.this); // 使用 RpcClient 发送 RPC 请求
                        }
                    })
                    .option(ChannelOption.SO_TIMEOUT, timeout)
                    .option(ChannelOption.SO_KEEPALIVE, true);
            // 同步调用
            ChannelFuture future = bootstrap.connect(host, port).sync();
            future.channel().writeAndFlush(request).sync();

            synchronized (obj) {
                obj.wait(); // 未收到响应，使线程等待
            }
            if (response != null) {
                future.channel().closeFuture().sync();
            }
            return response;
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
