package wirebarley.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorInfo {
    INTERNAL_SERVER_ERROR(500, "일시적인 오류가 발생했습니다. 잠심 후 다시 시도해 주세요."),
    ACCOUNT_NOT_EXIST_ERROR(501, "계좌를 찾을 수 없습니다."),
    ;

    private final int errorCode;
    private final String errorMsg;

    public String getErrorMsg(Object... arg) {
        return String.format(errorMsg, arg);
    }
}
