package com.emergency.panicbutton.config;

import com.emergency.panicbutton.common.ApiError;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Shapes container-level errors (unmatched routes, unsupported methods that
 * bypass the advice, etc.) into the FastAPI-compatible error envelope using the
 * {@code HTTP_<status>} error-code convention.
 */
@RestController
public class ApiErrorController implements ErrorController {

    @RequestMapping("${server.error.path:${error.path:/error}}")
    public ResponseEntity<ApiError> handleError(HttpServletRequest request) {
        HttpStatus status = resolveStatus(request);
        return ResponseEntity.status(status)
                .body(ApiError.of(
                        "HTTP_" + status.value(),
                        status.getReasonPhrase()));
    }

    private HttpStatus resolveStatus(HttpServletRequest request) {
        Object code = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (code != null) {
            try {
                HttpStatus resolved = HttpStatus.resolve(Integer.parseInt(code.toString()));
                if (resolved != null) {
                    return resolved;
                }
            } catch (NumberFormatException ignored) {
                // fall through to 500
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
