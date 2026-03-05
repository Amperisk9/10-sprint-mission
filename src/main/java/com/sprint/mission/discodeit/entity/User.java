package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@NoArgsConstructor
@Getter
public class User extends BaseUpdatableEntity {
    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    private BinaryContent profile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private UserStatus status;

    public User(String username, String password, String email) {
        super();
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void updateUserName(String username) {
        this.username = username;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateProfile(BinaryContent profile) {
        this.profile = profile;
    }

    public void updateStatus(UserStatus status) {
        if (this.status == null) {
            this.status = status;
            status.updateUser(this);
        }
    }

    @Override
    public String toString() {
        return String.format("'유저이름: %s / 메일: %s'",
                getUsername(), getEmail());
    }
}