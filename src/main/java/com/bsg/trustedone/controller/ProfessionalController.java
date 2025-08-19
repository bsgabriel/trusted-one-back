package com.bsg.trustedone.controller;

import com.bsg.trustedone.dto.ProfessionalCreationDto;
import com.bsg.trustedone.dto.ProfessionalDto;
import com.bsg.trustedone.service.ProfessionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/professional")
@RequiredArgsConstructor
public class ProfessionalController {

    private final ProfessionalService professionalService;

    @GetMapping
    public ResponseEntity<List<ProfessionalDto>> findAllProfessionals() {
        return ResponseEntity.ok(professionalService.findAllProfessionals());
    }

    @PostMapping
    public ResponseEntity<ProfessionalDto> createProfessional(@RequestBody ProfessionalCreationDto request) {
        var createdProfessional = professionalService.createProfessional(request);
        var uri = URI.create(String.format("/professional/%d", createdProfessional.getProfessionalId()));
        return ResponseEntity.created(uri).body(createdProfessional);
    }

    @DeleteMapping("/{professionalId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable("professionalId") Long professionalId) {
        professionalService.deleteProfessional(professionalId);
        return ResponseEntity.noContent().build();
    }
}
