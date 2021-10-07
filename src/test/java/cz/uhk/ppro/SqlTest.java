package cz.uhk.ppro;

import com.mysql.jdbc.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlTest {

    public static void main(String [] args) throws Exception {
        // Class.forName( "com.mysql.jdbc.Driver" ); // do this in init
        // // edit the jdbc url
        Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/ppro?user=root&password=2172/fomo");
        // Statement st = conn.createStatement();
        // ResultSet rs = st.executeQuery( "select * from table" );

        System.out.println("Connected?");
    }
}
