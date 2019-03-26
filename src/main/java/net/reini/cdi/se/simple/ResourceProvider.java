/**
 * File Name: ResourceProvider.java
 * 
 * Copyright (c) 2019 BISON Schweiz AG, All Rights Reserved.
 */

package net.reini.cdi.se.simple;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.sql.DataSource;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
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
    return new DataSource() {
      @Override
      public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
      }

      @Override
      public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
      }

      @Override
      public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
      }

      @Override
      public void setLoginTimeout(int seconds) throws SQLException {
      }

      @Override
      public void setLogWriter(PrintWriter out) throws SQLException {
      }

      @Override
      public int getLoginTimeout() throws SQLException {
        return 0;
      }

      @Override
      public PrintWriter getLogWriter() throws SQLException {
        return null;
      }

      @Override
      public Connection getConnection(String username, String password) throws SQLException {
        return null;
      }

      @Override
      public Connection getConnection() throws SQLException {
        return null;
      }

      @Override
      public String toString() {
        return "MyTestDataSource";
      }
    };
  }

  @Default
  @Produces
  @ResourceQualifier
  public TransactionManager provideTransactionManager(InjectionPoint ip) {
    System.out.println("provideTransactionManager to " + ip);
    return new TransactionManager() {
      @Override
      public Transaction suspend() throws SystemException {
        return null;
      }

      @Override
      public void setTransactionTimeout(int seconds) throws SystemException {
      }

      @Override
      public void setRollbackOnly() throws IllegalStateException, SystemException {
      }

      @Override
      public void rollback() throws IllegalStateException, SecurityException, SystemException {
      }

      @Override
      public void resume(Transaction tobj)
          throws InvalidTransactionException, IllegalStateException, SystemException {
      }

      @Override
      public Transaction getTransaction() throws SystemException {
        return null;
      }

      @Override
      public int getStatus() throws SystemException {
        return 0;
      }

      @Override
      public void commit() throws RollbackException, HeuristicMixedException,
          HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
      }

      @Override
      public void begin() throws NotSupportedException, SystemException {
      }

      @Override
      public String toString() {
        return "MyTestTransactionManager";
      }
    };
  }
}
