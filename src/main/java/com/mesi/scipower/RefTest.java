package com.mesi.scipower;

import java.io.*;

public class RefTest {
    private static void tempProcess() {
        String ref;
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/ref.txt"));
             BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/ref-process.txt"))) {
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

    public static void main(String[] args) {
         tempProcess();
    }
}
