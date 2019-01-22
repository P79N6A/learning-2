package wang.xiaoluobo.phoenix;

import org.springframework.boot.SpringApplication;

import javax.annotation.PostConstruct;
import java.sql.*;

/**
 * @author wangyd
 * @date 2019/1/22
 */
public class PhoenixApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhoenixApplication.class, args);
    }

    @PostConstruct
    public void start() {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
            conn = DriverManager.getConnection("jdbc:phoenix:bigdata01:2181");
            preparedStatement = conn.prepareStatement("create table test_phoenix(mykey integer not null primary key, mycol varchar)");
            preparedStatement.executeUpdate();
            conn.commit();

            preparedStatement.executeUpdate("upsert into test_phoenix values(1, 'test_phoenix1')");
            preparedStatement.executeUpdate("upsert into test_phoenix values(2, 'test_phoenix2')");
            preparedStatement.executeUpdate("upsert into test_phoenix values(3, 'test_phoenix3')");
            conn.commit();

            preparedStatement.executeUpdate("delete from test_phoenix where mykey = 1");
            conn.commit();

            resultSet = preparedStatement.executeQuery("select * from test_phoenix");

            while (resultSet.next()) {
                String key = resultSet.getString("mykey");
                String col = resultSet.getString("mycol");
                System.out.println(String.format("mykey=%s, mycol=%s", key, col));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }

                if (preparedStatement != null) {
                    preparedStatement.close();
                }

                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
