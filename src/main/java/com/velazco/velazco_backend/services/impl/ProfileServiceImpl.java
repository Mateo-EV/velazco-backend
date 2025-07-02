package com.velazco.velazco_backend.services.impl;

import org.springframework.stereotype.Service;

import com.velazco.velazco_backend.dto.profile.response.ProfileDto;
import com.velazco.velazco_backend.entities.User;
import com.velazco.velazco_backend.mappers.UserMapper;
import com.velazco.velazco_backend.services.ProfileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserMapper userMapper;

    @Override
    public ProfileDto getProfile(User user) {
        return userMapper.toProfileDto(user);
    }
}
