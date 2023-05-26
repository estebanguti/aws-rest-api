package com.rest.demoapi.controller;

import com.rest.demoapi.service.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private INotificationService notificationService;

    @PostMapping("/subscription/{email}")
    public void subscribeEmail(@PathVariable String email) {
        notificationService.subscribeEmail(email);
    }

    @DeleteMapping("/subscription/{email}")
    public void unsubscribeEmail(@PathVariable String email) {
        notificationService.unsubscribeEmail(email);
    }

}
