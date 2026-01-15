package com.bsg.trustedone.controller;

import com.bsg.trustedone.dto.GroupFormDto;
import com.bsg.trustedone.dto.GroupDto;
import com.bsg.trustedone.dto.GroupListingDto;
import com.bsg.trustedone.dto.PageResponse;
import com.bsg.trustedone.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @GetMapping
    public ResponseEntity<PageResponse<GroupListingDto>> listGroups(@RequestParam(required = false) String search, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(groupService.listGroups(search, pageable));
    }

    @PostMapping
    public ResponseEntity<GroupDto> createGroup(@RequestBody GroupFormDto request) {
        var createdGroup = groupService.createGroup(request);
        var uri = URI.create(String.format("/group/%d", createdGroup.getGroupId()));
        return ResponseEntity.created(uri).body(createdGroup);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDto> findGroup(@PathVariable("groupId") Long groupId) {
        return ResponseEntity.ok(groupService.findById(groupId));
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable("groupId") Long groupdId) {
        groupService.deleteGroup(groupdId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<GroupDto> updateGroup(@PathVariable("groupId") Long groupId, @RequestBody GroupFormDto groupCreationDto) {
        return ResponseEntity.ok(groupService.updateGroup(groupCreationDto, groupId));
    }

}
