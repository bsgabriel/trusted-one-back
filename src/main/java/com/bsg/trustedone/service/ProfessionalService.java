package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.ProfessionDto;
import com.bsg.trustedone.dto.ProfessionalCreationDto;
import com.bsg.trustedone.dto.ProfessionalDto;
import com.bsg.trustedone.exception.UnauthorizedAccessException;
import com.bsg.trustedone.factory.ProfessionalFactory;
import com.bsg.trustedone.mapper.ProfessionalMapper;
import com.bsg.trustedone.repository.ProfessionalRepository;
import com.bsg.trustedone.validator.ProfessionalValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfessionalService {

    private final UserService userService;
    private final GroupService groupService;
    private final CompanyService companyService;
    private final ProfessionService professionService;
    private final ProfessionalMapper professionalMapper;
    private final ProfessionalFactory professionalFactory;
    private final ProfessionalValidator professionalValidator;
    private final ProfessionalRepository professionalRepository;

    public List<ProfessionalDto> findAllProfessionals() {
        var loggedUser = userService.getLoggedUser();
        return professionalRepository.findAllByUserId(loggedUser.getUserId())
                .stream()
                .map(professionalMapper::toDto)
                .toList();
    }

    @Transactional
    public ProfessionalDto createProfessional(ProfessionalCreationDto professionalCreationDto) {
        professionalValidator.validateProfessionalCreation(professionalCreationDto);

        var loggedUser = userService.getLoggedUser();

        var group = groupService.findOrCreateGroup(professionalCreationDto.getGroup());
        var company = companyService.findOrCreateCompany(professionalCreationDto.getCompany());
        var professions = professionalCreationDto.getProfessions()
                .stream()
                .map(professionService::findOrCreateProfession)
                .peek(p -> p.setAvailableForReferrals(isAvailableForReferrals(p, professionalCreationDto.getProfessions())))
                .collect(Collectors.toList());

        var professional = professionalRepository.save(professionalFactory.createEntity(professionalCreationDto, group, company, loggedUser, professionalCreationDto.getContactMethods(), professions));

        return professionalMapper.toDto(professional);
    }

    private boolean isAvailableForReferrals(ProfessionDto profession, List<ProfessionDto> professions) {
        return professions.stream()
                .filter(p -> p.getProfessionId().equals(profession.getProfessionId()))
                .findFirst()
                .map(ProfessionDto::isAvailableForReferrals)
                .orElse(false);
    }

    public void deleteProfessional(Long professionalId) {
        var opt = professionalRepository.findById(professionalId);

        if (opt.isEmpty()) {
            return;
        }

        var loggedUser = userService.getLoggedUser();
        var professional = opt.get();

        if (!professional.getUserId().equals(loggedUser.getUserId())) {
            throw new UnauthorizedAccessException("An error occurred while deleting professional");
        }

        professionalRepository.deleteById(professionalId);
    }

}
