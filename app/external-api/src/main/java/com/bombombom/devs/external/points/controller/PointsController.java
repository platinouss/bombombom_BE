package com.bombombom.devs.external.points.controller;

import com.bombombom.devs.external.global.web.LoginUser;
import com.bombombom.devs.external.points.controller.dto.GetPointsResponse;
import com.bombombom.devs.external.points.service.PointsService;
import com.bombombom.devs.security.AppUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/points")
public class PointsController {

    private final PointsService pointsService;

    @GetMapping("/me")
    public ResponseEntity<GetPointsResponse> getUserPoints(@LoginUser AppUserDetails userDetails) {
        Long currentPoints = pointsService.getCurrentPoints(userDetails.getId());
        return ResponseEntity.ok(GetPointsResponse.fromResult(currentPoints));
    }
}
