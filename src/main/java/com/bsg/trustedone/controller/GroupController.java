package com.bsg.trustedone.controller;

import com.bsg.trustedone.dto.GroupCreationDto;
import com.bsg.trustedone.dto.GroupDto;
import com.bsg.trustedone.service.GroupService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<GroupDto>> findAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @PostMapping
    public ResponseEntity<GroupDto> createGroup(@RequestBody GroupCreationDto request) {
        var createdGroup = groupService.createGroup(request);
        var uri = URI.create(String.format("/group/%d", createdGroup.getGroupId()));
        return ResponseEntity.created(uri).body(createdGroup);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable("groupId") Long groupdId) {
        groupService.deleteGroup(groupdId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<GroupDto> update(@PathVariable("groupId") Long groupId, @RequestBody GroupCreationDto groupCreationDto) {
        return ResponseEntity.ok(groupService.updateGroup(groupCreationDto, groupId));
    }


}
