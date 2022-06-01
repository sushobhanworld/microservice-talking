package com.sushobhan.clients.fraud;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("notification")
public interface NotificationClient {
    @PostMapping("/api/v1/notification")
    void sendNotificationPings(NotificationRequest notificationRequest);
}
