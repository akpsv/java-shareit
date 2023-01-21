package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoIn;

import javax.validation.constraints.Min;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addItem(long userId, ItemDtoIn itemDtoIn) {
        return post("", userId, itemDtoIn);
    }

    public ResponseEntity<Object> getItem(long itemId, long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItems(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }


    public ResponseEntity<Object> update(long itemId, long userId, ItemDtoIn itemDtoIn) {
        return patch("/" + itemId, userId, itemDtoIn);
    }

    public ResponseEntity<Object> search(@RequestParam String text, @RequestParam(required = false) @Min(0) Integer from,
                                         @RequestParam(required = false) @Min(1) Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size);
        return get("/search", null, parameters);
    }

    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") final int userId, @PathVariable long itemId, @RequestBody CommentDtoIn comment) {
        return post("/" + itemId + "/comment", userId, comment);
//        return itemService.addComment(userId, itemId, comment.getText()).get();
    }
}
