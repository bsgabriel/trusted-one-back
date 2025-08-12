package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.ProfessionalCreationDto;
import com.bsg.trustedone.dto.ProfessionalDto;
import com.bsg.trustedone.exception.UnauthorizedAccessException;
import com.bsg.trustedone.factory.ProfessionalFactory;
import com.bsg.trustedone.mapper.ProfessionalMapper;
import com.bsg.trustedone.repository.ProfessionalRepository;
import com.bsg.trustedone.validator.ContactMethodValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfessionalService {

    private final UserService userService;
    private final GroupService groupService;
    private final CompanyService companyService;
    private final ProfessionalMapper professionalMapper;
    private final ProfessionalFactory professionalFactory;
    private final ProfessionalRepository professionalRepository;
    private final ContactMethodValidator contactMethodValidator;

    public List<ProfessionalDto> findAllProfessionals() {
        var loggedUser = userService.getLoggedUser();
        return professionalRepository.findAllByUserId(loggedUser.getUserId())
                .stream()
                .map(professionalMapper::toDto)
                .toList();
    }

    public ProfessionalDto createProfessional(ProfessionalCreationDto professionalCreationDto) {
        // TODO: criar validador
        var loggedUser = userService.getLoggedUser();

        var group = groupService.findOrCreateGroup(professionalCreationDto.getGroup());
        var company = companyService.findOrCreateCompany(professionalCreationDto.getCompany());

        var professional = professionalRepository.save(professionalFactory.createEntity(professionalCreationDto, group, company, loggedUser, professionalCreationDto.getContactMethods()));

        // TODO: criar relação ProfessionalProfession e salvar
        return professionalMapper.toDto(professional);
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
