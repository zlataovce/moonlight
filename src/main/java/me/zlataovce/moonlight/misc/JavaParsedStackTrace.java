package me.zlataovce.moonlight.misc;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@ToString
public class JavaParsedStackTrace {
    @Getter @Setter
    private String type;
    @Getter @Setter
    private String optionalMessage = null;
    @Getter
    private final List<StackTraceElement> stack = new ArrayList<>();
    @Getter @Setter
    private int line;
}
