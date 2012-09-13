package ru.kwanza.dbtool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Guzanov Alexander
 */
public class Main {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("com.inet.ora.OraDriver");
        Connection connection = DriverManager.getConnection("jdbc:inetpool:inetora:test02:1521:imap", "AGUZANOV_TESTER", "AGUZANOV_TESTER");
        Connection connection1 = DriverManager.getConnection("jdbc:inetpool:inetora:test02:1521:imap", "AGUZANOV_TESTER", "AGUZANOV_TESTER");
        Connection connection2 = DriverManager.getConnection("jdbc:inetpool:inetora:test02:1521:imap", "AGUZANOV_TESTER", "AGUZANOV_TESTER");
        Connection connection3 = DriverManager.getConnection("jdbc:inetpool:inetora:test02:1521:imap", "AGUZANOV_TESTER", "AGUZANOV_TESTER");
        Connection connection4 = DriverManager.getConnection("jdbc:inetpool:inetora:test02:1521:imap", "AGUZANOV_TESTER", "AGUZANOV_TESTER");
        Connection connection5 = DriverManager.getConnection("jdbc:inetpool:inetora:test02:1521:imap", "AGUZANOV_TESTER", "AGUZANOV_TESTER");
        Connection connection6 = DriverManager.getConnection("jdbc:inetpool:inetora:test02:1521:imap", "AGUZANOV_TESTER", "AGUZANOV_TESTER");
        Connection connection7 = DriverManager.getConnection("jdbc:inetpool:inetora:test02:1521:imap", "AGUZANOV_TESTER", "AGUZANOV_TESTER");
        Connection connection8 = DriverManager.getConnection("jdbc:inetpool:inetora:test02:1521:imap", "AGUZANOV_TESTER", "AGUZANOV_TESTER");
        Connection connection9 = DriverManager.getConnection("jdbc:inetpool:inetora:test02:1521:imap", "AGUZANOV_TESTER", "AGUZANOV_TESTER");
        Connection connection10 = DriverManager.getConnection("jdbc:inetpool:inetora:test02:1521:imap", "AGUZANOV_TESTER", "AGUZANOV_TESTER");
        Connection connection11 = DriverManager.getConnection("jdbc:inetpool:inetora:test02:1521:imap", "AGUZANOV_TESTER", "AGUZANOV_TESTER");
        Connection connection12 = DriverManager.getConnection("jdbc:inetpool:inetora:test02:1521:imap", "AGUZANOV_TESTER", "AGUZANOV_TESTER");
        Connection connection13 = DriverManager.getConnection("jdbc:inetpool:inetora:test02:1521:imap", "AGUZANOV_TESTER", "AGUZANOV_TESTER");
    }
}
