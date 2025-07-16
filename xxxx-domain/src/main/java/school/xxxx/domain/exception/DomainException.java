package school.xxxx.domain.exception;

import lombok.Getter;

@Getter
public abstract class DomainException extends RuntimeException {
  private final String errorCode;

  protected DomainException(String message, String errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  protected DomainException(String message, Throwable cause, String errorCode) {
    super(message, cause);
    this.errorCode = errorCode;
  }

}
