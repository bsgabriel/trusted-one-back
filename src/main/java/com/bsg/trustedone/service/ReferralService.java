package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.ReferralCreationDto;
import com.bsg.trustedone.mapper.ReferralMapper;
import com.bsg.trustedone.repository.ReferralRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReferralService {

    private final UserService userService;
    private final ReferralMapper referralMapper;
    private final ReferralRepository referralRepository;

    public Long createReferral(ReferralCreationDto newReferral) {
        // TODO: validar

        var entity = referralMapper.toEntity(newReferral, userService.getLoggedUser());
        return referralRepository.save(entity).getReferralId();
    }
}
