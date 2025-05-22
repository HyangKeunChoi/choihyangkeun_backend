package wirebarley.exception;

import wirebarley.common.ErrorInfo;

public class InsufficientException extends BaseException {
    public InsufficientException() {
        super(ErrorInfo.INSUFFICIENT_BALANCE_ERROR);
    }
}
