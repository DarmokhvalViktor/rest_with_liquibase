package com.darmokhval.rest_with_liquibase.utility;

import java.io.File;

public class FileRemover {

    /**
     * Deletes .json files in the specified directory.
     * @param directoryPath The path to the directory containing the files to be deleted.
     */
    public void deleteFiles(String directoryPath) {
        if (isNullOrEmpty(directoryPath)) {
            System.out.println("Invalid directory path.");
            return;
        }
        File directory = new File(directoryPath);
        if (!validateDirectory(directory)) {
            return;
        }
        File[] files = directory.listFiles();
        if (isEmptyDirectory(files)) {
            return;
        }
        deleteFilesInDirectory(files);
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * checks if directory exists and is directory itself
     * @param directory directory to check
     * @return true if exists and directory, otherwise false
     */
    private boolean validateDirectory(File directory) {
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Specified directory does not exist or is not a directory.");
            return false;
        }
        return true;
    }

    /**
     * check if directory isn't empty
     */
    private boolean isEmptyDirectory(File[] files) {
        // Check if the directory is empty or contains no files
        if (files == null || files.length == 0) {
            System.out.println("There are no files inside the specified directory.");
            return true;
        }
        return false;
    }

    /**
     * iterate all files and call method to delete only .json files
     */
    private void deleteFilesInDirectory(File[] files) {
        for (File file : files) {
            deleteFile(file);
        }
    }

    /**
     * check if file .json, if yes -> deletes it.
     * @param file file to delete
     */
    private void deleteFile(File file) {
        if (file.isFile() && file.getName().endsWith(".json")) {
            boolean deleted = file.delete();
            if (deleted) {
                System.out.println("Deleted file: " + file.getName());
            } else {
                System.out.println("Failed to delete file: " + file.getName());
            }
        }
    }
}
