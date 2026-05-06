package logging_service.mapper;

import logging_service.dto.response.RetentionPolicyResponse;
import logging_service.entity.LogRetentionPolicy;
import org.springframework.stereotype.Component;

@Component
public class RetentionPolicyMapper {

    public RetentionPolicyResponse toRetentionPolicyResponse(LogRetentionPolicy policy) {
        return RetentionPolicyResponse.builder()
                .id(policy.getId())
                .category(policy.getCategory())
                .retentionDays(policy.getRetentionDays())
                .enabled(policy.getEnabled())
                .createdAt(policy.getCreatedAt())
                .updatedAt(policy.getUpdatedAt())
                .build();
    }
}
