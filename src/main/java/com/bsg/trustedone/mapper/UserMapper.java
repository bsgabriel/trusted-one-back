package com.bsg.trustedone.mapper;

import com.bsg.trustedone.dto.UserDetailDto;
import com.bsg.trustedone.dto.UserDto;
import com.bsg.trustedone.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public UserDetailDto toUserDetailDto(User user) {
        return UserDetailDto.builder()
                .id(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }

    public UserDto toUserDto(UserDetailDto user) {
        return UserDto.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }


}
