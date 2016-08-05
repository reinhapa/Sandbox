package net.reini;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeExamples {
  public static void main(String[] args) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM uuuu HH:mm:ss");
    LocalDateTime dateTime = LocalDateTime.parse("01 02 1903 12:34:56", formatter);
    System.out.println(dateTime);
    ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.of("CET"));
    System.out.println(zonedDateTime);
  }
}
