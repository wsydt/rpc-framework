package com.wsy.client;

import com.wsy.client.net.NetClient;
import com.wsy.common.Request;
import com.wsy.common.Response;
import com.wsy.common.protocol.MessageProtocol;
import com.wsy.discovery.ServiceInfo;
import com.wsy.discovery.ServiceInfoDiscovery;
import lombok.Data;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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
            String serviceName = inter.getName();
            List<ServiceInfo> serviceInfo = serviceInfoDiscovery.getServiceInfo(serviceName);
            if (serviceInfo.size() == 0) {
                throw new Exception("remote service not found ;");
            }
            ServiceInfo service = serviceInfo.get(random.nextInt(serviceInfo.size()));

            //创建 Request 对象实例
            Request request = new Request();
            request.setServiceName(service.getName());
            request.setMethod(method.getName());
            request.setParameterType(method.getParameterTypes());
            request.setParameter(args);

            //获取方法对应的协议
            MessageProtocol protocol = supportMessageProtocols.get(service.getProtocol());
            // 将 Request 编组
            byte[] data = protocol.marshallingRequest(request);
            //发送消息
            byte[] responseData = netClient.sendRequest(data, service);
            //解组响应结果
            Response response = protocol.unmarshallingResponse(responseData);
            //结果处理
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