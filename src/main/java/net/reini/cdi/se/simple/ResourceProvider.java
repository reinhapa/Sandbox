/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2020 Patrick Reinhart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.reini.cdi.se.simple;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import javax.sql.DataSource;
import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.Synchronization;
import jakarta.transaction.SystemException;
import jakarta.transaction.Transaction;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.TransactionSynchronizationRegistry;

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

  @Default
  @Produces
  @ResourceQualifier
  public TransactionSynchronizationRegistry provideTransactionSynchronizationRegistry(
      InjectionPoint ip) {
    return new TransactionSynchronizationRegistry() {
      @Override
      public void setRollbackOnly() {
      }

      @Override
      public void registerInterposedSynchronization(Synchronization sync) {
      }

      @Override
      public void putResource(Object key, Object value) {
      }

      @Override
      public int getTransactionStatus() {
        return 0;
      }

      @Override
      public Object getTransactionKey() {
        return null;
      }

      @Override
      public boolean getRollbackOnly() {
        return false;
      }

      @Override
      public Object getResource(Object key) {
        return null;
      }

      @Override
      public String toString() {
        return "MyTestTransactionSynchronizationRegistry";
      }
    };
  }

}
