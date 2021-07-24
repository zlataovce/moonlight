package me.zlataovce.moonlight.test.mock;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;

public class MockExceptionGenerator {
    public static List<MockException> getStackTraces(Exception[] exceptions) {
        List<MockException> traces = new ArrayList<>();
        for (Exception exc : exceptions) {
            try {
                throw exc;
            } catch (Exception e) {
                traces.add(new MockException(exc.getClass().getName(), ExceptionUtils.getStackTrace(e), e.getMessage(), e.getStackTrace()));
            }
        }
        return traces;
    }
}
