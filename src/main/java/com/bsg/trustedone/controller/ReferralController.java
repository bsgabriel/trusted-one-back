package com.bsg.trustedone.controller;

import com.bsg.trustedone.dto.PageResponse;
import com.bsg.trustedone.dto.ReferralCreationDto;
import com.bsg.trustedone.dto.ReferralDto;
import com.bsg.trustedone.enums.ReferralSortType;
import com.bsg.trustedone.enums.ReferralStatus;
import com.bsg.trustedone.service.ReferralService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<PageResponse<ReferralDto>> findByFilter(@RequestParam(required = false) String search,
                                                                  @RequestParam(required = false) ReferralStatus status,
                                                                  @RequestParam(defaultValue = "RECENT") ReferralSortType sortBy,
                                                                  @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(referralService.findByFilter(search, status, sortBy, pageable));
    }

    @PutMapping("/{referralId}/status")
    public ResponseEntity<ReferralDto> updateStatus(@PathVariable("referralId") Long referralId, @RequestBody ReferralStatus status) {
        return ResponseEntity.ok(referralService.updateStatus(referralId, status));
    }

}
