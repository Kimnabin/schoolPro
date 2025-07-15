package school.xxxx.application.model.dto.user.request;

public class UserListReqDTO {
    private String username; // Optional filter
    private String email;    // Optional filter
    private int page = 0;
    private int size = 10;
    private String sortBy = "id";
    private String sortDirection = "asc";
}
