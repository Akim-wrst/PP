package com.example.pp.model;

import lombok.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    private String clientId;
    private String name;
    private String middleName;
    private String surname;
    private Long age;
    private LocalDate birthday;
    private String phone;
}
