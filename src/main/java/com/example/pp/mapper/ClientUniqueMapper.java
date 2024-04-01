package com.example.pp.mapper;

import com.example.pp.clientDTO.ClientUnique;
import com.example.pp.model.Client;
import com.example.pp.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientUniqueMapper {
    @Mapping(target = "fullName", expression = "java(client.getName() + ' ' + client.getMiddleName() + ' ' + client.getSurname())")
    @Mapping(target = "phone", source = "client.phone")
    @Mapping(target = "birthday", source = "client.birthday")
    @Mapping(target = "messageSend", ignore = true)
    ClientUnique toUniqueClient(Client client);

    @Mapping(target = "phone", source = "client.phone")
    @Mapping(target = "message", expression = "java(buildMessage(client, discount))")
    Message clientToMessage(ClientUnique client, String discount);

    default String buildMessage(ClientUnique client, String discount) {
        String[] fullNameParts = client.getFullName().split(" ");
        return String.format("%s %s, в этом месяце для вас действует скидка %s", fullNameParts[0], fullNameParts[1], discount);
    }
}
