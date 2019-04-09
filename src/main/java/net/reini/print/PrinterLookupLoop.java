package net.reini.print;
import java.io.IOException;
import java.util.Date;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

/**
 * File Name: PrinterLookupLoop.java
 * 
 * Copyright (c) 2017 BISON Schweiz AG, All Rights Reserved.
 */
public class PrinterLookupLoop {

  public static void main(String[] args) {
    try {
      while (System.in.available() == 0) {
        System.out.println(new Date() + " printers:");
        for (PrintService ps : PrintServiceLookup.lookupPrintServices(null, null)) {
          System.out.println(ps.getName());
        }
        System.out.println();
        System.out.println("Hit enter to stop...");
        Thread.sleep(1000);
        System.out.println();
      }
    } catch (IOException | InterruptedException e) {
      // ignore errors
    }
  }

}
