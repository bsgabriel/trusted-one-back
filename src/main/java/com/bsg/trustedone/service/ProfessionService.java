package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.ProfessionCreationDto;
import com.bsg.trustedone.dto.ProfessionDto;
import com.bsg.trustedone.entity.Profession;
import com.bsg.trustedone.exception.ResourceAlreadyExistsException;
import com.bsg.trustedone.exception.ResourceCreationException;
import com.bsg.trustedone.exception.ResourceNotFoundException;
import com.bsg.trustedone.exception.UnauthorizedAccessException;
import com.bsg.trustedone.factory.ProfessionFactory;
import com.bsg.trustedone.mapper.ProfessionMapper;
import com.bsg.trustedone.repository.ProfessionRepository;
import com.bsg.trustedone.validator.ProfessionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class ProfessionService {

    private final UserService userService;
    private final ProfessionMapper professionMapper;
    private final ProfessionFactory professionFactory;
    private final ProfessionValidator professionValidator;
    private final ProfessionRepository professionRepository;

    public List<ProfessionDto> findAllProfessions() {
        var loggedUser = userService.getLoggedUser();
        return professionRepository.findAllByUserId(loggedUser.getUserId())
                .stream()
                .map(professionMapper::toDto)
                .toList();
    }

    public ProfessionDto createProfession(ProfessionCreationDto profession) {
        professionValidator.validateProfessionCreate(profession);
        var entity = professionFactory.createEntity(profession, userService.getLoggedUser());

        return isNull(profession.getParentProfessionId())
                ? saveNewProfession(entity)
                : saveEspecialization(entity);
    }

    private ProfessionDto saveNewProfession(Profession profession) {
        if (professionRepository.existsByNameAndUserId(profession.getName(), profession.getUserId())) {
            throw new ResourceAlreadyExistsException("A profession with this name already exists. Please choose a different name.");
        }

        return professionMapper.toDto(professionRepository.save(profession));
    }

    private ProfessionDto saveEspecialization(Profession profession) {
        if (professionRepository.existsByNameAndParentProfessionId(profession.getName(), profession.getParentProfessionId())) {
            throw new ResourceAlreadyExistsException("A especialization for this profession already exists with this name. Please choose a different name.");
        }

        var parentProfession = professionRepository.findById(profession.getParentProfessionId())
                .orElseThrow(() -> new ResourceCreationException("Parent profession not found"));

        if (!parentProfession.getUserId().equals(profession.getUserId())) {
            throw new UnauthorizedAccessException("An error ocurred while creating specialization");
        }

        return professionMapper.toDto(professionRepository.save(profession));
    }

    public void deleteProfession(Long professionId) {
        List<Profession> specializations = professionRepository.findByParentProfessionId(professionId);
        for (Profession specialization : specializations) {
            deleteProfession(specialization.getProfessionId());
        }

        // TODO: quando tiver relação profissional_profession, chamar remoção aqui
        professionRepository.deleteById(professionId);
    }

    public ProfessionDto updateProfession(ProfessionCreationDto request, Long groupId) {
        // TODO: adicionar validador

        var profession = professionRepository.findById(groupId).orElseThrow(() -> new ResourceNotFoundException("Profession not found"));

        if (!profession.getUserId().equals(userService.getLoggedUser().getUserId())) {
            throw new UnauthorizedAccessException("An error occurred while updating profession");
        }

        profession.setName(request.getName());
        profession.setParentProfessionId(request.getParentProfessionId());
        return professionMapper.toDto(professionRepository.save(profession));
    }
}
