package admin_service.security;

import admin_service.entity.AdminActionLog;
import admin_service.repository.AdminActionLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Parameter;

/**
 * AOP aspect that intercepts methods annotated with @AdminAction
 * and automatically creates audit log entries in AdminActionLog.
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AdminActionAspect {

    private final AdminActionLogRepository actionLogRepository;

    @Around("@annotation(adminAction)")
    public Object logAdminAction(ProceedingJoinPoint joinPoint, AdminAction adminAction) throws Throwable {
        // Execute the actual method first
        Object result = joinPoint.proceed();

        try {
            Long adminId = resolveAdminId();
            Long targetId = resolveTargetId(joinPoint);
            String ipAddress = resolveIpAddress();
            String userAgent = resolveUserAgent();

            String description = adminAction.description().isEmpty()
                    ? adminAction.actionType().name() + " on " + adminAction.targetType().name()
                    : adminAction.description();

            AdminActionLog actionLog = AdminActionLog.builder()
                    .adminId(adminId)
                    .actionType(adminAction.actionType())
                    .targetType(adminAction.targetType())
                    .targetId(targetId)
                    .description(description)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .build();

            actionLogRepository.save(actionLog);
            log.debug("Admin action logged: {} by admin {} on {} {}",
                    adminAction.actionType(), adminId, adminAction.targetType(), targetId);
        } catch (Exception e) {
            log.error("Failed to log admin action: {}", e.getMessage(), e);
            // Do not rethrow - audit logging failure should not affect business logic
        }

        return result;
    }

    private Long resolveAdminId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AdminUserDetails adminDetails) {
            return adminDetails.getAdminId();
        }
        // Fallback: try header
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            String adminIdHeader = attrs.getRequest().getHeader("X-Admin-Id");
            if (adminIdHeader != null && !adminIdHeader.isBlank()) {
                try {
                    return Long.parseLong(adminIdHeader.trim());
                } catch (NumberFormatException ignored) {}
            }
        }
        return null;
    }

    private Long resolveTargetId(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Parameter[] parameters = signature.getMethod().getParameters();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameters.length; i++) {
            String paramName = parameters[i].getName();
            if (paramName.contains("Id") || paramName.contains("id")) {
                if (args[i] instanceof Long) {
                    return (Long) args[i];
                }
            }
        }
        return null;
    }

    private String resolveIpAddress() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            String forwarded = request.getHeader("X-Forwarded-For");
            if (forwarded != null && !forwarded.isBlank()) {
                return forwarded.split(",")[0].trim();
            }
            return request.getRemoteAddr();
        }
        return null;
    }

    private String resolveUserAgent() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            return attrs.getRequest().getHeader("User-Agent");
        }
        return null;
    }
}
