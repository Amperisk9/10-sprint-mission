package com.sprint.mission.discodeit.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

  public interface CacheNames {

    String CHANNELS_BY_USER = "channelsByUser";
    String USER_CACHE = "userCache";
    String NOTIFICATIONS_BY_USER = "notificationsByUser";
  }
}
