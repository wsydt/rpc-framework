package com.wsy.client;

import com.wsy.client.net.NetClient;
import com.wsy.client.net.NettyNetClient;
import com.wsy.common.Request;
import com.wsy.common.Response;
import com.wsy.common.protocol.MessageProtocol;
import com.wsy.discovery.ServiceInfo;
import com.wsy.discovery.ServiceInfoDiscovery;
import com.wsy.discovery.ZooKeeperServiceInfoDiscovery;
import lombok.Data;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author wangshuangyong 2021.3.22
 */
@Data
public class ClientStubProxyFactory {

    private ServiceInfoDiscovery serviceInfoDiscovery;

    private Map<String, MessageProtocol> supportMessageProtocols;

    private NetClient netClient;

    private Map<Class<?>, Object> objectCache;

    public ClientStubProxyFactory() throws IOException, InterruptedException {
        this(new ZooKeeperServiceInfoDiscovery(), new NettyNetClient());
    }

    public ClientStubProxyFactory(ServiceInfoDiscovery serviceInfoDiscovery) {
        this(serviceInfoDiscovery, new NettyNetClient());
    }

    public ClientStubProxyFactory(NetClient netClient) throws IOException, InterruptedException {
        this(new ZooKeeperServiceInfoDiscovery(), netClient);
    }

    public ClientStubProxyFactory(ServiceInfoDiscovery serviceInfoDiscovery, NetClient netClient) {
        this.serviceInfoDiscovery = serviceInfoDiscovery;
        this.netClient = netClient;
        this.objectCache = new HashMap<>();
        this.supportMessageProtocols = new HashMap<>();
    }

    private class ClientStubInvocationHandler implements InvocationHandler{

        private Class<?> inter;

        private Random random;

        public ClientStubInvocationHandler(Class<?> inter) {
            super();
            this.inter = inter;
            this.random = new Random();
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String serviceName = inter.getSimpleName();
            List<ServiceInfo> serviceInfo = serviceInfoDiscovery.getServiceInfo(serviceName);
            if (serviceInfo.size() == 0) {
                throw new Exception("remote service not found ;");
            }
            ServiceInfo service = serviceInfo.get(random.nextInt(serviceInfo.size()));

            //?????? Request ????????????
            Request request = new Request();
            request.setServiceName(service.getName());
            request.setMethod(method.getName());
            request.setParameterType(method.getParameterTypes());
            request.setParameter(args);

            System.out.println(method.getReturnType());
            //???????????????????????????
            MessageProtocol protocol = supportMessageProtocols.get(service.getProtocol());
            if (protocol == null) {
                protocol = (MessageProtocol) Class.forName(service.getProtocol()).newInstance();
                supportMessageProtocols.put(protocol.getClass().getName(), protocol);
            }
            // ??? Request ??????
            byte[] data = protocol.marshallingRequest(request);
            //????????????
            byte[] responseData = netClient.sendRequest(data, service);
            //??????????????????
            Response response = protocol.unmarshallingResponse(responseData);
            //????????????
            if (response.getException() != null) {
                throw response.getException();
            }
            return response.getReturnValue();
        }
    }

    public <T> T getProxy(Class<T> inter){
        T object = (T) objectCache.get(inter);
        if (object == null) {
            object = (T) Proxy.newProxyInstance(inter.getClassLoader(), new Class[]{inter}, new ClientStubInvocationHandler(inter));
            objectCache.put(inter, object);
        }
        return object;
    }

}
