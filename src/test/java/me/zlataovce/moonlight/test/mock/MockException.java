package me.zlataovce.moonlight.test.mock;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
public class MockException {
    @Getter
    private final String name;
    @Getter
    private final String trace;
    @Getter
    private final String message;
    @Getter
    private final StackTraceElement[] stackTraceElements;
}
