package com.example.pp.clientDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "сlients")
@AllArgsConstructor
@NoArgsConstructor
public class ClientUnique {
    @Column(name = "full_name")
    private String fullName;
    @Id
    @Column(name = "phone")
    private String phone;
    @Column(name = "birthday")
    private LocalDate birthday;
    @Column(name = "message_send")
    private boolean messageSend;
}
