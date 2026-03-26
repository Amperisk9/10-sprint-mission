package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentMapper mapper;

  @Transactional
  @Override
  public BinaryContentDto create(MultipartFile attachment) throws IOException {
    if (attachment == null) {
      log.warn("[Service] attachment is null");
      return null;  // TODO exception으로 교체 필요
    }

    BinaryContent content = new BinaryContent(attachment.getOriginalFilename(),
        attachment.getSize(), attachment.getContentType());
    binaryContentRepository.save(content);
    binaryContentStorage.put(content.getId(), attachment.getBytes());
    log.info("[Service] create: 첨부파일 저장 성공");

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

  @Transactional
  @Override
  public void deleteById(UUID uuid) throws IOException {
    binaryContentRepository.findById(uuid)
        .orElseThrow(() -> {
          log.warn("[Service] BinaryContent not found: id={}", uuid);
          return new BusinessLogicException(ErrorCode.BINARYCONTENT_NOT_FOUND);
        });
    binaryContentStorage.delete(uuid);
    binaryContentRepository.deleteById(uuid);
    log.info("[Service] delete: 첨부파일 삭제 성공");
  }

  private BinaryContentDto toResponse(BinaryContent binaryContent) {
    return mapper.toDto(binaryContent);
  }
}
