package com.micorregimiento.micorregimiento.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebhookConfig {

    @Value("${webhook.notifications.default-url}")
    private String n8nWebhookUrl;

    @Value("${webhook.notifications.enabled:true}")
    private boolean webhookEnabled;

    @Value("${webhook.notifications.timeout-seconds:30}")
    private int timeoutSeconds;

    @Value("${webhook.notifications.max-retries:3}")
    private int maxRetries;

    public String getN8nWebhookUrl() {
        return n8nWebhookUrl;
    }

    public boolean isWebhookEnabled() {
        return webhookEnabled;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public int getMaxRetries() {
        return maxRetries;
    }
}