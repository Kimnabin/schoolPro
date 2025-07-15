package school.xxxx.controller.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.xxxx.controller.model.enums.ResultCode;
import school.xxxx.controller.model.enums.ResultUtil;
import school.xxxx.controller.model.vo.ResultMessage;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/home")
@Slf4j
public class HomeController {

    /**
     * Trang chủ API
     */
    @GetMapping
    public ResponseEntity<ResultMessage<Map<String, Object>>> homepage() {
        try {
            log.info("Accessing homepage");

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Welcome to the XXXX School API");
            response.put("version", "1.0.0");
            response.put("timestamp", LocalDateTime.now());
            response.put("status", "active");

            return ResponseEntity.ok(ResultUtil.zdata(response));
        } catch (Exception e) {
            log.error("Error accessing homepage", e);
            return ResponseEntity.status(500)
                    .body(ResultUtil.error(ResultCode.ERROR));
        }
    }

    /**
     * Kiểm tra trạng thái hệ thống
     */
    @GetMapping("/health")
    public ResponseEntity<ResultMessage<Map<String, Object>>> healthCheck() {
        try {
            log.info("Health check requested");

            Map<String, Object> healthInfo = new HashMap<>();
            healthInfo.put("status", "UP");
            healthInfo.put("timestamp", LocalDateTime.now());
            healthInfo.put("service", "xxxx-school-api");

            return ResponseEntity.ok(ResultUtil.zdata(healthInfo));
        } catch (Exception e) {
            log.error("Health check failed", e);
            return ResponseEntity.status(500)
                    .body(ResultUtil.error(ResultCode.ERROR));
        }
    }

    /**
     * Thông tin API
     */
    @GetMapping("/info")
    public ResponseEntity<ResultMessage<Map<String, Object>>> apiInfo() {
        try {
            log.info("API info requested");

            Map<String, Object> apiInfo = new HashMap<>();
            apiInfo.put("name", "XXXX School Management API");
            apiInfo.put("version", "1.0.0");
            apiInfo.put("description", "RESTful API for school management system");
            apiInfo.put("author", "XXXX School Team");
            apiInfo.put("documentation", "/api/docs");

            return ResponseEntity.ok(ResultUtil.zdata(apiInfo));
        } catch (Exception e) {
            log.error("Error getting API info", e);
            return ResponseEntity.status(500)
                    .body(ResultUtil.error(ResultCode.ERROR));
        }
    }
}