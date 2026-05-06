package logging_service.service.impl;

import logging_service.common.response.DataResponseMessage;
import logging_service.common.response.PageResponse;
import logging_service.dto.request.CreateErrorLogRequest;
import logging_service.dto.request.ResolveErrorLogRequest;
import logging_service.dto.response.ErrorLogResponse;
import logging_service.entity.ErrorLog;
import logging_service.enums.ErrorSeverity;
import logging_service.exception.ErrorLogNotFoundException;
import logging_service.mapper.ErrorLogMapper;
import logging_service.repository.ErrorLogRepository;
import logging_service.service.ErrorLogService;
import logging_service.util.PageResponseConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ErrorLogServiceImpl implements ErrorLogService {

    private static final int MAX_PAGE_SIZE = 100;

    private final ErrorLogRepository errorLogRepository;
    private final ErrorLogMapper errorLogMapper;
    private final PageResponseConverter pageConverter;

    @Override
    @Transactional
    public DataResponseMessage<ErrorLogResponse> createErrorLog(CreateErrorLogRequest request) {
        ErrorLog errorLog = errorLogMapper.toErrorLog(request);
        ErrorLog saved = errorLogRepository.save(errorLog);
        return DataResponseMessage.success("Error log created", errorLogMapper.toErrorLogResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<ErrorLogResponse> getErrorLogById(Long id) {
        ErrorLog errorLog = errorLogRepository.findById(id)
                .orElseThrow(() -> new ErrorLogNotFoundException(id));
        return DataResponseMessage.success("Error log found", errorLogMapper.toErrorLogResponse(errorLog));
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<ErrorLogResponse>> getUnresolvedErrors(int page, int size) {
        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ErrorLog> errorPage = errorLogRepository.findByResolvedFalse(pageable);
        PageResponse<ErrorLogResponse> response = pageConverter.convert(errorPage, errorLogMapper::toErrorLogResponse);
        return DataResponseMessage.success("Unresolved errors", response);
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<ErrorLogResponse>> getCriticalErrors(int page, int size) {
        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ErrorLog> errorPage = errorLogRepository.findBySeverityAndResolvedFalse(ErrorSeverity.CRITICAL, pageable);
        PageResponse<ErrorLogResponse> response = pageConverter.convert(errorPage, errorLogMapper::toErrorLogResponse);
        return DataResponseMessage.success("Critical unresolved errors", response);
    }

    @Override
    @Transactional
    public DataResponseMessage<ErrorLogResponse> resolveErrorLog(Long errorLogId, ResolveErrorLogRequest request) {
        ErrorLog errorLog = errorLogRepository.findById(errorLogId)
                .orElseThrow(() -> new ErrorLogNotFoundException(errorLogId));

        errorLog.setResolved(true);
        errorLog.setResolvedBy(request.getResolvedBy());
        errorLog.setResolvedAt(LocalDateTime.now());
        errorLog.setResolutionNote(request.getResolutionNote());

        ErrorLog saved = errorLogRepository.save(errorLog);
        return DataResponseMessage.success("Error log resolved successfully", errorLogMapper.toErrorLogResponse(saved));
    }
}
