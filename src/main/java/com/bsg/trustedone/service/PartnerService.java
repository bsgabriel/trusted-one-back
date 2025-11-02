package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.*;
import com.bsg.trustedone.entity.Partner;
import com.bsg.trustedone.exception.ResourceNotFoundException;
import com.bsg.trustedone.exception.UnauthorizedAccessException;
import com.bsg.trustedone.factory.PartnerFactory;
import com.bsg.trustedone.mapper.ExpertiseMapper;
import com.bsg.trustedone.mapper.PartnerMapper;
import com.bsg.trustedone.repository.PartnerExpertiseRepository;
import com.bsg.trustedone.repository.PartnerRepository;
import com.bsg.trustedone.validator.PartnerValidator;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartnerService {

    private final UserService userService;
    private final GroupService groupService;
    private final CompanyService companyService;
    private final ExpertiseService expertiseService;
    private final PartnerMapper partnerMapper;
    private final PartnerFactory partnerFactory;
    private final PartnerValidator partnerValidator;
    private final PartnerRepository partnerRepository;
    private final ExpertiseMapper expertiseMapper;
    private final PartnerExpertiseRepository partnerExpertiseRepository;

    public List<PartnerDto> findAllPartners() {
        var loggedUser = userService.getLoggedUser();
        return partnerRepository.findAllByUserId(loggedUser.getUserId())
                .stream()
                .map(partnerMapper::toDto)
                .toList();
    }

    @Transactional
    public PartnerDto createPartner(Long partnerId, PartnerCreationDto partnerCreationDto) {
        partnerValidator.validatePartnerCreation(partnerCreationDto);

        var loggedUser = userService.getLoggedUser();

        var group = groupService.findOrCreateGroup(partnerCreationDto.getGroup());
        var company = companyService.findOrCreateCompany(partnerCreationDto.getCompany());
        var expertises = partnerCreationDto.getExpertises()
                .stream()
                .map(originalExpertise -> {
                    var expertise = expertiseService.findOrCreateExpertise(originalExpertise);
                    expertise.setAvailableForReferral(originalExpertise.isAvailableForReferral());
                    return expertise;
                })
                .collect(Collectors.toList());

        var entity = partnerFactory.createEntity(partnerCreationDto, group, company, loggedUser, partnerCreationDto.getContactMethods(), expertises, partnerCreationDto.getGainsProfile(), partnerCreationDto.getBusinessProfile());
        entity.setPartnerId(partnerId);

        return partnerMapper.toDto(partnerRepository.save(entity));
    }

    public PageResponse<PartnerListingDto> listPartners(String search, Pageable pageable) {
        var loggedUser = userService.getLoggedUser();
        Specification<Partner> spec = (root, query, cb) -> {
            var predicate = cb.equal(root.get("userId"), loggedUser.getUserId());

            if (StringUtils.isNotBlank(search)) {
                var searchPattern = "%" + search.toLowerCase() + "%";

                List<Predicate> searchPredicates = new ArrayList<>();
                searchPredicates.add(cb.like(cb.lower(root.get("name")), searchPattern));

                var companyJoin = root.join("company", JoinType.LEFT);
                searchPredicates.add(cb.like(cb.lower(companyJoin.get("name")), searchPattern));

                var groupJoin = root.join("group", JoinType.LEFT);
                searchPredicates.add(cb.like(cb.lower(groupJoin.get("name")), searchPattern));

                var searchPredicate = cb.or(searchPredicates.toArray(new Predicate[0]));
                predicate = cb.and(predicate, searchPredicate);
            }

            return predicate;
        };

        var sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("name").ascending());
        var page = partnerRepository.findAll(spec, sortedPageable);

        return PageResponse.from(page.map(partnerMapper::toListingDto));
    }

    public void deletePartner(Long partnerId) {
        var opt = partnerRepository.findById(partnerId);

        if (opt.isEmpty()) {
            return;
        }

        var loggedUser = userService.getLoggedUser();
        var partner = opt.get();

        if (!partner.getUserId().equals(loggedUser.getUserId())) {
            throw new UnauthorizedAccessException("An error occurred while deleting partner");
        }

        partnerRepository.deleteById(partnerId);
    }

    public PartnerDto findPartner(Long partnerId) {
        var partner = partnerRepository.findById(partnerId).orElseThrow(() -> new ResourceNotFoundException("Partner not found"));

        var loggedUser = userService.getLoggedUser();
        if (!partner.getUserId().equals(loggedUser.getUserId())) {
            throw new UnauthorizedAccessException("An error occurred while searching partner");
        }

        return partnerMapper.toDto(partner);
    }

    public List<ExpertiseDto> findRecommendableExpertises(Long partnerId) {
        return partnerExpertiseRepository.findRecommendableExpertisesForPartner(partnerId)
                .stream()
                .map(expertiseMapper::toDto)
                .toList();
    }

}