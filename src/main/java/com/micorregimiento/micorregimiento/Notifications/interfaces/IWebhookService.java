package com.micorregimiento.micorregimiento.Notifications.interfaces;


import com.micorregimiento.micorregimiento.Notifications.entitys.WebhookPayload;

public interface IWebhookService {
    boolean sendToWebhook(String webhookUrl, WebhookPayload payload);
    boolean sendToWebhook(String webhookUrl, Object data, String eventType);
}