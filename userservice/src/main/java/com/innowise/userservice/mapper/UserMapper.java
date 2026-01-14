package com.innowise.userservice.mapper;

import com.innowise.userservice.model.dto.UserDto;
import com.innowise.userservice.model.dto.UserWithCardsDto;
import com.innowise.userservice.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = PaymentCardMapper.class)
public interface UserMapper {

  UserDto userToUserDto(User user);

  @Mapping(target = "cards", source = "cards")
  UserWithCardsDto userToUserWithCardsDto(User user);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "cards", ignore = true)
  @Mapping(target = "active", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  User userDtoToUser(UserDto userDto);
}
