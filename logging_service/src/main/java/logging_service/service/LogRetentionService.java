package logging_service.service;

import logging_service.common.response.DataResponseMessage;
import logging_service.common.response.ResponseMessage;
import logging_service.dto.request.UpdateRetentionPolicyRequest;
import logging_service.dto.response.RetentionPolicyResponse;
import logging_service.enums.LogCategory;

import java.util.List;

public interface LogRetentionService {

    DataResponseMessage<RetentionPolicyResponse> createOrUpdatePolicy(UpdateRetentionPolicyRequest request);

    DataResponseMessage<List<RetentionPolicyResponse>> getPolicies();

    ResponseMessage cleanupOldLogs();

    ResponseMessage cleanupLogsByCategory(LogCategory category);
}
