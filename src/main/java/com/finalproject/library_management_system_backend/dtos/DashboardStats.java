package com.finalproject.library_management_system_backend.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStats {
    private long totalBooks;
    private long totalUsers;
    private long activeLoans;
    private long overdueBooks;
    private long totalReservations;
}