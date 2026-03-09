package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;

@Entity
@NoArgsConstructor(access = )
@Getter
public class UserStatus extends BaseUpdatableEntity {
    private static final long ONLINE_TIME_OUT_MS = 5 * 60_000;  // 5분

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Instant lastActiveAt;



    public UserStatus(User user) {
        super();
        this.user = user;
        this.lastActiveAt = Instant.now();
    }

    public void updateUser(User user) {
        if (this.user == null) {
            this.user = user;
            user.updateStatus(this);
        }
    }

    public void updateLastActiveAt(Instant lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }

    public boolean isOnline() {
        return Duration.between(this.lastActiveAt, Instant.now()).toMillis() <= ONLINE_TIME_OUT_MS;
    }
}
