/**
 * File Name: ResourceProvider.java
 * 
 * Copyright (c) 2019 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.cdi.se.simple;

import static org.easymock.EasyMock.createMock;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;

@ApplicationScoped
public class ResourceProvider {

  public ResourceProvider() {
    System.out.println("init net.reini.cdi.se.TestResourceInjectionServices");
  }

  @Produces
  @ResourceQualifier(lookup = "java:global/env/jdbc/CustomerDatasource")
  public DataSource provideDataSource(InjectionPoint ip) {
    System.out.println("provideDataSource to " + ip);
    return createMock(DataSource.class);
  }

  @Default
  @Produces
  @ResourceQualifier
  public TransactionManager provideTransactionManager(InjectionPoint ip) {
    System.out.println("provideTransactionManager to " + ip);
    return createMock(TransactionManager.class);
  }
}
