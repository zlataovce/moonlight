package me.zlataovce.moonlight.controllers;

import lombok.RequiredArgsConstructor;
import me.zlataovce.moonlight.storage.Paste;
import me.zlataovce.moonlight.storage.PasteRepository;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1")
public class APIv1Controller {
    private final PasteRepository pasteRepository;

    @GetMapping(path = "/pastes/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getPaste(@PathVariable String id) {
        final Optional<Paste> result = this.pasteRepository.findByIdentifier(id);
        if (result.isPresent()) {
            return new ResponseEntity<>(result.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Collections.singletonMap("error", "Not found"), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(path = "/pastes", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> createPaste(@RequestBody MultiValueMap<String, Object> data) {
        if (!data.containsKey("type") || !data.containsKey("content") || !(data.getFirst("type") instanceof String) || !(data.getFirst("content") instanceof String)) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Invalid request form"), HttpStatus.BAD_REQUEST);
        }
        if (data.getFirst("type") == "base64" && !Base64.isBase64((String) data.getFirst("type"))) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Not base64 encoded"), HttpStatus.BAD_REQUEST);
        }
        final String content = (data.getFirst("type") == "base64") ? ((String) data.getFirst("content")) : java.util.Base64.getEncoder().encodeToString(((String) Objects.requireNonNull(data.getFirst("content"))).getBytes(Charset.defaultCharset()));
        return new ResponseEntity<>(this.pasteRepository.save(
                new Paste()
                        .setContent(content)
                        .setIdentifier(RandomStringUtils.randomAlphanumeric(15))
        ), HttpStatus.OK);
    }
}
