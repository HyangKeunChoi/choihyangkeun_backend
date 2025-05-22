package wirebarley.exception;

import wirebarley.common.ErrorInfo;

public class WithdrawLimitException extends BaseException {
    public WithdrawLimitException() {
        super(ErrorInfo.WITHDRAW_LIMIT_ERROR);
    }
}
