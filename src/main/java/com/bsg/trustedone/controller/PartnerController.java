package com.bsg.trustedone.controller;

import com.bsg.trustedone.dto.*;
import com.bsg.trustedone.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/partner")
@RequiredArgsConstructor
public class PartnerController {

    private final PartnerService partnerService;

    @GetMapping
    public ResponseEntity<List<PartnerDto>> findAllPartners() {
        return ResponseEntity.ok(partnerService.findAllPartners());
    }

    @PostMapping
    public ResponseEntity<PartnerDto> createPartner(@RequestBody PartnerCreationDto request) {
        var createdPartner = partnerService.createPartner(null, request);
        var uri = URI.create(String.format("/partner/%d", createdPartner.getPartnerId()));
        return ResponseEntity.created(uri).body(createdPartner);
    }

    @PutMapping("/{partnerId}")
    public ResponseEntity<PartnerDto> updatePartner(@PathVariable Long partnerId, @RequestBody PartnerCreationDto request){
        var updatedPartner = partnerService.createPartner(partnerId, request);
        var uri = URI.create(String.format("/partner/%d", updatedPartner.getPartnerId()));
        return ResponseEntity.created(uri).body(updatedPartner);
    }

    @DeleteMapping("/{partnerId}")
    public ResponseEntity<Void> deletePartner(@PathVariable("partnerId") Long partnerId) {
        partnerService.deletePartner(partnerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/listing")
    public ResponseEntity<PageResponse<PartnerListingDto>> listAllPartners(@RequestParam(required = false) String search,
                                                                           @RequestParam(required = false, defaultValue = "true") boolean fullSearch,
                                                                           @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(partnerService.listPartners(search, pageable, fullSearch));
    }

    @GetMapping("/{partnerId}")
    public ResponseEntity<PartnerDto> findPartner(@PathVariable Long partnerId) {
        return ResponseEntity.ok(partnerService.findPartner(partnerId));
    }

    @GetMapping("/{partnerId}/recommendable-expertises")
    public ResponseEntity<List<ExpertiseDto>> findRecommendableExpertises(@PathVariable Long partnerId) {
        return ResponseEntity.ok(partnerService.findRecommendableExpertises(partnerId));
    }
}