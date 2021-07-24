package me.zlataovce.moonlight.misc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class JavaStackTraceParser {
    private static final Pattern excPattern = Pattern.compile("(?:(?![\\n\\r])\\s)+at ");

    public static HashMap<Integer, String> isolateExceptions(List<String> s) {
        boolean isCounting = false;
        StringBuilder currentTrace = new StringBuilder();
        int currentTraceLine = 0;
        HashMap<Integer, String> traces = new HashMap<>();
        for (String line : s) {
            if (isCounting) {
                if ((line.startsWith("at ") || excPattern.matcher(line).find()) && line.contains("(") && line.contains(")")) {
                    String[] linePart = line.split("at ");
                    currentTrace.append("at ").append(linePart[linePart.length - 1]);
                    if (!linePart[linePart.length - 1].endsWith("\n")) {
                        currentTrace.append("\n");
                    }
                    continue;
                } else {
                    isCounting = false;
                    traces.put(currentTraceLine, currentTrace.toString());
                    currentTrace.setLength(0);
                }
            }
            final boolean lineIndex = (s.indexOf(line) + 1) <= s.size() - 1 && (s.get(s.indexOf(line) + 1).startsWith("at ") || excPattern.matcher(s.get(s.indexOf(line) + 1)).find()) && s.get(s.indexOf(line) + 1).contains("(") && s.get(s.indexOf(line) + 1).contains(")") && s.get(s.indexOf(line) + 1).contains(".java");
            if ((line.contains(": ") && lineIndex) || (line.contains("Caused by: ") && lineIndex) || (line.toLowerCase(Locale.ROOT).contains("exception") && lineIndex)) {
                isCounting = true;
                if (line.contains("Caused by: ")) {
                    String[] linePart = line.split("d by: ");
                    currentTrace.append("Caused by: ").append(linePart[1]);
                } else {
                    if (StringUtils.countMatches(line, ":") >= 1 && !line.split(": ")[0].toLowerCase(Locale.ROOT).contains("exception")) {
                        currentTrace.append(Arrays.stream(line.split(": ")).skip(1).collect(Collectors.joining(": ")));
                    } else {
                        currentTrace.append(line);
                    }
                }
                currentTraceLine = s.indexOf(line);
                if (!line.endsWith("\n")) {
                    currentTrace.append("\n");
                }
            }
        }
        return traces;
    }

    public static List<JavaParsedStackTrace> parseExceptions(HashMap<Integer, String> s) {
        List<JavaParsedStackTrace> javaParsedStackTraces = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : s.entrySet()) {
            String trace = entry.getValue();
            List<String> traceLines = Arrays.stream(trace.split("\n")).collect(Collectors.toList());
            JavaParsedStackTrace javaParsedStackTrace = new JavaParsedStackTrace();
            final boolean lineIndex = (trace.indexOf(traceLines.get(0)) + 1) <= traceLines.size() - 1 && (traceLines.get(traceLines.indexOf(traceLines.get(0)) + 1).startsWith("at ") || excPattern.matcher(traceLines.get(traceLines.indexOf(traceLines.get(0)) + 1)).find()) && traceLines.get(traceLines.indexOf(traceLines.get(0)) + 1).contains("(") && traceLines.get(traceLines.indexOf(traceLines.get(0)) + 1).contains(")") && traceLines.get(traceLines.indexOf(traceLines.get(0)) + 1).contains(".java");
            if (traceLines.get(0).contains(": ") && lineIndex) {
                List<String> split = Arrays.stream(traceLines.get(0).split(": ")).collect(Collectors.toList());
                if (traceLines.get(0).contains("Caused by: ")) {
                    split.remove(0);
                }
                javaParsedStackTrace.setType(split.get(0));
                if (split.size() > 1) {
                    javaParsedStackTrace.setOptionalMessage(split.stream().skip(1).collect(Collectors.joining(": ")));
                }
            } else {
                javaParsedStackTrace.setType(traceLines.get(0));
            }

            for (String line : traceLines.stream().skip(1).collect(Collectors.toList())) {
                List<String> preprocess = Arrays.stream(excPattern.matcher(line).replaceFirst("").replaceFirst("at ", "").split(" ")).collect(Collectors.toList());
                if (preprocess.get(preprocess.size() - 1).contains("[")) {
                    preprocess.remove(preprocess.size() - 1);
                }
                String[] elementParts = String.join(" ", preprocess).split("\\(");
                String[] fileAndLineCnt = elementParts[1].replace(")", "").split(":");
                List<String> classParts = Arrays.stream(elementParts[0].split("\\.")).collect(Collectors.toList());
                String method = classParts.remove(classParts.size() - 1);
                javaParsedStackTrace.getStack().add(new StackTraceElement(String.join(".", classParts), method, (fileAndLineCnt.length == 1) ? null : fileAndLineCnt[0], (fileAndLineCnt.length == 1) ? -2 : Integer.parseInt(fileAndLineCnt[1].trim())));
            }
            javaParsedStackTrace.setLine(entry.getKey());
            javaParsedStackTraces.add(javaParsedStackTrace);
        }
        return javaParsedStackTraces;
    }
}
