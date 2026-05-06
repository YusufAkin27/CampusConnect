package logging_service.service;

import logging_service.common.response.DataResponseMessage;
import logging_service.dto.response.DailyLogStatsResponse;
import logging_service.dto.response.LogStatsResponse;
import logging_service.dto.response.ServiceLogStatsResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface LogStatsService {

    DataResponseMessage<LogStatsResponse> getGeneralStats(
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    DataResponseMessage<List<ServiceLogStatsResponse>> getStatsByService(
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    DataResponseMessage<List<DailyLogStatsResponse>> getDailyStats(
            LocalDate startDate,
            LocalDate endDate
    );
}
