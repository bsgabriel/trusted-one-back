package com.bsg.trustedone.controller;

import com.bsg.trustedone.dto.CompanyDto;
import com.bsg.trustedone.dto.CompanyFormDto;
import com.bsg.trustedone.dto.CompanyListingDto;
import com.bsg.trustedone.dto.PageResponse;
import com.bsg.trustedone.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    public ResponseEntity<PageResponse<CompanyListingDto>> listCompanies(@RequestParam(required = false) String search, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(companyService.listCompanies(search, pageable));
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyDto> fetchCompany(@PathVariable("companyId") Long companyId) {
        return ResponseEntity.ok(companyService.findById(companyId));
    }

    @PostMapping
    public ResponseEntity<CompanyDto> createCompany(@RequestBody @Valid CompanyFormDto request) {
        var createdCompany = companyService.createCompany(request);
        var uri = URI.create(String.format("/company/%d", createdCompany.getCompanyId()));
        return ResponseEntity.created(uri).body(createdCompany);
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyDto> updateCompany(@PathVariable("companyId") Long companyId, @RequestBody @Valid CompanyFormDto companyFormDto) {
        return ResponseEntity.ok(companyService.updateCompany(companyFormDto, companyId));
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<Void> deleteCompany(@PathVariable("companyId") Long companyId) {
        companyService.deleteCompany(companyId);
        return ResponseEntity.noContent().build();
    }

}
