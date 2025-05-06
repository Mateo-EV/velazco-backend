package com.velazco.velazco_backend.dto.order.responses;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderListResponseDto {
    private Long id;
    private LocalDateTime date;
    private String clientName;
    private String status;
    private AttendedBy attendedBy;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AttendedBy {
        private Long id;
        private String name;
    }
}
