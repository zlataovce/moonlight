package me.zlataovce.moonlight.controllers;

import lombok.RequiredArgsConstructor;
import me.zlataovce.moonlight.storage.Paste;
import me.zlataovce.moonlight.storage.PastePost;
import me.zlataovce.moonlight.storage.PasteRepository;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @GetMapping(path = "/pastes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPaste(@PathVariable String id) {
        final Optional<Paste> result = this.pasteRepository.findByIdentifier(id);
        if (result.isPresent()) {
            return new ResponseEntity<>(result.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Collections.singletonMap("error", "Not found"), HttpStatus.NOT_FOUND);
        }
    }

    @SuppressWarnings("deprecation")
    @PostMapping(path = "/pastes", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createPaste(@RequestBody PastePost data) {
        if (Objects.equals(data.getType(), "base64") && !Base64.isBase64(data.getContent())) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Not base64 encoded"), HttpStatus.BAD_REQUEST);
        }
        if (data.getContent() == null || data.getType() == null) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Invalid request body"), HttpStatus.BAD_REQUEST);
        }
        final String content = (Objects.equals(data.getType(),"base64")) ? data.getContent() : java.util.Base64.getEncoder().encodeToString(Objects.requireNonNull(data.getContent()).getBytes(Charset.defaultCharset()));
        return new ResponseEntity<>(this.pasteRepository.save(
                new Paste()
                        .setContent(content)
                        .setIdentifier(RandomStringUtils.randomAlphanumeric(15))
        ), HttpStatus.OK);
    }
}
