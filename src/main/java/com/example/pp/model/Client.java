package com.example.pp.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Data
public class Client {
    @Id
    private String clientId;
    private String name;
    private String middleName;
    private String surname;
    private Long age;
    private LocalDate birthday;
    private String phone;
}
