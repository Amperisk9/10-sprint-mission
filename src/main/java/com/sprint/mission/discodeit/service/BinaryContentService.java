package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentDto create(BinaryContentDto.BinaryContentCreateRequest createReq) throws IOException;
    BinaryContentDto findById(UUID uuid);
    List<BinaryContentDto> findAllByIdIn(List<UUID> uuids);
    void deleteById(UUID uuid) throws IOException;
}
