package org.teamchallenge.bookshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.teamchallenge.bookshop.dto.ProfileUpdateDto;
import org.teamchallenge.bookshop.dto.UserDto;
import org.teamchallenge.bookshop.dto.UserTestDto;
import org.teamchallenge.bookshop.model.User;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    UserDto entityToDto(User user);

    User dtoToEntity(UserDto userDto);

    @Mapping(source = "firstName", target = "name")
    @Mapping(source = "lastName", target = "surname")
    void updateUserFromProfileDto(ProfileUpdateDto profileUpdateDto, @MappingTarget User user);

    User toEntity(UserTestDto userTestDto);
    UserTestDto toDto(User user);
    @Mapping(target = "id", ignore = true)
    void updateUserFromDto(UserDto userDto, @MappingTarget User user);
}