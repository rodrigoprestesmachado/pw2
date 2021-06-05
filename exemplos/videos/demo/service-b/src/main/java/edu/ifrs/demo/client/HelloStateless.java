package edu.ifrs.demo.client;

import javax.ejb.Stateless;

@Stateless
public class HelloStateless {

    public void test() {
        System.out.println("Hello");
    }

}
