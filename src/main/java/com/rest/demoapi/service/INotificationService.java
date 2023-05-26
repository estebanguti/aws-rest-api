package com.rest.demoapi.service;

import com.amazonaws.services.sqs.model.Message;

import java.util.List;

public interface INotificationService {

    void subscribeEmail(String email);

    void unsubscribeEmail(String email);

    void sendMessageToQueue(String message);

    void sendMessageToTopic(String message);

    List<Message> readMessages();

}
