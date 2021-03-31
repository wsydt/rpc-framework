package com.wsy.demo.producer;

import com.wsy.demo.HelloService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangshuangyong 2021.3.30
 */

public class HelloServiceImpl implements HelloService {


    @Override
    public String sayHello(String name) {
        return "Welcome " + name + ", hello ";
    }

    @Override
    public List<String> getTag() {
        List<String> tagList = new ArrayList<>();
        tagList.add("poor");
        tagList.add("weak");
        tagList.add("useless");
        tagList.add("silly");
        tagList.add("ugly");
        tagList.add("selfish");
        return tagList;
    }
}
