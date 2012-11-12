package ru.kwanza.dbtool.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Guzanov Alexander
 */
public class GenerateUtil {
    public static void main(String[] args) throws IOException {
        String fileName = "E:/data.xml";
        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        PrintWriter writer = new PrintWriter(fileOutputStream);

        for (int i = 0; i < 5000; i++) {

            writer.println(" <test_table key=\"" + i + "\" name=\"n_" + i / 3 + "\" version=\"0\"/>");

        }

        writer.close();
        fileOutputStream.close();
    }
}
