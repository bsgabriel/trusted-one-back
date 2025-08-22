package com.bsg.trustedone.controller;

import com.bsg.trustedone.dto.ExpertiseCreationDto;
import com.bsg.trustedone.dto.ExpertiseDto;
import com.bsg.trustedone.service.ExpertiseService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<ExpertiseDto>> findAllExpertises() {
        return ok(expertiseService.findAllExpertises());
    }

    @PostMapping
    public ResponseEntity<ExpertiseDto> createExpertise(@RequestBody ExpertiseCreationDto request) {
        var createdExpertise = expertiseService.createExpertise(request);
        var uri = URI.create(String.format("/expertise/%d", createdExpertise.getExpertiseId()));
        return ResponseEntity.created(uri).body(createdExpertise);
    }


    @DeleteMapping("/{expertiseId}")
    public ResponseEntity<Void> deleteExpertise(@PathVariable("expertiseId") Long expertiseId) {
        expertiseService.deleteExpertise(expertiseId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{expertiseId}")
    public ResponseEntity<ExpertiseDto> update(@PathVariable("expertiseId") Long expertiseId, @RequestBody ExpertiseCreationDto expertiseCreationDto) {
        return ResponseEntity.ok(expertiseService.updateExpertise(expertiseCreationDto, expertiseId));
    }

}
