package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.NotificationDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.notification.NotificationAccessDeniedException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

  private final ReadStatusRepository readStatusRepository;
  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Override
  public void registerMessageCreatedNotification(UUID channelId, Message message) {
    List<ReadStatus> readStatusesExceptAuthor = readStatusRepository
        .findAllByChannelId(channelId).stream()
        .filter(ReadStatus::isNotificationEnabled)
        .filter(rs -> !rs.getUser().getId().equals(message.getAuthor().getId()))
        .toList();

    if (readStatusesExceptAuthor.isEmpty()) {
      return;
    }

    List<Notification> notifications = readStatusesExceptAuthor.stream()
        .map(rs -> new Notification(
            rs.getUser(),
            getMessageEventTitle(message),
            message.getContent()))
        .toList();
    notificationRepository.saveAll(notifications);
    log.info("notification [MessageCrated] 생성: messageId={}, notificationSize={}",
        message.getId(), notifications.size());
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Override
  public void registerRoleUpdatedNotification(RoleUpdatedEvent event) {
    Notification notification = new Notification(
        event.user(),
        "권한이 변경되었습니다",
        event.oldRole() + " -> " + event.newRole());

    notificationRepository.save(notification);
    log.info("notification [RoleUpdated] 생성");
  }

  @Override
  public List<NotificationDto> getNotifications(UUID receiverId) {
    return notificationRepository.findAllByReceiverId(receiverId).stream()
        .map(notificationMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  public void deleteNotification(UUID notificationId, UUID userId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotificationNotFoundException());
    if (!notification.isOwnedBy(userId)) {
      throw new NotificationAccessDeniedException();
    }

    log.info("알람 삭제: id={}", notificationId);
    notificationRepository.delete(notification);
  }

  private String getMessageEventTitle(Message message) {
    String channelName = message.getChannel().getName() != null ?
        message.getChannel().getName() : "개인채널";
    return message.getAuthor().getUsername() + " (#" + channelName + ")";
  }
}
