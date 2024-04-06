package com.zeal.rpc.common.code;

import com.zeal.rpc.common.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * WHAT THE ZZZZEAL
 * 解码器
 * @Author zeal
 * @Date 2022/10/5 10:55
 * @Version 1.0
 */
public class RpcDecoder extends ByteToMessageDecoder {

    /**
     * 解码后的类型
     */
    private Class<?> genericClass;

    public RpcDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        // 若缓冲区没有int长度的字符，返回
        if (byteBuf.readableBytes() < 4) {
            return;
        }
        byteBuf.markReaderIndex();
        int length = byteBuf.readInt();
        if (length < 0) {
            channelHandlerContext.close();
        }
        if (byteBuf.readableBytes() < length) {
            byteBuf.resetReaderIndex();
        }
        // 写入数据
        byte[] data = new byte[length];
        byteBuf.readBytes(data);
        Object obj = SerializationUtil.deserialize(data, genericClass);
        list.add(obj);
    }
    
}
