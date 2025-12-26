package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.AssignedExpertiseDto;
import com.bsg.trustedone.dto.ExpertiseCreationDto;
import com.bsg.trustedone.dto.UserDto;
import com.bsg.trustedone.dto.expertise.form.ExpertiseFormDto;
import com.bsg.trustedone.entity.Expertise;
import com.bsg.trustedone.factory.ExpertiseFactory;
import com.bsg.trustedone.helper.DummyObjects;
import com.bsg.trustedone.helper.RandomUtils;
import com.bsg.trustedone.mapper.ExpertiseMapper;
import com.bsg.trustedone.repository.ExpertiseRepository;
import com.bsg.trustedone.validator.ExpertiseValidator;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class ExpertiseServiceTest {

    @InjectMocks
    private ExpertiseService expertiseService;

    @Mock
    private ExpertiseMapper expertiseMapper;

    @Mock
    private ExpertiseFactory expertiseFactory;

    @Mock
    private ExpertiseRepository expertiseRepository;

    @Mock
    private ExpertiseValidator expertiseValidator;

    @Mock
    private Validator validator;

    @Mock
    private UserService userService;

    private UserDto loggedUser;

    @BeforeEach
    public void beforeAll() {
        lenient().when(expertiseMapper.toDto(any(Expertise.class))).thenCallRealMethod();
        lenient().when(expertiseMapper.toCreationDto(any(AssignedExpertiseDto.class))).thenCallRealMethod();
        lenient().when(expertiseFactory.createEntity(any(ExpertiseFormDto.class), any(UserDto.class))).thenCallRealMethod();
        lenient().when(expertiseRepository.save(any(Expertise.class))).then(invocation -> {
            var created = (Expertise) invocation.getArguments()[0];
            created.setExpertiseId(RandomUtils.nextLong(1, 999));
            return created;
        });

        this.loggedUser = DummyObjects.newInstance(UserDto.class);
        lenient().when(userService.getLoggedUser()).thenReturn(loggedUser);
    }

}