package com.mesi.scipower;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class RefTest {

    private static void refPreProcess() {
        try (BufferedReader reader = new BufferedReader(new FileReader("/Users/nikol/Desktop/ref.txt"));
             BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/nikol/Desktop/ref-process.txt"))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    for (String ref : line.split("; ")) writer.write(ref + "\n");
                }
            }

            writer.flush();
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
    }

    private static void findRefTitle() {
        try (BufferedReader reader = new BufferedReader(new FileReader("/Users/nikol/Desktop/ref-process.txt"));
             BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/nikol/Desktop/ref-title.txt"))) {
            String line;

            while((line = reader.readLine()) != null) {
                String[] refComponent = line.split(", ");
                String refTitle = Arrays.stream(refComponent).max(Comparator.comparing(String::length)).orElse(null);
                writer.write(refTitle + "\n");
            }

            writer.flush();
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
    }

    private static void processKewWords() {
        try (BufferedReader reader = new BufferedReader(new FileReader("/Users/nikol/Desktop/kw.txt"));
             BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/nikol/Desktop/kw-process.txt"))) {
            String line;

            Set<String> kwSet = new HashSet<>();
            while((line = reader.readLine()) != null) {
                String[] kwArray = line.split("; ");
                for (String kw : kwArray) kwSet.add(kw.toLowerCase());
            }

            for (String kw : kwSet) writer.write(kw + "\n");
            writer.flush();

        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            String processType = args[0].split("=")[1];

            switch (processType) {
                case "kw" -> {
                    System.out.println("Pre-process: \"/Users/nikol/Desktop/kw.txt\"");
                    System.out.println("Output: \"/Users/nikol/Desktop/kw-process.txt\"");
                    processKewWords();
                }
                case "ref" -> {
                    String stageType = args[1].split("=")[1];

                    switch (stageType) {
                        case "pre" -> {
                            System.out.println("Pre-process: \"/Users/nikol/Desktop/ref.txt\"");
                            System.out.println("Output: \"/Users/nikol/Desktop/ref-process.txt\"");
                            refPreProcess();
                        } case "title" -> {
                            System.out.println("Component-length: \"/Users/nikol/Desktop/ref-process.txt\"");
                            System.out.println("Output: \"/Users/nikol/Desktop/ref-title.txt\"");
                            findRefTitle();
                        }
                        default -> System.err.println("Unknown process stage");
                    }
                }
                default -> System.err.println("Unknown process type");
            }
        }
    }

}
