package wirebarley.exception;

import wirebarley.common.ErrorInfo;

public class AccountNotExistException extends BaseException {
    public AccountNotExistException() {
        super(ErrorInfo.ACCOUNT_NOT_EXIST_ERROR);
    }
}
