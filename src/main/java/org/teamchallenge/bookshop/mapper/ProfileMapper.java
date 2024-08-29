package org.teamchallenge.bookshop.mapper;

import org.mapstruct.*;
import org.teamchallenge.bookshop.dto.ProfileUpdateDto;
import org.teamchallenge.bookshop.model.Profile;
import org.teamchallenge.bookshop.model.User;

@Mapper(config = MapperConfig.class)
public interface ProfileMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProfileFromDto(ProfileUpdateDto profileUpdateDto, @MappingTarget Profile profile);

    ProfileUpdateDto toProfileUpdateDto(Profile profile);

    @Mapping(source = "name", target = "firstName")
    @Mapping(source = "surname", target = "lastName")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    ProfileUpdateDto toProfileUpdateDto(User user);
}
