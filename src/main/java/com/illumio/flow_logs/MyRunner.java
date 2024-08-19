package com.illumio.flow_logs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class MyRunner implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(MyRunner.class);

    @Autowired
    private DataLookupLoader dataLookupLoader;

    @Override
    public void run(String... args) {
        System.out.print("Enter input file: ");
        Scanner scanner = new Scanner(System.in);
        String inputFile = scanner.nextLine();
        System.out.print("Enter output folder: ");
        Scanner outputScanner = new Scanner(System.in);
        String outputDir = scanner.nextLine();

        File file = new File(inputFile);

        try (Stream<String> rows = Files.lines(Paths.get(file.getAbsolutePath()))) {
            Map<String, Long> portProtocolCombinationMapByFreq = rows
                    .filter(row -> row.trim().length() > 0)
                    .map(this::buildKey)
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            String portProtocolRes =  outputDir + "/" +  "portProtocolCombine.txt";
            writeOutput(portProtocolRes, portProtocolCombinationMapByFreq, Arrays.asList("Port.", "Protocol.", "Count."));

            Map<String, Long> tagsBasedCount = portProtocolCombinationMapByFreq.keySet().stream().map(
                    key -> dataLookupLoader.getTagMappedByPortProtocol()
                            .getOrDefault(key.toLowerCase(), "Untagged")
            ).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            String tagBasedCnt =  outputDir + "/" +  "tagBasedCnt.txt";
            writeOutput(tagBasedCnt, tagsBasedCount, Arrays.asList("Tag.", "Count."));

        } catch (IOException exception) {
            LOG.error("Failed to read data from input file {}", inputFile);
            throw new FlowLogsException("Failed to read data from input file", exception);
        }
    }

    private void writeOutput(String outputFile, Map<String, Long> output, List<String> headers) throws IOException {
        List<List<String>> rows = output.keySet().stream().map(key -> {
            List<String> row = new ArrayList<>(Arrays.asList(key.split(":")));
            row.add(String.valueOf(output.get(key)));
            return row;
        }).collect(Collectors.toList());
        rows.add(0, headers);

        File file = new File(outputFile);
        if (file.exists()) {
            file.delete();
        }

        file.createNewFile();
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        for (int i = 0; i < rows.size(); i++) {
            bw.write(String.join(",", rows.get(i)));
            bw.newLine();
        }
        bw.flush();
        bw.close();
    }

    private String buildKey(String row) {
        String [] cols = row.split(",");
        return String.join(":", cols);
    }
}
