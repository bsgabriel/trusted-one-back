package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.ExpertiseCreationDto;
import com.bsg.trustedone.dto.ExpertiseDto;
import com.bsg.trustedone.dto.ExpertiseListingDto;
import com.bsg.trustedone.entity.Expertise;
import com.bsg.trustedone.exception.ResourceAlreadyExistsException;
import com.bsg.trustedone.exception.ResourceCreationException;
import com.bsg.trustedone.exception.ResourceNotFoundException;
import com.bsg.trustedone.exception.UnauthorizedAccessException;
import com.bsg.trustedone.factory.ExpertiseFactory;
import com.bsg.trustedone.mapper.ExpertiseMapper;
import com.bsg.trustedone.repository.ExpertiseRepository;
import com.bsg.trustedone.validator.ExpertiseValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class ExpertiseService {

    private final UserService userService;
    private final ExpertiseMapper expertiseMapper;
    private final ExpertiseFactory expertiseFactory;
    private final ExpertiseValidator expertiseValidator;
    private final ExpertiseRepository expertiseRepository;

    public List<ExpertiseDto> findAllExpertises() {
        var loggedUser = userService.getLoggedUser();
        return expertiseRepository.findAllByUserId(loggedUser.getUserId())
                .stream()
                .map(expertiseMapper::toDto)
                .toList();
    }

    public ExpertiseDto createExpertise(ExpertiseCreationDto expertise) {
        expertiseValidator.validateExpertiseCreate(expertise);
        var entity = expertiseFactory.createEntity(expertise, userService.getLoggedUser());

        return isNull(expertise.getParentExpertiseId())
                ? saveNewExpertise(entity)
                : saveEspecialization(entity);
    }

    public List<ExpertiseListingDto> findParents() {
        var userId = userService.getLoggedUser().getUserId();
        return expertiseRepository.findByUserIdAndParentExpertiseIdOrderByName(userId, null)
                .stream()
                .map(expertiseMapper::toListingDto)
                .toList();
    }

    public List<ExpertiseListingDto> findChildren(Long parentId) {
        var userId = userService.getLoggedUser().getUserId();
        return expertiseRepository.findByUserIdAndParentExpertiseIdOrderByName(userId, parentId)
                .stream()
                .map(expertiseMapper::toListingDto)
                .toList();
    }

    private ExpertiseDto saveNewExpertise(Expertise expertise) {
        if (expertiseRepository.existsByNameAndUserId(expertise.getName(), expertise.getUserId())) {
            throw new ResourceAlreadyExistsException("A expertise with this name already exists. Please choose a different name.");
        }

        return expertiseMapper.toDto(expertiseRepository.save(expertise));
    }

    private ExpertiseDto saveEspecialization(Expertise expertise) {
        if (expertiseRepository.existsByNameAndParentExpertiseId(expertise.getName(), expertise.getParentExpertiseId())) {
            throw new ResourceAlreadyExistsException("A especialization for this expertise already exists with this name. Please choose a different name.");
        }

        var parentExpertise = expertiseRepository.findById(expertise.getParentExpertiseId())
                .orElseThrow(() -> new ResourceCreationException("Parent expertise not found"));

        if (!parentExpertise.getUserId().equals(expertise.getUserId())) {
            throw new UnauthorizedAccessException("An error ocurred while creating specialization");
        }

        return expertiseMapper.toDto(expertiseRepository.save(expertise));
    }

    public void deleteExpertise(Long expertiseId) {
        List<Expertise> specializations = expertiseRepository.findByParentExpertiseId(expertiseId);
        for (Expertise specialization : specializations) {
            deleteExpertise(specialization.getExpertiseId());
        }

        expertiseRepository.deleteById(expertiseId);
    }

    public ExpertiseDto updateExpertise(ExpertiseCreationDto request, Long expertiseId) {
        expertiseValidator.validateExpertiseUpdate(request);

        var expertise = expertiseRepository.findById(expertiseId).orElseThrow(() -> new ResourceNotFoundException("Expertise not found"));

        if (!expertise.getUserId().equals(userService.getLoggedUser().getUserId())) {
            throw new UnauthorizedAccessException("An error occurred while updating expertise");
        }

        expertise.setName(request.getName());
        expertise.setParentExpertiseId(request.getParentExpertiseId());
        return expertiseMapper.toDto(expertiseRepository.save(expertise));
    }

    public ExpertiseDto findOrCreateExpertise(ExpertiseDto expertise) {
        if (isNull(expertise.getParentExpertiseId()) && !isNull(expertise.getParentExpertiseName())) {
            var parentExpertise = findOrCreateByName(expertise.getParentExpertiseName());
            expertise.setParentExpertiseId(parentExpertise.getExpertiseId());
        }

        if (isNull(expertise.getExpertiseId())) {
            return this.createExpertise(expertiseMapper.toCreationDto(expertise));
        }

        return this.expertiseRepository.findById(expertise.getExpertiseId())
                .map(expertiseMapper::toDto)
                .orElseGet(() -> this.createExpertise(expertiseMapper.toCreationDto(expertise)));
    }

    private ExpertiseDto findOrCreateByName(String name) {
        var loggedUser = userService.getLoggedUser();

        return expertiseRepository.findByNameAndUserId(name, loggedUser.getUserId())
                .map(expertiseMapper::toDto)
                .orElseGet(() -> {
                    var creationDto = ExpertiseCreationDto.builder()
                            .name(name)
                            .parentExpertiseId(null)
                            .build();
                    return createExpertise(creationDto);
                });
    }
}
