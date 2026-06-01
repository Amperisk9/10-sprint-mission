package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.NotificationDto;
import java.util.List;
import java.util.UUID;

public interface NotificationService {

  void registerNotification();

  List<NotificationDto> getNotifications();

  void deleteNotification(UUID notificationId);

}
