package school.xxxx.controller.http;

import lombok.extern.slf4j.Slf4j;  // Lombok cung cấp logger cho class
import org.springframework.http.ResponseEntity; // Để trả về HTTP response với status và body
import org.springframework.web.bind.annotation.GetMapping; // Đánh dấu phương thức xử lý GET request
import org.springframework.web.bind.annotation.RequestMapping; // Định nghĩa base URL cho controller
import org.springframework.web.bind.annotation.RestController; // Đánh dấu class là Rest API controller
import school.xxxx.controller.model.enums.ResultCode; // Enum mã kết quả (code)
import school.xxxx.controller.model.enums.ResultUtil; // Utility tạo đối tượng ResultMessage chuẩn
import school.xxxx.controller.model.vo.ResultMessage; // Wrapper chuẩn cho response trả về

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/home") // Base path cho toàn bộ API trong controller này
@Slf4j // Tự tạo biến logger để log thông tin (log.info, log.error)
public class HomeController {

    /**
     * Trang chủ API
     * URL: GET /api/v1/home
     * Trả về thông tin đơn giản về API như message, version, timestamp, trạng thái.
     */
    @GetMapping
    public ResponseEntity<ResultMessage<Map<String, Object>>> homepage() {
        try {
            log.info("Accessing homepage");

            // Chuẩn bị dữ liệu trả về dạng Map (key-value)
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Welcome to the XXXX School API");
            response.put("version", "1.0.0");
            response.put("timestamp", LocalDateTime.now()); // Thời gian hiện tại
            response.put("status", "active");

            // Trả về response với HTTP status 200 OK cùng dữ liệu bọc trong ResultMessage chuẩn
            return ResponseEntity.ok(ResultUtil.zdata(response));
        } catch (Exception e) {
            log.error("Error accessing homepage", e);

            // Nếu có lỗi, trả về HTTP 500 cùng message lỗi chuẩn
            return ResponseEntity.status(500)
                    .body(ResultUtil.error(ResultCode.ERROR));
        }
    }

    /**
     * Kiểm tra trạng thái hệ thống (health check)
     * URL: GET /api/v1/home/health
     * Trả về trạng thái "UP" và một số thông tin cơ bản
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
     * Thông tin API chi tiết hơn
     * URL: GET /api/v1/home/info
     * Trả về tên API, version, mô tả, tác giả, đường dẫn tài liệu
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
