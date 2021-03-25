package com.wsy.server;

import com.wsy.common.Request;
import com.wsy.common.Response;
import com.wsy.common.Status;
import com.wsy.common.protocol.MessageProtocol;
import com.wsy.server.register.ServerRegister;
import com.wsy.server.register.ServiceObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author wangshuangyong 2021.3.22
 */

@Slf4j
@Data
public class RequestHandler {

    private MessageProtocol messageProtocol;

    private ServerRegister serverRegister;

    public byte[] handleRequest(byte[] data) throws Exception {
        Request request = messageProtocol.unmarshallingRequest(data);
        ServiceObject serviceObject = serverRegister.getServiceObject(request.getServiceName());
        Response response;
        if (serviceObject == null) {
            response = new Response(Status.NOT_FOUND);
        } else {
            try {
                Method method = serviceObject.getInter().getMethod(request.getMethod(), request.getParameterType());
                Object returnValue = method.invoke(serviceObject.getObject(), request.getParameter());
                response = new Response(Status.SUCCESS);
                response.setReturnValue(returnValue);
            } catch (Exception e) {
                e.printStackTrace();
                response = new Response(Status.ERROR);
                response.setException(e);
            }
        }
        return messageProtocol.marshallingResponse(response);
    }

}
