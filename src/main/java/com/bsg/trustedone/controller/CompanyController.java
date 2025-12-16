package com.bsg.trustedone.controller;

import com.bsg.trustedone.dto.CompanyFormDto;
import com.bsg.trustedone.dto.CompanyDto;
import com.bsg.trustedone.dto.CompanyListingDto;
import com.bsg.trustedone.dto.PageResponse;
import com.bsg.trustedone.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    public ResponseEntity<List<CompanyDto>> findAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @PostMapping
    public ResponseEntity<CompanyDto> createCompany(@RequestBody CompanyFormDto request) {
        var createdCompany = companyService.createCompany(request);
        var uri = URI.create(String.format("/company/%d", createdCompany.getCompanyId()));
        return ResponseEntity.created(uri).body(createdCompany);
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyDto> findCompany(@PathVariable("companyId") Long companyId) {
        return ResponseEntity.ok(companyService.findById(companyId));
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<Void> deleteCompany(@PathVariable("companyId") Long companyId) {
        companyService.deleteCompany(companyId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyDto> update(@PathVariable("companyId") Long companyId, @RequestBody CompanyFormDto companyFormDto) {
        return ResponseEntity.ok(companyService.updateCompany(companyFormDto, companyId));
    }

    @GetMapping("/listing")
    public ResponseEntity<PageResponse<CompanyListingDto>> listCompanies(@RequestParam(required = false) String search, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(companyService.listCompanies(search, pageable));
    }
}
