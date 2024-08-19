package com.illumio.flow_logs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DataLookupLoader {
    private static final Logger LOG = LoggerFactory.getLogger(DataLookupLoader.class);
    private Map<String, String> tagMappedByPortProtocol = null;
    private String LOOK_UP_DATA_CVS_FILE = "data/lookup-data.csv";
    private String COMMA_DELIMITER = ",";

    public Map<String, String> getTagMappedByPortProtocol() {
        if (tagMappedByPortProtocol == null) {
            LOG.error("Failed to load tags based lookup data.");
            throw new FlowLogsException("Failed to load tags based lookup data.");
        }
        return tagMappedByPortProtocol;
    }
    public void loadData() throws URISyntaxException, IOException {
        URL resource = getClass().getClassLoader().getResource(LOOK_UP_DATA_CVS_FILE);
        File file = Paths.get(resource.toURI()).toFile();
        try (Stream<String> rows = Files.lines(Paths.get(file.getAbsolutePath())).skip(1)) {
            tagMappedByPortProtocol = rows.filter(row -> row.trim().length() > 0)
                    .map(row -> createTagMap(row))
                    .flatMap(map -> map.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
    }

    private Map<String, String> createTagMap(String row) {
        String[] cols = row.split(COMMA_DELIMITER);
        String key = cols[0] + ":" + cols[1];
        return Map.of(key.toLowerCase(), cols[2]);
    }
}
