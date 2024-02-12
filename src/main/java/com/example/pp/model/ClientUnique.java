package com.example.pp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "сlients")
public class ClientUnique {
    private String fullName;
    @Id
    private String phone;
    private LocalDate birthday;
    private boolean messageSend;
}
