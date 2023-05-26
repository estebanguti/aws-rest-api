package com.rest.demoapi.listener;

import com.rest.demoapi.service.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationQueueListener {

    @Autowired
    private INotificationService notificationService;

//    @Scheduled(fixedRate = 3000)
//    public void readBatchFromQueueAndPushToTopic() {
//        var messages = notificationService.readMessages();
//        messages.forEach(message -> notificationService.sendMessageToTopic(message.getBody()));
//    }

}
