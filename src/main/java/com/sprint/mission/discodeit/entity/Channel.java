package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import lombok.Getter;

import java.util.*;

@Getter
public class Channel extends BaseUpdatableEntity {
    private final ChannelType type;
    private final Set<UUID> participants;
    private final List<UUID> messages;
    private String name;
    private String description;


    private Channel(ChannelType type, String name, String description, List<UUID> participantIds) {
        super();
        this.participants = participantIds == null ? new HashSet<>() : new HashSet<>(participantIds);
        this.messages = new ArrayList<>();
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public static Channel of(String name, String description) {
        return new Channel(ChannelType.PUBLIC, name, description, null);
    }

    public static Channel of(List<UUID> participantIds) {
        return new Channel(ChannelType.PRIVATE, null, null, participantIds);
    }

    // participants
    public Set<UUID> getParticipants() {
        return Collections.unmodifiableSet(this.participants);
    }

    public void addParticipant(UUID userId) {
        participants.add(userId);
    }

    public void removeParticipant(UUID userId) {
        participants.remove(userId);
    }

    // messages
    public List<UUID> getMessages() {
        return Collections.unmodifiableList(this.messages);
    }

    public void addMessage(UUID messageId) {
        this.messages.add(messageId);
    }

    public void removeMessage(UUID messageId) {
        this.messages.remove(messageId);
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("'채널이름: %s / 채널설명:%s'", getName(), getDescription());
    }
}


