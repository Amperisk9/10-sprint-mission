package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.NotificationDto;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

  @Override
  public void registerNotification() {

  }

  @Override
  public List<NotificationDto> getNotifications() {
    return List.of();
  }

  @Override
  public void deleteNotification(UUID notificationId) {

  }
}
