package com.mesi.scipower;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;

public class RefTest {
    private static void preProcess() {
        String ref;
        try (BufferedReader reader = new BufferedReader(new FileReader("/Users/nikol/Desktop/ref.txt"));
             BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/nikol/Desktop/ref-process.txt"))) {

            while ((ref = reader.readLine()) != null) {
                if (!ref.isEmpty()) {
                    for (String refInner : ref.split("; ")) writer.write(refInner + "\n");
                }
            }

            writer.flush();
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
    }

    private static void componentLength() {
        String ref;
        try (BufferedReader reader = new BufferedReader(new FileReader("/Users/nikol/Desktop/ref-process.txt"));
             BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/nikol/Desktop/ref-comp-length.txt"))) {

            while((ref = reader.readLine()) != null) {
                String[] refComponent = ref.split(", ");
                String refTitle = Arrays.stream(refComponent).max(Comparator.comparing(String::length)).orElse(null);
                writer.write(refTitle + "\n");
            }

            writer.flush();
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
    }

    public static void main(String[] args) {
         componentLength();
    }
}
