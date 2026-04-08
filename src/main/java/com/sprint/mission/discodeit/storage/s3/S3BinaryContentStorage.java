package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.exception.s3.S3DownloadFailException;
import com.sprint.mission.discodeit.exception.s3.S3UploadFailException;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final S3Client s3Client;
  private final S3Presigner s3Presigner;

  @Value("${discodeit.storage.s3.access-key}")
  private String accessKey;
  @Value("${discodeit.storage.s3.secret-key}")
  private String secretKey;
  @Value("${discodeit.storage.s3.region}")
  private String region;
  @Value("${discodeit.storage.s3.bucket}")
  private String bucket;
  @Value("${discodeit.storage.s3.presigned-url-expiration}")
  private long presignedUrlExpiration;

  @Override
  public UUID put(UUID binaryContentId, byte[] bytes) {
    String key = binaryContentId.toString();

    try {
      s3Client.putObject(req -> req.bucket(bucket).key(key), RequestBody.fromBytes(bytes));
      log.info("[Storage] 파일 저장 성공: {}", binaryContentId);

      return binaryContentId;
    } catch (RuntimeException e) {
      throw new S3UploadFailException(e);
    }
  }

  @Override
  public InputStream get(UUID binaryContentId) {
    String key = binaryContentId.toString();

    try {
      byte[] bytes = s3Client.getObjectAsBytes(req -> req.bucket(bucket).key(key)).asByteArray();

      return new ByteArrayInputStream(bytes);
    } catch (RuntimeException e) {
      throw new S3DownloadFailException(e);
    }
  }

  @Override
  public void delete(UUID binaryContentId) {
  }

  @Override
  public ResponseEntity<Void> download(BinaryContentDto dto) {
    String presignedUrl = generatePresignedUrl(dto.id().toString(), dto.contentType());
    log.info("[Storage] presignedUrl 발급 성공: {}", presignedUrl);

    return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(presignedUrl))
        .build();
  }

  private String generatePresignedUrl(String key, String contentType) {
    GetObjectRequest request = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .responseContentType(contentType)
        .build();

    PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(
        req -> req.getObjectRequest(request)
            .signatureDuration(Duration.ofSeconds(presignedUrlExpiration))); // 유효기간(단위: 초))

    return presignedRequest.url().toString();
  }
}
