package me.zlataovce.moonlight.storage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class PastePost {
    @JsonProperty(value = "type", required = true)
    private String type;
    @JsonProperty(value = "content", required = true)
    private String content;
}
