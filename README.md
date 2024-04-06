# 一、简介

本程序使用了zookeeper、netty开发了一个rpc框架

# 二、使用

## 1. 开启zookeeper

程序使用了zookeeper作为注册中心，将服务器地址保存在了 `/registry/data`节点上

## 2. 运行server

运行rpc-server下的ServerApplication启动类。若服务器连接zookeeper失败，请手动创建节点`create /registry`

## 3. 调用服务

导入RpcClient包后，通过如何代码即可调用。

```java
ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring-client.xml");
RpcProxy rpcProxy = ctx.getBean(RpcProxy.class);
CalculatorService calculatorService = rpcProxy.createService(CalculatorService.class);
float result = calculatorService.sum(1.3F, 2.0F);
```

# 三、准备知识

## 1.Zookeeper注册中心

## 2.Netty



# 三、设计介绍

代码分为 rpc-common、rpc-server、rpc-client、rpc-test四个模块。

```
├─rpc-client
│  ├─src
│  │  ├─main
│  │  │  ├─java
│  │  │  │  └─com
│  │  │  │      └─zeal
│  │  │  │          └─rpc
│  │  │  │              └─client
│  │  │  └─resources
|
|
├─rpc-common
│  ├─src
│  │  ├─main
│  │  │  └─java
│  │  │      └─com
│  │  │          └─zeal
│  │  │              └─rpc
│  │  │                  └─common
│  │  │                      ├─code
│  │  │                      ├─protocol
│  │  │                      └─util
|
|
├─rpc-server
│  ├─src
│  │  ├─main
│  │  │  ├─java
│  │  │  │  └─com
│  │  │  │      └─zeal
│  │  │  │          └─rpc
│  │  │  │              └─server
│  │  │  │                  ├─core
│  │  │  │                  └─service
│  │  │  │                      └─impl
│  │  │  └─resources
|
|
└─rpc-test
    ├─src
    │  ├─main
    │  │  ├─java
    │  │  │  └─com
    │  │  │      └─zeal
    │  │  │          └─rpc
    │  │  └─resources
```

## 1.rpc-common

common模块包括了 rpc传输协议、rpc编码译码器、序列化工具类。

`protocol`包定义RPC传输协议请求、应答消息的格式。请求消息包括 请求ID、类名、方法名、参数类型、参数值；应答消息包括 应答ID、错误、 结果。

`code`包定义了编码译码器，通过序列化工具，进行序列化、反序列化。



## 2.rpc-server

server模块是功能实现的地方。

`core`包中包括：

​	`Registry`类：用于将服务注册到 zookeeper

​	`RpcHandler`类：用于基于反射执行目标方法

​	`RpcService`注解：标识Rpc服务类，并注册到 spring bean 中

​	`RpcServer`类：获取所有的Rpc服务，将 netty 绑定到服务器地址，收发数据

`service`包：

​	定义了具体需要实现的服务接口和实现类，可自行拓展。实现类必须加上 RpcService 注解。

## 3.rpc-client

client模块为客户端，有三个类。

`Discovery`类：用于从 zookeeper 注册中心中发现rpc服务器地址

`RpcClient`类：用于通过 netty 发送请求，并将读取到的 rpcresponse返回。

```java
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
```

`RpcProxy`类：使用代理模式，通过createService方法，构造 rpc请求，并通过RpcClient向已知的RpcServer发送请求，返回结果。

## 4.rpc-test

test为具体的调用模块，用户可通过如下代码调用。

```java
public static void main( String[] args )
{
    ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring-client.xml");
    RpcProxy rpcProxy = ctx.getBean(RpcProxy.class);
    
    // 服务一
    CalculatorService calculatorService = rpcProxy.createService(CalculatorService.class);
    float result1 = calculatorService.sum(1.3F, 2.0F);
    System.out.println("*********************" + result1);
    // 服务二
    CharacterService characterService  = rpcProxy.createService(CharacterService.class);
    String result2 = characterService.uppercase("aaabbbcc");
    System.out.println("*********************" + result2);
}
```

# 四、运行逻辑

### 1.

启动 rpc-server 模块，此时，服务端已被注册至 zookeeper。

### 2.

在 test 模块通过`ClassPathXmlApplicationContext`类中 xml 文件构建bean， 并通过 getBean 方法得到一个 RpcProxy 实例。

```java
ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring-client.xml");
RpcProxy rpcProxy = ctx.getBean(RpcProxy.class);
```

spring-client.xml中配置了两个 bean ，ZooKeeper 服务器的地址在 client-config.properties 文件中配置

```xml
<!--client的配置类-->
<context:property-placeholder location="classpath:client-config.properties"/>

<!-- 配置 discovery 组件 -->
<bean id="discovery" class="com.zeal.rpc.client.Discovery">
    <constructor-arg name="registryAddress" value="${registry.address}"/>
</bean>

<!-- 配置 RPC 代理 -->
<bean id="rpcProxy" class="com.zeal.rpc.client.RpcProxy">
    <constructor-arg name="discovery" ref="discovery"/>
</bean>
```

### 2.

通过 rpcProxy.createService(**Service.class) 创建一个服务实例，之后即可使用该服务提供的方法。

```java
CalculatorService calculatorService = rpcProxy.createService(CalculatorService.class);
float result1 = calculatorService.sum(1.3F, 2.0F);
```

### 3.

createService 方法使用了 spring 提供的动态代理类，通过反射在运行时创建一个实现某些给定接口的新类。

在处理类中，首先构建一个 rpc 请求，依次填入 请求ID、类名、 方法名、 参数类型、 参数值，接着调用 discover 方法，发现服务器，最后通过 send 方法向服务器发送该请求，并监听，返回监听到的 RpcResponse 结果。

```java
public <T> T createService(Class<?> serviceClass) {
    return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class<?>[]{serviceClass},
            (proxy, method, args) -> {
                // 构造rpc请求
                RpcRequest request = new RpcRequest();
                request.setRequestId(UUID.randomUUID().toString());
                request.setClassName(method.getDeclaringClass().getName());
                request.setMethodName(method.getName());
                request.setParameterTypes(method.getParameterTypes());
                request.setParameters(args);
                
                // 获取 服务器地址
                if (discovery != null) {
                    serverAddress = discovery.discover();
                }
                String[] array = serverAddress.split(":");
                String host = array[0];		// 主机名
                int port = Integer.parseInt(array[1]);		// 端口号

                RpcClient client = new RpcClient(host, port); // 初始化 RPC 客户端
                RpcResponse response = client.send(request);  // 通过 RPC客户端发送RPC请求并获取RPC响应
                if (response.isError()) {
                    throw response.getError();
                } else {
                    return response.getResult();
                }
            });
}
```

### 4.

send 方法在 RpcClient 类中实现，基于 netty 框架，要求该类实现 SimpleChannelInboundHandler 接口。

该方法在

```java
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
```

### 5.

服务器端在初始化bean的时候执行，同步绑定到信道。

```java
public void afterPropertiesSet() throws Exception {
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) {
                        channel.pipeline()
                                // 添加解码器
                                .addLast(new RpcDecoder(RpcRequest.class))
                                // 添加编码器
                                .addLast(new RpcEncoder(RpcResponse.class))
                                // 添加处理器
                                .addLast(new RpcHandler(handlerMap));
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        String[] array = serverAddress.split(":");
        String host = array[0];
        int port = Integer.parseInt(array[1]);

        // 同步绑定
        ChannelFuture future = bootstrap.bind(host, port).sync();
        LOGGER.debug("server started on port {}", port);

        // 将服务器地址注册到zookeeper上
        if (registry != null) {
            registry.register(serverAddress);
        }

        future.channel().closeFuture().sync();
    } finally {
        // 关闭
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
}
```
