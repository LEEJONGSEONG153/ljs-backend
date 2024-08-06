package com.ljs.ljsbackend.common.util;

import java.io.IOException;

public class HeicConverter {
    public static void convertHeicToJpg(String heicFilePath, String jpgFilePath) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("magick", "convert", heicFilePath, jpgFilePath);

        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("HEIC to JPG conversion failed!");
        }
    }

    public static void main(String[] args) {
        String heicFilePath = "path/to/input.heic";
        String jpgFilePath = "path/to/output.jpg";

        try {
            convertHeicToJpg(heicFilePath, jpgFilePath);
            System.out.println("Conversion successful!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
