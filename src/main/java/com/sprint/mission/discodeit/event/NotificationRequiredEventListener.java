package com.sprint.mission.discodeit.event;

import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

public class NotificationRequiredEventListener {

  @TransactionalEventListener
  public void on(MessageCreatedEvent event) {

  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(RoleUpdatedEvent event) {
    
  }

}
