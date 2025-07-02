package com.velazco.velazco_backend.services;

import com.velazco.velazco_backend.dto.profile.response.ProfileDto;
import com.velazco.velazco_backend.entities.User;

public interface ProfileService {

    ProfileDto getProfile(User user);
}
