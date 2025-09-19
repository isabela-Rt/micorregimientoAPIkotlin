package com.micorregimiento.micorregimiento.Generics.services;

import com.micorregimiento.micorregimiento.Notifications.interfaces.IWebhookService;
import com.micorregimiento.micorregimiento.Notifications.entitys.WebhookPayload;
import com.micorregimiento.micorregimiento.Config.WebhookConfig;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;

@Service
public class WebhookService implements IWebhookService {

    private final RestTemplate restTemplate;
    private final WebhookConfig webhookConfig;

    @Autowired
    public WebhookService(WebhookConfig webhookConfig) {
        this.webhookConfig = webhookConfig;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public boolean sendToWebhook(String webhookUrl, WebhookPayload payload) {
        if (!webhookConfig.isWebhookEnabled()) {
            System.out.println("Webhook está deshabilitado en configuración");
            return false;
        }

        System.out.println("Intentando enviar a webhook: " + webhookUrl);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<WebhookPayload> entity = new HttpEntity<>(payload, headers);

            System.out.println("Enviando payload: " + payload);

            ResponseEntity<String> response = restTemplate.exchange(
                    webhookUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            System.out.println("Respuesta del webhook - Status: " + response.getStatusCode());
            System.out.println("Respuesta del webhook - Body: " + response.getBody());

            return response.getStatusCode().is2xxSuccessful();

        } catch (ResourceAccessException e) {
            System.err.println("Error de conexión con el webhook: " + e.getMessage());
            System.err.println("URL: " + webhookUrl);
            System.err.println("Posibles causas: Host no encontrado, timeout, o problemas de red");
            return false;

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("Error HTTP del servidor webhook: " + e.getStatusCode());
            System.err.println("Respuesta: " + e.getResponseBodyAsString());
            return false;

        } catch (Exception e) {
            System.err.println("Error inesperado enviando al webhook: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean sendToWebhook(String webhookUrl, Object data, String eventType) {
        WebhookPayload payload = WebhookPayload.builder()
                .eventType(eventType)
                .data(data)
                .timestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                .source("micorregimiento-api")
                .build();

        return sendToWebhook(webhookUrl, payload);
    }

    public boolean sendToN8nWebhook(Object data, String eventType) {
        return sendToWebhook(webhookConfig.getN8nWebhookUrl(), data, eventType);
    }
}