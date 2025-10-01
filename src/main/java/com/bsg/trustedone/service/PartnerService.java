package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.*;
import com.bsg.trustedone.exception.UnauthorizedAccessException;
import com.bsg.trustedone.factory.PartnerFactory;
import com.bsg.trustedone.helper.DummyObjects;
import com.bsg.trustedone.helper.RandomUtils;
import com.bsg.trustedone.mapper.PartnerMapper;
import com.bsg.trustedone.repository.PartnerRepository;
import com.bsg.trustedone.validator.PartnerValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

        var partner = partnerRepository.save(partnerFactory.createEntity(partnerCreationDto, group, company, loggedUser, partnerCreationDto.getContactMethods(), expertises));

        return partnerMapper.toDto(partner);
    }

    public PageResponse<PartnerListingDto> listPartners(String search, Pageable pageable) {
        List<PartnerListingDto> parceiros = new ArrayList<>();

        var empresa1 = CompanyDto.builder()
                .name("Empresa ABC")
                .build();

        var empresa2 = CompanyDto.builder()
                .name("Empresa XYZ")
                .build();

        var grupo1 = GroupDto.builder()
                .name("Manos da Serra")
                .build();

        var grupo2 = GroupDto.builder()
                .name("BNI")
                .build();

        for (int i = 0; i < 103; i++) {
            var partner = DummyObjects.newInstance(PartnerListingDto.class);
            partner.setName("Parceiro " + (i + 1));

            int e = RandomUtils.nextInt(0, 2);
            if (e == 1) {
                partner.setCompany(empresa1);
            } else if (e == 2) {
                partner.setCompany(empresa2);
            }

            int g = RandomUtils.nextInt(0, 2);
            if (g == 1) {
                partner.setGroup(grupo1);
            } else if (g == 2) {
                partner.setGroup(grupo2);
            }

            if (RandomUtils.nextBoolean()) {
                partner.setMetrics(PartnerListingDto.PartnerMetricsDto.builder()
                        .pendingReferrals(RandomUtils.nextInt(0, 50))
                        .acceptedReferrals(RandomUtils.nextInt(0, 50))
                        .rejectedReferrals(RandomUtils.nextInt(0, 50))
                        .build());
            } else {
                partner.setMetrics(PartnerListingDto.PartnerMetricsDto.builder().build());
            }

            parceiros.add(partner);
        }

        List<PartnerListingDto> filtrados = parceiros.stream()
                .filter(p -> StringUtils.isBlank(search)
                        || p.getName().contains(search)
                        || (Objects.nonNull(p.getCompany()) && p.getCompany().getName().contains(search))
                        || (Objects.nonNull(p.getGroup()) && p.getGroup().getName().contains(search)))
                .toList();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtrados.size());
        List<PartnerListingDto> content = start < filtrados.size()
                ? filtrados.subList(start, end)
                : List.of();

        return PageResponse.from(new PageImpl<>(content, pageable, filtrados.size()));
    }

    private boolean isAvailableForReferrals(ExpertiseDto expertise, List<ExpertiseDto> expertises) {
        return expertises.stream()
                .filter(p -> p.getExpertiseId().equals(expertise.getExpertiseId()))
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