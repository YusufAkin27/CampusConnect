package logging_service.service;

import logging_service.common.response.DataResponseMessage;
import logging_service.dto.request.BatchLogEntryRequest;
import logging_service.dto.request.CreateLogEntryRequest;
import logging_service.dto.response.BatchLogResponse;
import logging_service.dto.response.LogEntryResponse;

import java.util.List;

/**
 * Service responsible for ingesting logs from other microservices.
 *
 * TODO: Replace HTTP log ingestion with Kafka/RabbitMQ event consumer in production scale.
 * Kafka consumer would subscribe to a 'campus-connect-logs' topic and process log events asynchronously,
 * providing better throughput and resilience than HTTP ingestion.
 */
public interface LogIngestionService {

    DataResponseMessage<LogEntryResponse> createLog(CreateLogEntryRequest request);

    DataResponseMessage<BatchLogResponse> createBatchLogs(BatchLogEntryRequest request);

    void createLogAsync(CreateLogEntryRequest request);

    void createBatchLogsAsync(List<CreateLogEntryRequest> requests);
}
