package models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserModel {

    private String name;
    private String job;
}