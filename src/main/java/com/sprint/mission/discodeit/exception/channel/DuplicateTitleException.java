package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class DuplicateTitleException extends ChannelException {

  public DuplicateTitleException() {
    super(ErrorCode.DUPLICATE_TITLE);
  }
}
