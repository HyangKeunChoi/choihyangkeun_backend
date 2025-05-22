package wirebarley.exception;


import lombok.Getter;
import wirebarley.common.ErrorInfo;

@Getter
public abstract class BaseException extends RuntimeException {
    private final ErrorInfo errorInfo;

    public BaseException(ErrorInfo errorInfo) {
        super(errorInfo.getErrorMsg());
        this.errorInfo = errorInfo;
    }
}
