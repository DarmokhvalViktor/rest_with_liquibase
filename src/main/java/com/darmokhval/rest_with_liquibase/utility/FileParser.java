package com.darmokhval.rest_with_liquibase.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FileParser {
    private static final int CHUNK_SIZE = 50 * 1024 * 1024; //50MB chunk size for thread to process;
    private static final int MIN_CHUCK_SIZE = 50 * 1024 * 1024; //Minimum file size to use multiple threads

    /** calls method that parse file and writes data in a map.
     * if file size > 50mb, creates more threads to speed up parsing process.
     */
    public void parseFile(File file, String searchedField, Map<String, Integer> countFieldMap) {
        if (!checkFileIfJson(file)) {
            return;
        }

        long fileSize = file.length();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int numThreads = Math.max(1, Math.min(availableProcessors * 2, (int) Math.ceil((double) fileSize / MIN_CHUCK_SIZE)));
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        long offset = 0;
        for (int i = 0; i < numThreads; i++) {
            long start = offset;
            long end = Math.min(start + CHUNK_SIZE, fileSize);
            executor.execute(() -> processChunk(file, start, end, searchedField, countFieldMap));
            offset = end;
        }

        executor.shutdown();
        try {
            boolean terminated = executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
            if(!terminated) {
                System.out.println("Threads are taking too long to finish.");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread termination was interrupted.");
        }
    }

    /**
     * method iterates over one file in chunks to reduce RAM consumption, and writes result to a map that passed as argument.
     */

    private void processChunk(File file, long start, long end, String searchedField, Map<String, Integer> countFieldMap) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))){
            reader.skip(start);
            long bytesRead = start;
            String line;
            while((line = reader.readLine()) != null && bytesRead < end) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(line);
                String fieldValue = rootNode.path(searchedField).asText();
                String[] fieldValues = fieldValue.split(",\\s*");
                for (String value: fieldValues) {
                    if (!searchedField.equals(value.trim())) {
                        countFieldMap.merge(value.trim(), 1, Integer::sum);
                    }
                }
                bytesRead += line.length() + System.lineSeparator().length();
            }
        } catch (IOException e) {
            log.error("An error occurred while reading from a file", e);
        }

    }

    /**
     * checks if file exactly in .json format
     * @param file file to check
     * @return false if file is not json
     */
    private boolean checkFileIfJson(File file) {
        return file != null && file.getName().endsWith(".json");
    }
}
