package com.rest.demoapi.service;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.InvokeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LambdaService implements ILambdaService {

    @Value("${lambda.function-arn}")
    private String lambdaFunction;

    @Autowired
    private AWSLambda awsLambda;

    @Override
    public void trigger() {
        InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName(lambdaFunction)
                .withPayload("{\"detail-type\": \"Demp API application\"}");
        awsLambda.invoke(invokeRequest);
    }

}
