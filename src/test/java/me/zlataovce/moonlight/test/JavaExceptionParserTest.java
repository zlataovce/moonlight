package me.zlataovce.moonlight.test;

import lombok.extern.slf4j.Slf4j;
import me.zlataovce.moonlight.misc.JavaParsedStackTrace;
import me.zlataovce.moonlight.misc.JavaStackTraceParser;
import me.zlataovce.moonlight.test.mock.MockException;
import me.zlataovce.moonlight.test.mock.MockExceptionGenerator;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;

@Slf4j
public class JavaExceptionParserTest {
    @Test
    public void testExceptions() {
        List<MockException> exceptions = MockExceptionGenerator.getStackTraces(
                new Exception[] {
                        new NumberFormatException(RandomStringUtils.randomAlphanumeric(15)),
                        new ArrayIndexOutOfBoundsException(RandomStringUtils.randomAlphanumeric(15)),
                        new UnsupportedOperationException(RandomStringUtils.randomAlphanumeric(15)),
                        new StringIndexOutOfBoundsException(RandomStringUtils.randomAlphanumeric(15)),
                        new ArithmeticException(RandomStringUtils.randomAlphanumeric(15)),
                        new ClassCastException(RandomStringUtils.randomAlphanumeric(15)),
                        new ClassNotFoundException(RandomStringUtils.randomAlphanumeric(15)),
                        new IllegalMonitorStateException(RandomStringUtils.randomAlphanumeric(15)),
                        new DuplicateFormatFlagsException(RandomStringUtils.randomAlphanumeric(15)),
                        new ArrayStoreException(RandomStringUtils.randomAlphanumeric(15)),
                        new RuntimeException(),
                        new CloneNotSupportedException(),
                        new IllegalAccessException(),
                        new IllegalArgumentException(),
                        new SecurityException(),
                        new IllegalCallerException(),
                        new InstantiationException(),
                        new InterruptedException(),
                        new ReflectiveOperationException(),
                        new LayerInstantiationException()
                }
        );
        List<String[]> traces = exceptions.stream().map(MockException::getTrace).map(x -> x.split("\n")).collect(Collectors.toList());
        List<String> traceLines = new ArrayList<>();
        traces.forEach(trace -> traceLines.addAll(Arrays.stream(trace).collect(Collectors.toList())));
        final HashMap<Integer, String> firstStage = JavaStackTraceParser.isolateExceptions(traceLines);
        // log.info(firstStage.values().toString());
        List<JavaParsedStackTrace> results = JavaStackTraceParser.parseExceptions(firstStage);
        log.info(firstStage.values().size() + " isolated");
        log.info(results.size() + " parsed");
        log.info(exceptions.size() + " expected");
        if (exceptions.size() > results.size()) {
            log.info("expected: " + exceptions);
            log.info("results: " + results);
        }
        for (MockException e : exceptions) {
            final Optional<JavaParsedStackTrace> parsedStackTrace = results.stream().filter(x -> x.getType().equals(e.getName())).findFirst();
            if (parsedStackTrace.isEmpty()) {
                Assertions.fail(e.getName() + " was not found in the results.");
            }
            Assertions.assertEquals(e.getName(), parsedStackTrace.get().getType());
            Assertions.assertEquals(e.getMessage(), parsedStackTrace.get().getOptionalMessage());
            final List<String> stack = parsedStackTrace.get().getStack().stream().map(StackTraceElement::toString).collect(Collectors.toList());
            int count = 0;
            for (StackTraceElement elem : e.getStackTraceElements()) {
                if (stack.contains(elem.toString())) {
                    count++;
                } else {
                    // log.info("Didn't find line '" + elem.toString() + "' for " + e.getName());
                    if (!e.getTrace().contains(elem.toString())) {
                        // log.info("Line wasn't found in original stacktrace, presuming a false positive.");
                        count++;
                    } else {
                        if (!String.join("\n", firstStage.values()).contains(elem.toString())) {
                            log.info("First stage doesn't contain the missing line.");
                        }
                        Assertions.fail("Didn't find line '" + elem + "' for " + e.getName());
                    }
                }
            }
            // log.info("Validated lines for " + e.getName() + ", " + count + "/" + e.getStackTraceElements().length + " matched.");
        }
    }
}