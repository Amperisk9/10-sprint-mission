package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "notification")
@Getter
public class Notification extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "receiver_id", nullable = false)
  private User receiver;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String content;

  public Notification(User receiver, String title, String content) {
    super();
    this.receiver = receiver;
    this.title = title;
    this.content = content;
  }

  public boolean isOwnedBy(UUID userId) {
    return this.receiver.getId().equals(userId);
  }
}
