package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentMapper mapper;

    @Override
    public BinaryContentDto create(BinaryContentDto.BinaryContentCreateRequest req) throws IOException {
        BinaryContent content = new BinaryContent(req.filename(), req.bytes().length, req.contentType());
        binaryContentRepository.save(content);
        binaryContentStorage.put(content.getId(),req.bytes());
        return toResponse(content);
    }

    @Override
    public BinaryContentDto findById(UUID uuid) {
        return binaryContentRepository.findById(uuid)
                .map(this::toResponse)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.BINARYCONTENT_NOT_FOUND));
    }

    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> uuids) {
        return binaryContentRepository.findAllByIdIn(uuids).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void deleteById(UUID uuid) throws IOException{
        binaryContentRepository.findById(uuid)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.BINARYCONTENT_NOT_FOUND));
        binaryContentStorage.delete(uuid);
        binaryContentRepository.deleteById(uuid);
    }

    private BinaryContentDto toResponse(BinaryContent binaryContent) {
        return mapper.toDto(binaryContent);
    }
}
