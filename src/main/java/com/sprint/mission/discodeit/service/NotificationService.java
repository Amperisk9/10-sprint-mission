package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.NotificationDto;
import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;

public interface NotificationService {

  void registerNotification(UUID channelId, Message message);

  List<NotificationDto> getNotifications(UUID receiverId);

  void deleteNotification(UUID notificationId, UUID userId);

}
