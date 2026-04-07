package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "discodeit.s3")
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

  private String accessKey;
  private String secretKey;
  private String region;
  private String bucket;

  @Override
  public UUID put(UUID binaryContentId, byte[] bytes) throws IOException {
    return null;
  }

  @Override
  public InputStream get(UUID binaryContentId) throws IOException {
    return null;
  }

  @Override
  public void delete(UUID binaryContentId) throws IOException {

  }

  @Override
  public ResponseEntity<?> download(BinaryContentDto dto) throws IOException {
    return null;
  }
}
