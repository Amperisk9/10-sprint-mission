package com.sprint.mission.discodeit.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration                                                // 스프링 설정 정보를 담고있는 클래스 표시
@ConfigurationProperties(prefix = "discodeit.storage.s3")     // 환경변수에 작성된 값을 자바 객체에 바인딩
public class S3Properties {

  private String accessKey;
  private String secretKey;
  private String region;
  private String bucket;
  private long presignedUrlExpiration;

}
