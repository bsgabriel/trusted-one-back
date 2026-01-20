package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.*;
import com.bsg.trustedone.entity.Partner;
import com.bsg.trustedone.exception.ResourceNotFoundException;
import com.bsg.trustedone.factory.PartnerFactory;
import com.bsg.trustedone.mapper.ExpertiseMapper;
import com.bsg.trustedone.mapper.PartnerMapper;
import com.bsg.trustedone.repository.PartnerExpertiseRepository;
import com.bsg.trustedone.repository.PartnerRepository;
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
    private final MessageService messageService;
    private final ExpertiseService expertiseService;
    private final PartnerMapper partnerMapper;
    private final PartnerFactory partnerFactory;
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
    public PartnerDto createPartner(Long partnerId, PartnerFormDto partnerFormDto) {
        var loggedUser = userService.getLoggedUser();

        var group = groupService.findOrCreateGroup(partnerFormDto.getGroup());
        var company = companyService.findOrCreateCompany(partnerFormDto.getCompany());
        var expertises = partnerFormDto.getExpertises()
                .stream()
                .map(originalExpertise -> {
                    var expertise = expertiseService.findOrCreateExpertise(originalExpertise);
                    expertise.setAvailableForReferral(originalExpertise.isAvailableForReferral());
                    return expertise;
                })
                .collect(Collectors.toList());

        var entity = partnerFactory.createEntity(partnerFormDto, group, company, loggedUser, partnerFormDto.getContactMethods(), expertises, partnerFormDto.getGainsProfile(), partnerFormDto.getBusinessProfile());
        entity.setPartnerId(partnerId);

        return partnerMapper.toDto(partnerRepository.save(entity));
    }

    public PageResponse<PartnerListingDto> listPartners(String search, Pageable pageable, boolean fullSearch) {
        var loggedUser = userService.getLoggedUser();
        Specification<Partner> spec = (root, query, cb) -> {
            var predicate = cb.and(
                    cb.equal(root.get("userId"), loggedUser.getUserId()),
                    cb.isTrue(root.get("active"))
            );

            if (StringUtils.isNotBlank(search)) {
                var searchPattern = "%" + search.toLowerCase() + "%";

                List<Predicate> searchPredicates = new ArrayList<>();
                searchPredicates.add(cb.like(cb.lower(root.get("name")), searchPattern));

                if (fullSearch) {
                    var companyJoin = root.join("company", JoinType.LEFT);
                    searchPredicates.add(cb.like(cb.lower(companyJoin.get("name")), searchPattern));

                    var groupJoin = root.join("group", JoinType.LEFT);
                    searchPredicates.add(cb.like(cb.lower(groupJoin.get("name")), searchPattern));
                }

                var searchPredicate = cb.or(searchPredicates.toArray(new Predicate[0]));
                predicate = cb.and(predicate, searchPredicate);
            }

            return predicate;
        };

        var sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("name").ascending());
        var page = partnerRepository.findAll(spec, sortedPageable);

        return PageResponse.from(page.map(partnerMapper::toListingDto));
    }

    @Transactional
    public void deletePartner(Long partnerId) {
        partnerRepository.deactivate(partnerId, userService.getLoggedUser().getUserId());
    }

    public PartnerDto findPartner(Long partnerId) {
        var partner = partnerRepository.findByPartnerIdAndUserId(partnerId, userService.getLoggedUser().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(messageService.getMessage("partner.error.not-found")));

        if (!partner.isActive()) {
            throw new ResourceNotFoundException(messageService.getMessage("partner.error.not-found"));
        }

        partner.getPartnerExpertises().removeIf(partnerExpertise -> {
            var expertise = partnerExpertise.getExpertise();

            if (!expertise.isActive()) {
                return true;
            }

            return expertise.getParentExpertise() != null && !expertise.getParentExpertise().isActive();
        });

        return partnerMapper.toDto(partner);
    }

    public List<AssignedExpertiseDto> findRecommendableExpertises(Long partnerId) {
        return partnerExpertiseRepository.findRecommendableExpertisesForPartner(partnerId)
                .stream()
                .map(expertiseMapper::toDto)
                .toList();
    }

    public void removePartnersFromGroup(Long groupId) {
        var loggedUserId = userService.getLoggedUser().getUserId();
        this.partnerRepository.removePartnersFromGroup(groupId, loggedUserId);
    }

    public void addPartnersToGroup(List<Long> partnerIds, Long groupId) {
        partnerRepository.addPartnersToGroup(partnerIds, groupId);
    }

    public void removePartnersFromCompany(Long companyId) {
        var loggerUserId = userService.getLoggedUser().getUserId();
        this.partnerRepository.removePartnersFromCompany(companyId, loggerUserId);
    }

    public void addPartnersToCompany(List<Long> partnerIds, Long companyId) {
        this.partnerRepository.addPartnersToCompany(partnerIds, companyId);
    }

}