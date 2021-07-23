package me.zlataovce.moonlight.misc;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class JavaParsedStackTrace {
    @Getter @Setter
    private String type;
    @Getter @Setter
    private String optionalMessage = null;
    @Getter
    private final List<StackTraceElement> stack = new ArrayList<>();
}
