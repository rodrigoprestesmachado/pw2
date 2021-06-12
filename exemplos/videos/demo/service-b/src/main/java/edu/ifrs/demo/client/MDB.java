package edu.ifrs.demo.client;

import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;

@MessageDriven
public class MDB implements MessageListener {

    @Override
    public void onMessage(Message message) {
        System.out.println("Hello MDB");
    }

}
