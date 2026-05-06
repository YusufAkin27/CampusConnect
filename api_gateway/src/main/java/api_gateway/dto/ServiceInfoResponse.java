package api_gateway.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ServiceInfoResponse {
    private String serviceName;
    private String serviceId;
    private String address;
    private int port;
    private String healthStatus;
    private List<String> tags;
}
