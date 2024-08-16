package org.teamchallenge.bookshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.teamchallenge.bookshop.dto.ProfileUpdateDto;
import org.teamchallenge.bookshop.model.Profile;

@Mapper(config = MapperConfig.class)
public interface ProfileMapper {

    void updateProfileFromDto(ProfileUpdateDto profileUpdateDto, @MappingTarget Profile profile);
}
