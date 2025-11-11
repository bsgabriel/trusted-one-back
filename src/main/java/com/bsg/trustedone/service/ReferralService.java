package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.PageResponse;
import com.bsg.trustedone.dto.ReferralCreationDto;
import com.bsg.trustedone.dto.ReferralDto;
import com.bsg.trustedone.entity.Referral;
import com.bsg.trustedone.enums.ReferralSortType;
import com.bsg.trustedone.enums.ReferralStatus;
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
            var predicate = cb.equal(root.get("userId"), loggedUser.getUserId());

            List<Predicate> searchPredicates = new ArrayList<>();

            if (StringUtils.isNotBlank(search)) {
                var searchPattern = "%" + search.toLowerCase() + "%";

                searchPredicates.add(cb.like(cb.lower(root.get("referredTo")), searchPattern));

                var partnerJoin = root.join("partner", JoinType.LEFT);
                searchPredicates.add(cb.like(cb.lower(partnerJoin.get("name")), searchPattern));
            }

            if (status != null) {
                searchPredicates.add(cb.equal(root.get("status"), status));
            }

            if (!searchPredicates.isEmpty()) {
                var searchPredicate = cb.or(searchPredicates.toArray(new Predicate[0]));
                predicate = cb.and(predicate, searchPredicate);
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
}
