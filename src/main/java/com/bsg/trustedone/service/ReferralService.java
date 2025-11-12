package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.PageResponse;
import com.bsg.trustedone.dto.ReferralCreationDto;
import com.bsg.trustedone.dto.ReferralDto;
import com.bsg.trustedone.entity.Referral;
import com.bsg.trustedone.enums.ReferralSortType;
import com.bsg.trustedone.enums.ReferralStatus;
import com.bsg.trustedone.exception.ResourceNotFoundException;
import com.bsg.trustedone.exception.ResourceUpdateException;
import com.bsg.trustedone.exception.UnauthorizedAccessException;
import com.bsg.trustedone.mapper.ReferralMapper;
import com.bsg.trustedone.repository.ReferralRepository;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReferralService {

    private final UserService userService;
    private final ReferralMapper referralMapper;
    private final ReferralRepository referralRepository;

    public Long createReferral(ReferralCreationDto newReferral) {
        // TODO: validar

        var entity = referralMapper.toEntity(newReferral, userService.getLoggedUser());
        return referralRepository.save(entity).getReferralId();
    }

    public PageResponse<ReferralDto> findByFilter(String search, ReferralStatus status, ReferralSortType sortBy, Pageable pageable) {
        var loggedUser = userService.getLoggedUser();
        Specification<Referral> spec = (root, query, cb) -> {
            if (query != null && !query.getResultType().equals(Long.class)) {
                var expertiseFetch = root.fetch("expertise", JoinType.LEFT);
                expertiseFetch.fetch("parentExpertise", JoinType.LEFT);

                var partnerFetch = root.fetch("partner", JoinType.LEFT);
                partnerFetch.fetch("company", JoinType.LEFT);
                partnerFetch.fetch("group", JoinType.LEFT);
            }

            var predicate = cb.equal(root.get("userId"), loggedUser.getUserId());

            if (StringUtils.isNotBlank(search)) {
                var searchPattern = "%" + search.toLowerCase() + "%";
                List<Predicate> searchPredicates = new ArrayList<>();

                searchPredicates.add(cb.like(cb.lower(root.get("referredTo")), searchPattern));

                var partnerJoin = root.join("partner", JoinType.LEFT);
                searchPredicates.add(cb.like(cb.lower(partnerJoin.get("name")), searchPattern));

                var searchPredicate = cb.or(searchPredicates.toArray(new Predicate[0]));
                predicate = cb.and(predicate, searchPredicate);
            }

            if (status != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }

            return predicate;
        };

        var sortType = switch (sortBy) {
            case RECENT -> Sort.by("createdAt").descending();
            case OLDEST -> Sort.by("createdAt").ascending();
            default -> Sort.by("partner.name").ascending();
        };

        var sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortType);
        var page = referralRepository.findAll(spec, sortedPageable);

        return PageResponse.from(page.map(referralMapper::toDto));
    }

    @Transactional
    public ReferralDto updateStatus(Long referralId, ReferralStatus status) {
        if (ReferralStatus.PENDING.equals(status)) {
            throw new ResourceUpdateException("Could not update referral status", List.of("Status should be different than 'PENDING'"));
        }

        var loggedUser = userService.getLoggedUser();
        var referral = referralRepository.findById(referralId).orElseThrow(() -> new ResourceNotFoundException("Referral not found"));

        if (!referral.getUserId().equals(loggedUser.getUserId())) {
            throw new UnauthorizedAccessException("User not allowed to perform this operation");
        }

        referral.setStatus(status);
        referral.setUpdatedAt(LocalDateTime.now());
        return referralMapper.toDto(referral);
    }
}
