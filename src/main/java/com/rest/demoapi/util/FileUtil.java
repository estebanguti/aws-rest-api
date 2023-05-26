package com.rest.demoapi.util;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Service
public class FileUtil {

    public static InputStream getInputStream(MultipartFile multipartFile) {
        InputStream inputStream = null;
        try {
            inputStream = multipartFile.getInputStream();
        } catch (IOException e) {

        }
        return inputStream;
    }

    public static String getFileExtension(String fileName) {
        Optional<String> fileExtensionOpt = Optional.of(fileName)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(fileName.lastIndexOf(".") + 1));
        return fileExtensionOpt.orElseThrow(RuntimeException::new);
    }

    public static byte[] readBitmap(S3Object s3Object) {
        byte[] objectBytes = new byte[1024];
        try {
            S3ObjectInputStream s3is = s3Object.getObjectContent();
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            byte[] read_buf = new byte[1024];
            int read_len;
            while ((read_len = s3is.read(read_buf)) > 0) {
                os.write(read_buf, 0, read_len);
            }
            s3is.close();
            os.close();
            objectBytes = os.toByteArray();
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return objectBytes;
    }

}
