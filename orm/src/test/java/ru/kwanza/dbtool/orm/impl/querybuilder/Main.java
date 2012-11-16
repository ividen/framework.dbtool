package ru.kwanza.dbtool.orm.impl.querybuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * @author Alexander Guzanov
 */
public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        File f = new File("E:\\Framework\\dbtool\\orm\\src\\test\\resources\\ru\\kwanza\\dbtool\\orm\\impl\\fetcher\\initdb.xml");
        PrintWriter writer = new PrintWriter(f);
        int i;
        writer.println("<dataset>");
        for (i = 0; i < 1500; i++) {
            writer.println("<test_entity_a id=\"" + i + "\" title=\"" + "test_entity_a" + i + "\" version=\"0\"/>");
        }

        for (i = 1500; i < 3000; i++) {
            writer.println("<test_entity_b id=\"" + i + "\" title=\"" + "test_entity_b" + i + "\" version=\"0\"/>");
        }

        for (i = 3000; i < 4500; i++) {
            writer.println("<test_entity_d id=\"" + i + "\" title=\"" + "test_entity_d" + i + "\" version=\"0\"/>");
        }

        for (i = 4500; i < 6000; i++) {
            writer.println("<test_entity_f id=\"" + i + "\" title=\"" + "test_entity_f" + i + "\" version=\"0\"/>");
        }

        for (i = 6000; i < 7500; i++) {
            writer.println("<test_entity_g id=\"" + i + "\" title=\"" + "test_entity_g" + i + "\" version=\"0\"/>");
        }

        for (i = 7500; i < 9000; i++) {
            writer.println("<test_entity_e id=\"" + i + "\" title=\"" + "test_entity_e" + i + "\" version=\"0\" " +
                    "entity_gid=\"" + (i - 1500) + "\"/>");
        }

        for (i = 7500; i < 9000; i++) {
            writer.println("<test_entity_c id=\"" + i + "\" title=\"" + "test_entity_c" + i + "\" version=\"0\" " +
                    "entity_eid=\"" + (i - 1500) + "\"  entity_fid=\"" + (i - 3000) + "\" />");
        }


        writer.println("</dataset>");
    }
}
