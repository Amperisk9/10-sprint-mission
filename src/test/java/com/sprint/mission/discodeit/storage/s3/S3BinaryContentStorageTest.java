package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import software.amazon.awssdk.services.s3.S3Client;

@DisabledIfEnvironmentVariable(named = "GITHUB_ACTIONS", matches = "true")
@SpringBootTest(properties = "discodeit.storage.type=s3") // application설정 강제적용된 상태로 실행
@TestPropertySource(locations = "file:.env")
class S3BinaryContentStorageTest {

  @Autowired
  private S3BinaryContentStorage storage;
  @Autowired
  private S3Client s3Client;
  @Value("${discodeit.storage.s3.bucket}")
  private String bucket;

  private UUID testId;

  @AfterEach
  void dataDelete() {
    if (testId == null) {
      return;
    }

    s3Client.deleteObject(req -> req.bucket(bucket).key(testId.toString()));
  }

  @Nested
  class put {

    @Test
    @DisplayName("업로드 성공 후 컨텐츠ID 반환")
    void should_return_content_id_when_upload_succeeds() {
      // given
      testId = UUID.randomUUID();
      byte[] bytes = "test".getBytes();

      // when
      UUID returnId = storage.put(testId, bytes);

      // then
      assertEquals(testId, returnId);
    }

  }

  @Nested
  class get {

    @Test
    @DisplayName("컨텐츠ID로 파일받기 성공 후 Stream 반환")
    void should_return_inputStream_when_content_exists() throws IOException {
      // given
      testId = UUID.randomUUID();
      // 1x1 투명 PNG 형태의 Base64
      String base64Image = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=";
      byte[] imageBytes = Base64.getDecoder().decode(base64Image);
      storage.put(testId, imageBytes);

      // when
      InputStream iStream = storage.get(testId);

      // then
      assertArrayEquals(imageBytes, iStream.readAllBytes());
    }

    @Test
    @DisplayName("존재하지 않는 컨텐츠ID로 파일받기 실패")
    void should_throw_exception_when_content_id_not_exists() {
      // when, then
      assertThrows(Exception.class, () -> storage.get(UUID.randomUUID()));
    }

  }

  @Nested
  class download {

    @Test
    @DisplayName("컨텐츠ID로 presignedUrl 성공 후 ResponseEntity 반환")
    void should_return_response_entity_when_presigned_url_succeed() {
      // given
      testId = UUID.randomUUID();
      byte[] bytes = "test".getBytes();
      storage.put(testId, bytes);
      BinaryContentDto dto = new BinaryContentDto(testId, null, bytes.length, "text/plain");

      // when
      ResponseEntity<Void> responseEntity = storage.download(dto);

      // then
      assertThat(responseEntity).satisfies(res -> {
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(res.getHeaders().getLocation()).isNotNull();
      });

    }
  }
}