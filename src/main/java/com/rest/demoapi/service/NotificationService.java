package com.rest.demoapi.service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.AmazonSNSException;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.UnsubscribeRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NotificationService implements INotificationService {

    private static final String SNS_PROTOCOL = "email";

    @Value("${sns.topic-arn}")
    private String snsTopic;

    @Value("${sqs.queue-url}")
    private String sqsQueue;

    @Autowired
    private AmazonSNS snsClient;

    @Autowired
    private AmazonSQS sqsClient;

    @Override
    public void subscribeEmail(String email) {
        try {
            var request = new SubscribeRequest()
                    .withProtocol(SNS_PROTOCOL)
                    .withEndpoint(email)
                    .withTopicArn(snsTopic);
            snsClient.subscribe(request);
        } catch (AmazonSNSException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @Override
    public void unsubscribeEmail(String email) {
        try {
            var listResult = snsClient.listSubscriptionsByTopic(snsTopic);
            var subscriptions = listResult.getSubscriptions();
            subscriptions.stream()
                    .filter(subscription -> email.equals(subscription.getEndpoint()))
                    .findAny()
                    .ifPresent(subscription -> unsubscribe(subscription.getSubscriptionArn()));
        } catch (AmazonSNSException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @Override
    public void sendMessageToQueue(String message) {
        try {
            var request = new SendMessageRequest()
                    .withQueueUrl(sqsQueue)
                    .withMessageBody(message)
                    .withDelaySeconds(5);
            sqsClient.sendMessage(request);
        } catch (AmazonSQSException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @Override
    public void sendMessageToTopic(String message) {
        try {
            var publishRequest = new PublishRequest()
                    .withMessage(message)
                    .withTopicArn(snsTopic);
            snsClient.publish(publishRequest);
        } catch (AmazonSNSException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @Override
    public List<Message> readMessages() {
        try {
            var request = new ReceiveMessageRequest()
                    .withQueueUrl(sqsQueue)
                    .withWaitTimeSeconds(10)
                    .withMaxNumberOfMessages(10);
            var messages = sqsClient.receiveMessage(request).getMessages();
            messages.stream()
                    .map(Message::getReceiptHandle)
                    .forEach(receipt -> sqsClient.deleteMessage(sqsQueue, receipt));
            return messages;
        } catch (AmazonSQSException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    private void unsubscribe(String subscriptionArn) {
        try {
            var unsubscribeRequest = new UnsubscribeRequest()
                    .withSubscriptionArn(subscriptionArn);
            snsClient.unsubscribe(unsubscribeRequest);
        } catch (AmazonSNSException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

}
