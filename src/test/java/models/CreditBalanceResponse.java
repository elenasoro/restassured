package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreditBalanceResponse {

    @JsonProperty("user_id")
    private Long userId;

    private String subscription;
    private Integer balance;
    private String currency;
}
