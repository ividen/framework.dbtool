package ru.kwanza.dbtool.orm.impl.querybuilder;

/**
 * @author Alexander Guzanov
 */
public class Main {

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println("<test_entity id=\"" + i + "\" int_field=\"10\" string_field=\"asdfadfadf\" date_field=\"2012-01-01 10:00:00.0\" short_field=\"1\" version=\"0\" entity_aid=\"1\" entity_bid=\"1\" entity_cid=\"1\" entity_did=\"1\"/>");
        }

        for (int i = 0; i < 100; i++) {
            System.out.println("<test_entity id=\"" + i+1000 + "\" int_field=\"20\" string_field=\"asdfadfadf\" date_field=\"2012-01-02 20:00:00.0\" short_field=\"1\" version=\"0\" entity_aid=\"1\" entity_bid=\"1\" entity_cid=\"1\" entity_did=\"1\"/>");
        }

    }
}
