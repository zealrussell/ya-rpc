package com.zeal.common.code;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.Serializable;

/**
 * WHAT THE ZZZZEAL
 * 编码
 * @Author zeal
 * @Date 2022/10/5 10:54
 * @Version 1.0
 */
public class RpcEncoder extends MessageToByteEncoder<Object> {

    private Class<?> genericClass;

    public RpcEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    public void encode(ChannelHandlerContext cx, Object in, ByteBuf bf) throws Exception {
        // 若是输入
        if (genericClass.isInstance(in)) {
            byte[] data = Serializable
        }
    }

}
