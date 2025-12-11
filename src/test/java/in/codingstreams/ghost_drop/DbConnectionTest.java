package in.codingstreams.ghost_drop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class DbConnectionTest {
  @Autowired
  private DataSource dataSource;

  @Test
  void testConnection() throws SQLException{
    assertThat(dataSource).isNotNull();

    try(var connection = dataSource.getConnection()){
      assertThat(connection.isValid(1)).isTrue();
      System.out.println("Connected to: " + connection.getMetaData().getDatabaseProductName());
    }
  }
}
