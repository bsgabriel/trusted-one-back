package com.bsg.trustedone.controller;

import com.bsg.trustedone.dto.CompanyCreationDto;
import com.bsg.trustedone.dto.CompanyDto;
import com.bsg.trustedone.service.CompanyService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<CompanyDto> createCompany(@RequestBody CompanyCreationDto request) {
        var createdCompany = companyService.createCompany(request);
        var uri = URI.create(String.format("/company/%d", createdCompany.getCompanyId()));
        return ResponseEntity.created(uri).body(createdCompany);
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<Void> deleteCompany(@PathVariable("companyId") Long companyId) {
        companyService.deleteCompany(companyId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyDto> update(@PathVariable("companyId") Long companyId, @RequestBody CompanyCreationDto companyCreationDto) {
        return ResponseEntity.ok(companyService.updateCompany(companyCreationDto, companyId));
    }

}
