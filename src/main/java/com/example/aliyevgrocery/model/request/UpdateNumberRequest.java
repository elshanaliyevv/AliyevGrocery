package com.example.aliyevgrocery.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateNumberRequest {

    @NotBlank
    @Pattern(regexp = "^(?:\\+994|0)?(?:50|51|55|70|77|99|10)\\d{7}$")
    private String number;
}
