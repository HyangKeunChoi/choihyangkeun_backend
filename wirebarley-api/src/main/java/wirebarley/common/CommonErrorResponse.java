package wirebarley.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CommonErrorResponse {
    private String message;
    private ErrorInfo errorInfo;
}
