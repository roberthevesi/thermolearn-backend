package thermolearn.backend.api.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class S3Service {
    private final AmazonS3 s3client;

    @Autowired
    public S3Service(AmazonS3 s3client) {
        this.s3client = s3client;
    }

    public String uploadFile(String bucketName, byte[] fileBytes, String fileName, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(fileBytes.length);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileBytes);

        s3client.putObject(new PutObjectRequest(bucketName, fileName, byteArrayInputStream, metadata));
        return s3client.getUrl(bucketName, fileName).toString();
    }
}
