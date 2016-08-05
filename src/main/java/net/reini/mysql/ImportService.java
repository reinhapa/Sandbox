package net.reini.mysql;

import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.size;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.zip.GZIPInputStream;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

final class ImportService extends Service<Boolean> {
  private final StringProperty fileName;
  private final StringProperty jdbcUrl;
  private final StringProperty jdbcUser;
  private final StringProperty jdbcPassword;

  ImportService() {
    fileName = new SimpleStringProperty();
    jdbcUrl = new SimpleStringProperty();
    jdbcUser = new SimpleStringProperty();
    jdbcPassword = new SimpleStringProperty();
  }

  StringProperty fileNameProperty() {
    return fileName;
  }

  StringProperty jdbcUrlProperty() {
    return jdbcUrl;
  }

  StringProperty jdbcUserProperty() {
    return jdbcUser;
  }

  StringProperty jdbcPasswordProperty() {
    return jdbcPassword;
  }

  @Override
  protected Task<Boolean> createTask() {
    return new ImportTask();
  }

  final class ImportTask extends Task<Boolean> {
    long max;
    long work;

    @Override
    protected Boolean call() throws Exception {
      final Path sqlDumpFile = Paths.get(fileName.get());
      max = size(sqlDumpFile);
      try (InputStream in = newInputStream(sqlDumpFile);
          BufferedReader br = new BufferedReader(new InputStreamReader(
              new GZIPInputStream(new CountingInputStream(in, this::updateProgress)),
              StandardCharsets.UTF_8));
          Connection con =
              DriverManager.getConnection(jdbcUrl.get(), jdbcUser.get(), jdbcPassword.get());
          Statement stmt = con.createStatement()) {
        // set max_allowed_packet=1000000000
        br.lines().filter(line -> !line.isEmpty() && !line.startsWith("--"))
            .forEach(new ImportAction(stmt, this::processed));
      } catch (Exception e) {
        updateMessage("Import failed: " + e.getMessage());
        return Boolean.FALSE;
      }
      return Boolean.TRUE;
    }

    private void updateProgress(int read) {
      work += read;
      updateProgress(work, max);
    }

    private void processed(int stmtNumber, String statement, Exception error) {
      updateMessage("Processed statements: " + stmtNumber);
      if (error != null) {
        System.err.println("Unable to execute:\n" + statement);
        error.printStackTrace();
      }
    }
  }
}
