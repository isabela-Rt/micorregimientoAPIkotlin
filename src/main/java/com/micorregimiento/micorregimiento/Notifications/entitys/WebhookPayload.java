package com.micorregimiento.micorregimiento.Notifications.entitys;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookPayload {
    private String eventType;
    private Object data;
    private Long timestamp;
    private String source;
}