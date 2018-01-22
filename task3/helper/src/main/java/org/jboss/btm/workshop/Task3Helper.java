package org.jboss.btm.workshop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jboss.byteman.rule.Rule;

public class Task3Helper extends org.jboss.byteman.rule.helper.Helper {
    private static final String GREETINGS_FILE_NAME
        = System.getProperty("org.jboss.byteman.task3.greetingsfile", "task3.greetings");

    protected Task3Helper(Rule rule) {
        super(rule);
    }

    public boolean isGreeting(String greeting) {
        try {
            Optional<InputStream> is = loadFile(GREETINGS_FILE_NAME);
            if(!is.isPresent()) {
              System.err.println(Task3Helper.class.getName() + " can't load file " + GREETINGS_FILE_NAME);
              return false;
            }

            List<String> greetings = loadLines(is.get());

            return greetings.stream()
                .map(String::toLowerCase).anyMatch(str -> str.equals(greeting.toLowerCase()));
        } catch (Exception e) {
            System.err.printf("Error on loading properties file %s - %s:%s%n",
                    GREETINGS_FILE_NAME, e.getClass(), e.getMessage());
        }
        return false;
    }

    private Optional<InputStream> loadFile(String fileName) throws FileNotFoundException {
        InputStream is = null;
        if (new File(fileName).exists()) {
            is = new FileInputStream(fileName);
        } else if (getClass().getResourceAsStream("/" + fileName) != null) {
            is = getClass().getResourceAsStream("/" + fileName);
        } else if (getClass().getClassLoader().getResourceAsStream(fileName) != null) {
            is = getClass().getClassLoader().getResourceAsStream(fileName);
        }

        return is == null ? Optional.empty() : Optional.of(is);
    }

    private List<String> loadLines(InputStream is) throws IOException {
        List<String> greetings = new ArrayList<>();
        String line;
        BufferedReader bf = new BufferedReader(new InputStreamReader(is));
        while ((line = bf.readLine()) != null) {
            greetings.add(line);
        }
        return greetings;
    }
}
