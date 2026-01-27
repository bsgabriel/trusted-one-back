package com.bsg.trustedone.controller;

import com.bsg.trustedone.dto.PageResponse;
import com.bsg.trustedone.dto.expertise.ExpertiseDto;
import com.bsg.trustedone.dto.expertise.ExpertiseListingDto;
import com.bsg.trustedone.dto.expertise.SpecializationDto;
import com.bsg.trustedone.dto.expertise.SpecializationListingDto;
import com.bsg.trustedone.dto.expertise.form.ExpertiseFormDto;
import com.bsg.trustedone.dto.expertise.form.SpecializationFormDto;
import com.bsg.trustedone.service.ExpertiseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/expertise")
@RequiredArgsConstructor
public class ExpertiseController {

    private final ExpertiseService expertiseService;

    @GetMapping
    public ResponseEntity<PageResponse<ExpertiseListingDto>> listExpertises(@RequestParam(required = false) String search, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(expertiseService.listExpertises(search, pageable));
    }

    @GetMapping("/{expertiseId}")
    public ResponseEntity<ExpertiseDto> fetchExpertise(@PathVariable Long expertiseId) {
        return ResponseEntity.ok(expertiseService.findExpertise(expertiseId));
    }

    @PostMapping
    public ResponseEntity<ExpertiseDto> createExpertise(@RequestBody @Valid ExpertiseFormDto request) {
        var created = expertiseService.createExpertise(request);
        var uri = URI.create(String.format("/expertise/%d", created.getExpertiseId()));
        return ResponseEntity.created(uri).body(created);
    }

    @PutMapping("/{expertiseId}")
    public ResponseEntity<ExpertiseDto> updateExpertise(@PathVariable Long expertiseId, @RequestBody @Valid ExpertiseFormDto request) {
        return ResponseEntity.ok(expertiseService.updateExpertise(expertiseId, request));
    }

    @DeleteMapping("/{expertiseId}")
    public ResponseEntity<Void> deleteExpertise(@PathVariable Long expertiseId) {
        expertiseService.deleteExpertise(expertiseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{expertiseId}/specialization")
    public ResponseEntity<List<SpecializationListingDto>> listSpecializations(@PathVariable Long expertiseId) {
        return ResponseEntity.ok(expertiseService.listSpecializations(expertiseId));
    }

    @GetMapping("/specialization/{specializationId}")
    public ResponseEntity<SpecializationDto> fetchSpecialization(@PathVariable Long specializationId) {
        return ResponseEntity.ok(expertiseService.findSpecialization(specializationId));
    }

    @PostMapping("/specialization")
    public ResponseEntity<SpecializationDto> createSpecialization(@RequestBody @Valid SpecializationFormDto request) {
        var created = expertiseService.createSpecialization(request);
        var uri = URI.create(String.format("/expertise/specialization/%d", created.getExpertiseId()));
        return ResponseEntity.created(uri).body(created);
    }

    @PutMapping("/specialization/{specializationId}")
    public ResponseEntity<SpecializationDto> updateSpecialization(@PathVariable Long specializationId, @RequestBody @Valid SpecializationFormDto request) {
        return ResponseEntity.ok(expertiseService.updateSpecialization(specializationId, request));
    }

    @DeleteMapping("/specialization/{specializationId}")
    public ResponseEntity<Void> deleteSpecialization(@PathVariable Long specializationId) {
        expertiseService.deleteExpertise(specializationId);
        return ResponseEntity.noContent().build();
    }

}
