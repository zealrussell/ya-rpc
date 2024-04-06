package com.zeal.rpc.common.code;

import com.zeal.rpc.common.util.SerializationUtil;
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

    /** 
     * 编码前的类型
     */
    private Class<?> genericClass;

    public RpcEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    public void encode(ChannelHandlerContext cx, Object in, ByteBuf bf) throws Exception {
        // 将对象序列化后输入缓冲区
        if (genericClass.isInstance(in)) {
            byte[] data = SerializationUtil.serialize(in);
            bf.writeInt(data.length);
            bf.writeBytes(data);
        }
    }

}
