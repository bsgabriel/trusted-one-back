package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.*;
import com.bsg.trustedone.entity.Partner;
import com.bsg.trustedone.exception.UnauthorizedAccessException;
import com.bsg.trustedone.factory.PartnerFactory;
import com.bsg.trustedone.mapper.PartnerMapper;
import com.bsg.trustedone.repository.PartnerRepository;
import com.bsg.trustedone.validator.PartnerValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<PartnerDto> findAllPartners() {
        var loggedUser = userService.getLoggedUser();
        return partnerRepository.findAllByUserId(loggedUser.getUserId())
                .stream()
                .map(partnerMapper::toDto)
                .toList();
    }

    @Transactional
    public PartnerDto createPartner(PartnerCreationDto partnerCreationDto) {
        partnerValidator.validatePartnerCreation(partnerCreationDto);

        var loggedUser = userService.getLoggedUser();

        var group = groupService.findOrCreateGroup(partnerCreationDto.getGroup());
        var company = companyService.findOrCreateCompany(partnerCreationDto.getCompany());
        var expertises = partnerCreationDto.getExpertises()
                .stream()
                .map(expertiseService::findOrCreateExpertise)
                .peek(p -> p.setAvailableForReferrals(isAvailableForReferrals(p, partnerCreationDto.getExpertises())))
                .collect(Collectors.toList());

        var partner = partnerRepository.save(partnerFactory.createEntity(partnerCreationDto, group, company, loggedUser, partnerCreationDto.getContactMethods(), expertises, partnerCreationDto.getGainsProfile(), partnerCreationDto.getBusinessProfile()));

        return partnerMapper.toDto(partner);
    }

    public PageResponse<PartnerListingDto> listPartners(String search, Pageable pageable) {
        Specification<Partner> spec = (root, query, cb) -> {
            if (StringUtils.isBlank(search)) {
                return cb.conjunction();
            }

            var searchPattern = "%" + search.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("name")), searchPattern),
                    cb.like(cb.lower(root.get("company").get("name")), searchPattern),
                    cb.like(cb.lower(root.get("group").get("name")), searchPattern)
            );
        };

        var sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("name").ascending());
        var page = partnerRepository.findAll(spec, sortedPageable);

        return PageResponse.from(page.map(partnerMapper::toListingDto));
    }

    private boolean isAvailableForReferrals(ExpertiseDto expertise, List<ExpertiseDto> expertises) {
        return expertises.stream()
                .filter(p -> p.getName().equals(expertise.getName()))
                .findFirst()
                .map(ExpertiseDto::isAvailableForReferrals)
                .orElse(false);
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

}