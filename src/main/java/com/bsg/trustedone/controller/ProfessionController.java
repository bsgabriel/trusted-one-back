package com.bsg.trustedone.controller;

import com.bsg.trustedone.dto.ProfessionCreationDto;
import com.bsg.trustedone.dto.ProfessionDto;
import com.bsg.trustedone.service.ProfessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/profession")
@RequiredArgsConstructor
public class ProfessionController {

    private final ProfessionService professionService;

    @GetMapping
    public ResponseEntity<List<ProfessionDto>> findAllProfessions() {
        return ok(professionService.findAllProfessions());
    }

    @PostMapping
    public ResponseEntity<ProfessionDto> createProfession(@RequestBody ProfessionCreationDto request) {
        return ok(professionService.createProfession(request));
    }

}
