package event_service.client;

import event_service.client.dto.MediaSummaryRequest;
import event_service.client.dto.MediaSummaryResponse;
import event_service.client.dto.MediaUsageRequest;
import event_service.client.dto.ValidateMediaRequest;
import event_service.client.dto.ValidateMediaResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${media.service.name}")
public interface MediaServiceClient {

    @PostMapping("/v1/api/media/internal/validate")
    ValidateMediaResponse validateMediaIds(@RequestBody ValidateMediaRequest request);

    @PostMapping("/v1/api/media/internal/summaries")
    List<MediaSummaryResponse> getMediaSummaries(@RequestBody MediaSummaryRequest request);

    @PostMapping("/v1/api/media/internal/usage/register")
    void registerMediaUsage(@RequestBody MediaUsageRequest request);

    @PostMapping("/v1/api/media/internal/usage/unregister")
    void unregisterMediaUsage(@RequestBody MediaUsageRequest request);
}
