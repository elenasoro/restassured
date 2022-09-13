package models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterUserModel {
    private String email;
    private String password;
}
