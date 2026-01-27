package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.ReferralCreationDto;
import com.bsg.trustedone.dto.ReferralDto;
import com.bsg.trustedone.dto.ReferralStatsDto;
import com.bsg.trustedone.dto.UserDto;
import com.bsg.trustedone.entity.Referral;
import com.bsg.trustedone.enums.ReferralStatus;
import com.bsg.trustedone.exception.ResourceNotFoundException;
import com.bsg.trustedone.exception.ResourceUpdateException;
import com.bsg.trustedone.helper.DummyObjects;
import com.bsg.trustedone.helper.RandomUtils;
import com.bsg.trustedone.mapper.ReferralMapper;
import com.bsg.trustedone.projection.ReferralStatsProjection;
import com.bsg.trustedone.repository.ReferralRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ReferralServiceTest {

    @InjectMocks
    private ReferralService referralService;

    @Mock
    private UserService userService;

    @Mock
    private MessageService messageService;

    @Mock
    private ReferralMapper referralMapper;

    @Mock
    private ReferralRepository referralRepository;

    @Test
    @DisplayName("Should create referral successfully")
    void createReferral_withValidData_shouldReturnReferralId() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var creationDto = DummyObjects.newInstance(ReferralCreationDto.class);
        var entity = DummyObjects.newInstance(Referral.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(referralMapper.toEntity(creationDto, loggedUser)).thenReturn(entity);
        when(referralRepository.save(entity)).thenReturn(entity);

        var result = referralService.createReferral(creationDto);

        assertThat(result).isEqualTo(entity.getReferralId());
        verify(referralRepository).save(entity);
    }

    @Test
    @DisplayName("Should throw exception when trying to update status to PENDING")
    void updateStatus_withPendingStatus_shouldThrowResourceUpdateException() {
        when(messageService.getMessage(anyString())).thenReturn("error");
        assertThatThrownBy(() -> referralService.updateStatus(1L, ReferralStatus.PENDING)).isInstanceOf(ResourceUpdateException.class);
    }

    @Test
    @DisplayName("Should throw not found exception when referral does not exist")
    void updateStatus_withInvalidReferralId_shouldThrowResourceNotFoundException() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(messageService.getMessage(anyString())).thenReturn("error");
        when(referralRepository.findByReferralIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> referralService.updateStatus(1L, ReferralStatus.ACCEPTED)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should update referral status successfully")
    void updateStatus_withValidData_shouldUpdateStatusAndReturnDto() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var referral = DummyObjects.newInstance(Referral.class);
        var referralDto = DummyObjects.newInstance(ReferralDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(referralRepository.findByReferralIdAndUserId(referral.getReferralId(), loggedUser.getUserId())).thenReturn(Optional.of(referral));
        when(referralMapper.toDto(referral)).thenReturn(referralDto);

        var result = referralService.updateStatus(referral.getReferralId(), ReferralStatus.ACCEPTED);

        assertThat(referral.getStatus()).isEqualTo(ReferralStatus.ACCEPTED);
        assertThat(referral.getUpdatedAt()).isNotNull();
        assertThat(result).isEqualTo(referralDto);
    }

    @Test
    @DisplayName("Should return referral statistics for logged user")
    void findReferralStats_withLoggedUser_shouldReturnStatsDto() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var statsProjection = new ReferralStatsProjection() {
            @Override
            public Long getTotal() {
                return 999L;
            }

            @Override
            public Long getAccepted() {
                return RandomUtils.nextLong(0, 10);
            }

            @Override
            public Long getDeclined() {
                return RandomUtils.nextLong(0, 10);
            }

            @Override
            public Long getPending() {
                return RandomUtils.nextLong(0, 10);
            }

            @Override
            public Long getCurrentMonthCreated() {
                return RandomUtils.nextLong(0, 10);
            }

            @Override
            public Long getCurrentMonthResponses() {
                return RandomUtils.nextLong(0, 10);
            }

            @Override
            public Long getCurrentMonthAccepted() {
                return RandomUtils.nextLong(0, 10);
            }

            @Override
            public Long getCurrentMonthDeclined() {
                return RandomUtils.nextLong(0, 10);
            }

            @Override
            public Long getPreviousMonthCreated() {
                return RandomUtils.nextLong(0, 10);
            }

            @Override
            public Long getPreviousMonthResponses() {
                return RandomUtils.nextLong(0, 10);
            }

            @Override
            public Long getPreviousMonthAccepted() {
                return RandomUtils.nextLong(0, 10);
            }

            @Override
            public Long getPreviousMonthDeclined() {
                return RandomUtils.nextLong(0, 10);
            }
        };
        var statsDto = DummyObjects.newInstance(ReferralStatsDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(referralRepository.getReferralStats(loggedUser.getUserId())).thenReturn(statsProjection);
        when(referralMapper.toReferralStats(statsProjection)).thenReturn(statsDto);

        var result = referralService.findReferralStats();
        assertThat(result).isEqualTo(statsDto);
    }
}
