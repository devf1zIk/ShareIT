package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

@Component
public class ItemRequestClient {

    private final RestTemplate rest;
    private final String serverUrl;

    public ItemRequestClient(RestTemplate restTemplate,
                             @Value("${shareit-server.url}") String serverUrl) {
        this.rest = restTemplate;
        this.serverUrl = serverUrl + "/requests";
    }

    public ResponseEntity<Object> create(Long userId, NewItemRequestDto dto) {
        return rest.exchange(
                serverUrl,
                HttpMethod.POST,
                new HttpEntity<>(dto, defaultHeaders(userId)),
                Object.class
        );
    }

    public ResponseEntity<Object> getOwn(Long userId) {
        return rest.exchange(
                serverUrl,
                HttpMethod.GET,
                new HttpEntity<>(defaultHeaders(userId)),
                Object.class
        );
    }

    public ResponseEntity<Object> getAll(Long userId, Integer from, Integer size) {
        String url = String.format("%s/all?from=%d&size=%d", serverUrl, from, size);
        return rest.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(defaultHeaders(userId)),
                Object.class
        );
    }

    public ResponseEntity<Object> getById(Long userId, Long requestId) {
        String url = serverUrl + "/" + requestId;
        return rest.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(defaultHeaders(userId)),
                Object.class
        );
    }

    private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(userId));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
