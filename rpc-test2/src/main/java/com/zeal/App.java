package com.zeal;

import com.zeal.rpc.client.RpcProxy;
import com.zeal.rpc.server.service.CharacterService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring-client.xml");
        RpcProxy rpcProxy = ctx.getBean(RpcProxy.class);
        // 服务二
        CharacterService characterService  = rpcProxy.createService(CharacterService.class);
        String result2 = characterService.uppercase("aaabbbcc");
        System.out.println("客户端2--大写操作的结果为：" + result2);
    }
}
