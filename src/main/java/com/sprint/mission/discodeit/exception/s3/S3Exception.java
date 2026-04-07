package com.sprint.mission.discodeit.exception.s3;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class S3Exception extends DiscodeitException {

  public S3Exception(Throwable e) {
    super(ErrorCode.S3_UPLOAD_FAIL, e);
  }
}
