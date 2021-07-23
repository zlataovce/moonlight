package me.zlataovce.moonlight.config;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>A simple properties file builder.</p>
 *
 * @author zlataovce <34477304+zlataovce@users.noreply.github.com>
 */
@Accessors(chain = true)
@RequiredArgsConstructor
public class PropertiesBuilder {
    /**
     * <p>Represents the BufferedWriter which the builder will write with.</p>
     */
    private final BufferedWriter writer;
    /**
     * <p>Represents the property node structure.</p>
     */
    private final HashMap<String, String> nodes = new HashMap<>();

    /**
     * <p>Creates a new property node.</p>
     *
     * @param name the node name
     * @param value the node value
     * @return this builder
     */
    public PropertiesBuilder node(String name, String value) {
        this.nodes.put(name, value);
        return this;
    }

    /**
     * <p>Writes the node structure to a file.</p>
     *
     * @throws IOException if any error occurs when writing the file
     */
    public void build() throws IOException {
        for (Map.Entry<String, String> entry : this.nodes.entrySet()) {
            this.writer.write(entry.getKey() + "=" + entry.getValue());
            this.writer.newLine();
        }
        this.writer.close();
    }
}
