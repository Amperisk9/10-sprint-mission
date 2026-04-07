package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.config.S3Properties;
import com.sprint.mission.discodeit.exception.s3.S3Exception;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;


@RequiredArgsConstructor
@RestController
@RequestMapping("/s3test")
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "dev")
public class AWSS3Test {

  private final S3Properties props;
  private final S3Client client;
  private final S3Presigner preSigner;

  @PostMapping
  public ResponseEntity<Void> upload(@RequestParam("file") MultipartFile file) {
    String url = store(file);
    return ResponseEntity.status(HttpStatus.CREATED)
        .location(URI.create(url))
        .build();
  }

  @GetMapping
  public ResponseEntity<Void> download(@RequestParam("key") String key) {
    GetObjectRequest request = GetObjectRequest.builder()
        .bucket(props.getBucket())
        .key(key)
        // 보안상 문제로 inline 대신 attachment를 써서 강제로 다운받게 하기
        .responseContentDisposition("attachment; filename=\"" + key + "\"")
        .build();

    String signed = preSigner.presignGetObject(req ->
        req.getObjectRequest(request)
            .signatureDuration(Duration.ofSeconds(props.getPresignedUrlExpiration())) // 유효기간(단위: 초)
    ).url().toString();

    return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(signed))
        .build();
  }

  private String store(MultipartFile file) {
    try {
      String key = makeS3ObjectKey("image", file);
      client.putObject(req ->
              req.bucket(props.getBucket())
                  .key(key)
                  .contentType(file.getContentType()),
          RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

      return buildPublicUrl(props.getBucket(), props.getRegion(), key);
    } catch (Exception e) {
      throw new S3Exception(e);
    }
  }

  private String makeS3ObjectKey(String rootPath, MultipartFile file) {
    String originalName = file.getOriginalFilename();
    String ext = "";
    if (originalName != null && originalName.contains(".")) {
      ext = originalName.substring(originalName.lastIndexOf(".") + 1);
    }
    LocalDate now = LocalDate.now();
    String datePath = "%04d/%02d".formatted(now.getYear(), now.getMonthValue());
    String filename = UUID.randomUUID().toString() + (ext.isEmpty() ? "" : "." + ext);
    return rootPath + "/" + datePath + "/" + filename;
  }

  private String buildPublicUrl(String bucket, String region, String key) {
    String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8).replace("+", "%20");
    if (region == null || region.isBlank() || "us-east-1".equals(region)) {
      return "https://" + bucket + ".s3.amazonaws.com/" + encodedKey;
    }
    return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + encodedKey;
  }
}
