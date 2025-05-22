package wirebarley.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wirebarley.common.CommonErrorResponse;
import wirebarley.common.ErrorInfo;

@Slf4j
@RestControllerAdvice
public class LogicExceptionHandler {

    /*
     * http status: 500
     * 모니터링 필요
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<CommonErrorResponse> handleException(Exception e) {
        log.error(e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new CommonErrorResponse(e.getMessage(), ErrorInfo.INTERNAL_SERVER_ERROR));
    }

    /*
     * http status: 200
     * 비즈니스 로직 처리
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = BaseException.class)
    public ResponseEntity<CommonErrorResponse> handleBaseException(BaseException e) {
        return ResponseEntity.status(e.getErrorInfo().getErrorCode())
            .body(new CommonErrorResponse(e.getErrorInfo().getErrorMsg(), e.getErrorInfo()));
    }
}
