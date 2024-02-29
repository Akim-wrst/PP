package com.example.pp.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Client entity")
public class Client {
    @Id
    @Schema(description = "Client ID")
    private String clientId;
    @Schema(description = "Client name")
    private String name;
    @Schema(description = "Client middle name")
    private String middleName;
    @Schema(description = "Client surname")
    private String surname;
    @Schema(description = "Client age in years")
    private Long age;
    @Schema(description = "Client birthday")
    private LocalDate birthday;
    @Schema(description = "Client phone number")
    private String phone;
}
