package com.library.personnel.controller;

import com.library.personnel.dto.request.WhitelistRequest;
import com.library.personnel.dto.response.ApiResponse;
import com.library.personnel.dto.response.WhitelistResponse;
import com.library.personnel.enums.WhitelistType;
import com.library.personnel.service.WhitelistService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/whitelist")
public class WhitelistController {

    private final WhitelistService whitelistService;

    public WhitelistController(WhitelistService whitelistService) {
        this.whitelistService = whitelistService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WhitelistResponse>>> listWhitelist(
            @RequestParam(required = false) WhitelistType type) {
        return ResponseEntity.ok(ApiResponse.success(whitelistService.listWhitelist(type)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WhitelistResponse>> addToWhitelist(
            @Valid @RequestBody WhitelistRequest request) {
        return ResponseEntity.ok(ApiResponse.success(whitelistService.addToWhitelist(request)));
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<List<WhitelistResponse>>> batchAddToWhitelist(
            @Valid @RequestBody List<WhitelistRequest> requests) {
        return ResponseEntity.ok(ApiResponse.success(whitelistService.batchAddToWhitelist(requests)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> removeFromWhitelist(@PathVariable Long id) {
        whitelistService.removeFromWhitelist(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkWhitelist(
            @RequestParam String employeeId,
            @RequestParam WhitelistType type) {
        boolean exists = whitelistService.checkWhitelist(employeeId, type);
        return ResponseEntity.ok(ApiResponse.success(Map.of("exists", exists)));
    }
}