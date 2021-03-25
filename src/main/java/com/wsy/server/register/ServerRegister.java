package com.wsy.server.register;

public interface ServerRegister {

    void regiest(ServiceObject serviceObject, int port, String protocol);

    ServiceObject getServiceObject(String name);


}
