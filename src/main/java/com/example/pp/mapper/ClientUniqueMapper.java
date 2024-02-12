package com.example.pp.mapper;

import com.example.pp.model.Client;
import com.example.pp.model.ClientUnique;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientUniqueMapper {
    @Mapping(target = "fullName", expression = "java(client.getName() + ' ' + client.getMiddleName() + ' ' + client.getSurname())")
    @Mapping(target = "phone", source = "client.phone")
    @Mapping(target = "birthday", source = "client.birthday")
    @Mapping(target = "messageSend", ignore = true)
    ClientUnique toUniqueClient(Client client);
}
