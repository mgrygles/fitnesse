// Copyright (C) 2003-2009 by Object Mentor, Inc. All rights reserved.
// Released under the terms of the CPL Common Public License version 1.0.
package fitnesse.testutil;

import fitnesse.FitNesse;
import fitnesse.FitNesseContext;
import fitnesse.FitNesseContext.Builder;
import fitnesse.authentication.Authenticator;
import fitnesse.authentication.PromiscuousAuthenticator;
import fitnesse.testrunner.MultipleTestSystemFactory;
import fitnesse.testsystems.slim.CustomComparatorRegistry;
import fitnesse.testsystems.slim.tables.SlimTableFactory;
import fitnesse.wiki.RecentChangesWikiPage;
import fitnesse.wiki.fs.ZipFileVersionsController;
import fitnesse.wiki.mem.InMemoryPage;
import fitnesse.wiki.WikiPage;
import util.FileUtil;

import java.io.IOException;
import java.util.Properties;

public class FitNesseUtil {
  public static final String base = "TestDir";
  public static final int PORT = 1999;
  public static final String URL = "http://localhost:" + PORT + "/";

  private static FitNesse instance = null;

  public static FitNesseContext startFitnesse(WikiPage root) {
    FitNesseContext context = makeTestContext(root);
    startFitnesseWithContext(context);
    return context;
  }

  public static void startFitnesseWithContext(FitNesseContext context) {
    instance = context.fitNesse;
    instance.start();
  }

  public static void stopFitnesse() throws IOException {
    instance.stop();
    FileUtil.deleteFileSystemDirectory("TestDir");
  }

  public static FitNesseContext makeTestContext() {
    Properties properties = new Properties();
    properties.setProperty("FITNESSE_PORT", String.valueOf(PORT));
    return makeTestContext(InMemoryPage.makeRoot("RooT", properties));
  }

  public static FitNesseContext makeTestContext(WikiPage root) {
    return makeTestContext(root, PORT);
  }

  public static FitNesseContext makeTestContext(int port) {
    return makeTestContext(InMemoryPage.makeRoot("root"), port);
  }

  public static FitNesseContext makeTestContext(WikiPage root, int port) {
    return makeTestContext(root, ".", FitNesseUtil.base, port, new PromiscuousAuthenticator());
  }

  public static FitNesseContext makeTestContext(WikiPage root,
      Authenticator authenticator) {
    return makeTestContext(root, ".", FitNesseUtil.base, PORT, authenticator);
  }

  public static FitNesseContext makeTestContext(WikiPage root, int port,
      Authenticator authenticator) {
    return makeTestContext(root, ".", FitNesseUtil.base, port, authenticator);
  }



  public static FitNesseContext makeTestContext(WikiPage root, String rootPath,
      String rootDirectoryName, int port) {
    return makeTestContext(root, rootPath, rootDirectoryName, port, null);
  }

  public static FitNesseContext makeTestContext(WikiPage root, String rootPath,
      String rootDirectoryName, int port, Authenticator authenticator) {
    Builder builder = new Builder();
    builder.root = root;
    builder.rootPath = rootPath;
    builder.rootDirectoryName = rootDirectoryName;
    builder.port = port;
    builder.authenticator = authenticator;
    builder.versionsController = new ZipFileVersionsController();
    builder.recentChanges = new RecentChangesWikiPage();
    builder.testSystemFactory = new MultipleTestSystemFactory(new SlimTableFactory(), new CustomComparatorRegistry());
    builder.properties = new Properties();
    FitNesseContext context = builder.createFitNesseContext();

    // Ensure Velocity is configured with the default root directory name (FitNesseRoot)
    context.pageFactory.getVelocityEngine();
    return context;
  }

  public static void destroyTestContext() {
    FileUtil.deleteFileSystemDirectory("TestDir");
  }

}
