package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import lombok.Getter;

import java.util.*;

@Getter
public class Message extends BaseUpdatableEntity {
    private String content;
    private final Channel channel;
    private final User author;
    private final List<BinaryContent> attachments = new ArrayList<>();


    public Message(Channel channel, User author, String content) {
        super();
        this.channel = channel;
        this.author = author;
        this.content = content;
    }

    public List<BinaryContent> getAttachments() {
        return Collections.unmodifiableList(this.attachments);
    }
    public void addAttachmentId(BinaryContent attachment) {
        this.attachments.add(attachment);
    }
    public void removeAttachmentId(BinaryContent attachment) {
        this.attachments.remove(attachment);
    }

    public void updateMessage(String message) {
        this.content = message;
    }

    @Override
    public String toString() {
        return String.format("'채널ID: %s / 유저ID: %s / 채팅메세지: %s'", getChannel(), getAuthor(), getContent());
    }
}
