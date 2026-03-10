package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageDto createMessage(MessageDto.MessageCreateRequest messageReq,
                             List<BinaryContentDto.BinaryContentCreateRequest> contentReqs) throws IOException;
    MessageDto findMessage(UUID uuid);
    PageResponse<MessageDto> findAllByChannelId(UUID channelId, Object cursor, Pageable pageable);
    MessageDto updateMessage(UUID uuid, MessageDto.MessageUpdateRequest messageReq);
    void deleteMessage(UUID uuid) throws IOException;
}
