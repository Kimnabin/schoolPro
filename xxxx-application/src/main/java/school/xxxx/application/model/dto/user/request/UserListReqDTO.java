package school.xxxx.application.model.dto.user.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
@Data
public class UserListReqDTO {

    private String username; // Optional filter

    private String email;    // Optional filter

    @Min(value = 0, message = "Page number cannot be negative")
    private int page = 0;

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    private int size = 10;

    @Pattern(regexp = "^(id|username|email|fullName|createdAt)$",
            message = "Sort field must be one of: id, username, email, fullName, createdAt")
    private String sortBy = "id";

    @Pattern(regexp = "^(asc|desc)$",
            message = "Sort direction must be either 'asc' or 'desc'")
    private String sortDirection = "asc";
}