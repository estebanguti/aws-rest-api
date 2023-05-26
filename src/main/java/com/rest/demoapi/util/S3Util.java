package com.rest.demoapi.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.InputStream;


@Service
public class S3Util {

    @Autowired
    private AmazonS3 s3;

    public static final String S3_BUCKET_NAME = "images-demo-rds";

    public Resource downloadObject(String id) {
        checkBucketExists();
        checkObjectExits(id);

        S3Object s3Object = s3.getObject(S3_BUCKET_NAME, id);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        return new InputStreamResource(inputStream);
    }

    public void uploadObject(InputStream file, String filename, String customName) {
        checkBucketExists();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.addUserMetadata("Name", filename);
        metadata.setContentType("image/jpg");
        PutObjectRequest request = new PutObjectRequest(S3_BUCKET_NAME, customName, file, metadata);
        request.setMetadata(metadata);
        s3.putObject(request);
    }

    public void deleteObject(String objectName) {
        checkBucketExists();
        checkObjectExits(objectName);
        s3.deleteObject(S3_BUCKET_NAME, objectName);
    }

    private void checkBucketExists() {
        if (!s3.doesBucketExistV2(S3_BUCKET_NAME)) {
            s3.createBucket(S3_BUCKET_NAME);
        }
    }

    private void checkObjectExits(String objectName) {
        if (!s3.doesObjectExist(S3_BUCKET_NAME, objectName)) {
            throw new RuntimeException("Bucket doesn't exist");
        }
    }

}
