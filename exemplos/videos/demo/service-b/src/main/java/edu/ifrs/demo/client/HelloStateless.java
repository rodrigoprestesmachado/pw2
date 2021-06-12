package edu.ifrs.demo.client;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

@Stateless
public class HelloStateless {

    @Resource(lookup = "jms/JmsFactory")
    private ConnectionFactory factory;

    @Resource(lookup = "jms/MyQueue")
    private Queue queue;

    public void test() {
        System.out.println("Hello!!");
    }

    public void send() {

        // Creating a producer
        Connection connection;
        try {
            connection = factory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(queue);

            // Send the message
            Message message = session.createTextMessage();
            producer.send(message);
        } catch (JMSException e) {
            e.printStackTrace();
        }

        System.out.println("Sending a new message!!");

    }

}
