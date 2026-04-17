package com.parkcontrol.common.dto;

import java.util.List;

@Builder
public record PageResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages
) {}