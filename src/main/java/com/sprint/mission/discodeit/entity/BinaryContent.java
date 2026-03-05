package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import lombok.Getter;

@Getter
public class BinaryContent extends BaseEntity {
    private final String fileName;
    private final long size;
    private final String contentType;
    private final byte[] bytes;
    private final String url;

    public BinaryContent(String fileName, BinaryContentType contentType, byte[] bytes, String url) {
        super();
        this.fileName = fileName;
        this.size = bytes.length;
        this.contentType = contentType.toString();
        this.bytes = bytes;
        this.url = url;
    }
}
