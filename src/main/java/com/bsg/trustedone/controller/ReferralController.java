package com.bsg.trustedone.controller;

import com.bsg.trustedone.dto.ReferralCreationDto;
import com.bsg.trustedone.dto.ReferralDto;
import com.bsg.trustedone.service.ReferralService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/referral")
@RequiredArgsConstructor
public class ReferralController {

    private final ReferralService referralService;

    @PostMapping
    public ResponseEntity<Void> newReferral(@RequestBody ReferralCreationDto request) {
        var id = referralService.createReferral(request);
        return ResponseEntity.created(URI.create("/referral/" + id)).build();
    }

    // findAll - GET - não recebe nada. Na service, busca o ID do usuário logado
    // updateStatus - PUT - ID da referral por path, tipo por parâmetro
}
