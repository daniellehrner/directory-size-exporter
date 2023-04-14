package me.lehrner.directorsizeexporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.Undertow;
import io.undertow.util.Headers;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.out.println("No config file specified. Usage java directory-size-exporter <config-file>");
      System.exit(1);
    }

    final Config config = objectMapper.readValue(new File(args[0]), Config.class);

    final Undertow server = Undertow.builder()
        .addHttpListener(9101, "localhost")
        .setHandler(exchange -> {
          if (!exchange.getRequestURI().equals("/metrics")) {
            exchange.setStatusCode(404);
            exchange.getResponseSender().send("Not found");
            return;
          }

          exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");

          final StringBuilder response = new StringBuilder("# directory size exporter for prometheus\n\n");

          for (File directory: config.directories()) {
            response
                .append("directory_size_bytes{directory=\"")
                .append(directory).append("\"} ")
                .append(FileUtils.sizeOfDirectory(directory))
                .append("\n");
          }

          response.append("\n");

          exchange.getResponseSender().send(response.toString());
        }).build();
    server.start();
  }

  private record Config(List<File> directories) {}
}