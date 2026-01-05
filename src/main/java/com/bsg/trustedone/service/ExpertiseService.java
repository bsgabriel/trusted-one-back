package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.AssignedExpertiseDto;
import com.bsg.trustedone.dto.PageResponse;
import com.bsg.trustedone.dto.expertise.ExpertiseDto;
import com.bsg.trustedone.dto.expertise.ExpertiseListingDto;
import com.bsg.trustedone.dto.expertise.SpecializationDto;
import com.bsg.trustedone.dto.expertise.SpecializationListingDto;
import com.bsg.trustedone.dto.expertise.form.ExpertiseFormDto;
import com.bsg.trustedone.dto.expertise.form.SpecializationFormDto;
import com.bsg.trustedone.entity.Expertise;
import com.bsg.trustedone.exception.ResourceAlreadyExistsException;
import com.bsg.trustedone.exception.ResourceCreationException;
import com.bsg.trustedone.exception.ResourceNotFoundException;
import com.bsg.trustedone.exception.UnauthorizedAccessException;
import com.bsg.trustedone.mapper.ExpertiseMapper;
import com.bsg.trustedone.repository.ExpertiseRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class ExpertiseService {

    private final UserService userService;
    private final ExpertiseMapper expertiseMapper;
    private final ExpertiseRepository expertiseRepository;

    public PageResponse<ExpertiseListingDto> listExpertises(String search, Pageable pageable) {
        var loggedUser = userService.getLoggedUser();
        Specification<Expertise> spec = (root, query, cb) -> {
            var predicate = cb.and(
                    cb.equal(root.get("userId"), loggedUser.getUserId()),
                    root.get("parentExpertise").isNull(),
                    cb.isTrue(root.get("active"))
            );

            if (StringUtils.isNotBlank(search)) {
                var searchPattern = "%" + search.toLowerCase() + "%";

                List<Predicate> searchPredicates = new ArrayList<>();
                searchPredicates.add(cb.like(cb.lower(root.get("name")), searchPattern));

                var searchPredicate = cb.or(searchPredicates.toArray(new Predicate[0]));
                predicate = cb.and(predicate, searchPredicate);
            }

            return predicate;
        };

        var sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("name").ascending());
        var page = expertiseRepository.findAll(spec, sortedPageable);

        return PageResponse.from(page.map(expertiseMapper::entityToListingDto));
    }

    public ExpertiseDto findExpertise(Long expertiseId) {
        return expertiseRepository.findById(expertiseId)
                .map(expertiseMapper::entityToExpertiseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Expertise not found"));
    }

    public ExpertiseDto createExpertise(ExpertiseFormDto expertise) {
        var user = userService.getLoggedUser();

        if (expertiseRepository.existsByNameAndUserId(expertise.getName(), user.getUserId())) {
            throw new ResourceAlreadyExistsException("A expertise with this name already exists. Please choose a different name.");
        }

        var entity = expertiseMapper.expertiseFormToEntity(expertise, user);
        return expertiseMapper.entityToExpertiseDto(expertiseRepository.save(entity));
    }

    public ExpertiseDto updateExpertise(Long expertiseId, ExpertiseFormDto request) {
        var expertise = expertiseRepository.findById(expertiseId).orElseThrow(() -> new ResourceNotFoundException("Expertise not found"));

        var user = userService.getLoggedUser();
        if (!expertise.getUserId().equals(user.getUserId())) {
            throw new UnauthorizedAccessException("An error occurred while updating expertise");
        }

        expertise.setName(request.getName());
        expertise.getSpecializations().clear();
        expertise.getSpecializations()
                .addAll(request.getSpecializations()
                        .stream()
                        .map(form -> expertiseMapper.specializationFormToEntity(form, user))
                        .collect(Collectors.toCollection(ArrayList::new)));
        return expertiseMapper.entityToExpertiseDto(expertiseRepository.save(expertise));
    }

    public void deleteExpertise(Long expertiseId) {
        List<Expertise> specializations = expertiseRepository.findByParentExpertiseExpertiseId(expertiseId);
        for (Expertise specialization : specializations) {
            deleteExpertise(specialization.getExpertiseId());
        }

        expertiseRepository.deleteById(expertiseId);
    }

    public List<SpecializationListingDto> listSpecializations(Long expertiseId) {
        var user = userService.getLoggedUser();
        return expertiseRepository.listSpecializations(expertiseId, user.getUserId())
                .stream()
                .map(expertiseMapper::specializationListingProjectionToDto)
                .toList();
    }

    public SpecializationDto findSpecialization(Long specializationId) {
        return expertiseRepository.findById(specializationId)
                .map(expertiseMapper::entityToSpecialization)
                .orElseThrow(() -> new ResourceNotFoundException("Specialization not found"));
    }

    public SpecializationDto createSpecialization(SpecializationFormDto specialization) {
        var user = userService.getLoggedUser();

        var parentExpertise = expertiseRepository.findById(specialization.getParentExpertiseId())
                .orElseThrow(() -> new ResourceNotFoundException("Expertise not found."));

        if (parentExpertise.getParentExpertise() != null) {
            throw new ResourceCreationException("Cannot create specialization under another specialization.");
        }

        if (parentExpertise.getSpecializations().stream().anyMatch(s -> s.getName().equalsIgnoreCase(specialization.getName()))) {
            throw new ResourceAlreadyExistsException("A specialization for this expertise already exists with this name. Please choose a different name.");
        }

        var entity = expertiseMapper.specializationFormToEntity(specialization, user);
        return expertiseMapper.entityToSpecialization(expertiseRepository.save(entity));
    }

    public SpecializationDto updateSpecialization(Long specializationId, SpecializationFormDto request) {
        var specialization = expertiseRepository.findById(specializationId).orElseThrow(() -> new ResourceNotFoundException("Specialization not found"));

        if (!specialization.getUserId().equals(userService.getLoggedUser().getUserId())) {
            throw new UnauthorizedAccessException("An error occurred while updating specialization");
        }

        specialization.setName(request.getName());
        specialization.getPartnerExpertises().clear();
        specialization.getPartnerExpertises().addAll(request.getPartners()
                .stream()
                .map(partner -> expertiseMapper.partnerExpertiseFormToEntity(partner, specialization))
                .collect(Collectors.toCollection(ArrayList::new)));

        return expertiseMapper.entityToSpecialization(expertiseRepository.save(specialization));
    }

    public AssignedExpertiseDto findOrCreateExpertise(AssignedExpertiseDto expertise) {
        var user = userService.getLoggedUser();
        if (isNull(expertise.getParentExpertiseId()) && !isNull(expertise.getParentExpertiseName())) {
            var parentExpertise = expertiseRepository.findByNameAndUserId(expertise.getParentExpertiseName(), user.getUserId())
                    .orElseGet(() -> expertiseRepository.save(Expertise.builder()
                            .name(expertise.getParentExpertiseName())
                            .userId(user.getUserId())
                            .build()));
            expertise.setParentExpertiseId(parentExpertise.getExpertiseId());
        }

        if (isNull(expertise.getExpertiseId())) {
            var specialization = this.createSpecialization(expertiseMapper.toCreationDto(expertise));
            return AssignedExpertiseDto.builder()
                    .parentExpertiseName(specialization.getParentExpertiseName())
                    .parentExpertiseId(specialization.getParentExpertiseId())
                    .expertiseId(specialization.getExpertiseId())
                    .name(specialization.getName())
                    .build();
        }

        return this.expertiseRepository.findById(expertise.getExpertiseId())
                .map(specialization -> AssignedExpertiseDto.builder()
                        .parentExpertiseName(specialization.getParentExpertise().getName())
                        .parentExpertiseId(specialization.getParentExpertise().getExpertiseId())
                        .expertiseId(specialization.getExpertiseId())
                        .name(specialization.getName())
                        .build())
                .orElseGet(() -> {
                    var specialization = this.createSpecialization(expertiseMapper.toCreationDto(expertise));
                    return AssignedExpertiseDto.builder()
                            .parentExpertiseName(specialization.getParentExpertiseName())
                            .parentExpertiseId(specialization.getParentExpertiseId())
                            .expertiseId(specialization.getExpertiseId())
                            .name(specialization.getName())
                            .build();
                });
    }
}
