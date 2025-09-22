/*
 * Copyright (c) 2005, 2024, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package javx.tools;

/**
 * Provides methods for locating tool providers, for example, providers of compilers. This class
 * complements the functionality of {@link java.util.ServiceLoader}.
 *
 * @since 1.6
 */
public class ToolProvider {

  private static final String systemJavaCompilerModule = "jdk.compiler";
  private static final String systemJavaCompilerName = "com.sun.tools.javac.api.JavacTool";

  private ToolProvider() {}

  /**
   * Returns the Java programming language compiler provided with this platform.
   *
   * <p>The file manager returned by calling {@link JavaCompiler#getStandardFileManager
   * getStandardFileManager} on this compiler supports paths provided by any {@linkplain
   * java.nio.file.FileSystem filesystem}.
   *
   * @return the compiler provided with this platform or {@code null} if no compiler is provided
   * @implNote This implementation returns the compiler provided by the {@code jdk.compiler} module
   *     if that module is available, and {@code null} otherwise.
   */
  public static JavaCompiler getSystemJavaCompiler() {
    return getSystemTool(JavaCompiler.class, systemJavaCompilerModule, systemJavaCompilerName);
  }

   // deenu modify: add new method
  public static JavaCompiler getSystemJavaCompiler(ClassLoader classLoader) {
    return getSystemTool(JavaCompiler.class, systemJavaCompilerModule, systemJavaCompilerName, classLoader);
  }

  private static final String systemDocumentationToolModule = "jdk.javadoc";
  private static final String systemDocumentationToolName = "jdk.javadoc.internal.api.JavadocTool";

  /**
   * Returns the Java programming language documentation tool provided with this platform.
   *
   * <p>The file manager returned by calling {@link DocumentationTool#getStandardFileManager
   * getStandardFileManager} on this tool supports paths provided by any {@linkplain
   * java.nio.file.FileSystem filesystem}.
   *
   * @return the documentation tool provided with this platform or {@code null} if no documentation
   *     tool is provided
   * @implNote This implementation returns the tool provided by the {@code jdk.javadoc} module if
   *     that module is available, and {@code null} otherwise.
   */
  public static DocumentationTool getSystemDocumentationTool() {
    return getSystemTool(
        DocumentationTool.class, systemDocumentationToolModule, systemDocumentationToolName);
  }

  /**
   * Returns a class loader that may be used to load system tools, or {@code null} if no such
   * special loader is provided.
   *
   * @implSpec This implementation always returns {@code null}.
   * @deprecated This method is subject to removal in a future version of Java SE. Use the
   *     {@linkplain java.util.spi.ToolProvider system tool provider} or {@linkplain
   *     java.util.ServiceLoader service loader} mechanisms to locate system tools as well as
   *     user-installed tools.
   * @return a class loader, or {@code null}
   */
  @Deprecated(since = "9")
  public static ClassLoader getSystemToolClassLoader() {
    return null;
  }

  /**
   * Get an instance of a system tool using the service loader.
   *
   * @implNote By default, this returns the implementation in the specified module. For limited
   *     backward compatibility, if this code is run on an older version of the Java platform that
   *     does not support modules, this method will try and create an instance of the named class.
   *     Note that implies the class must be available on the system class path.
   * @param <T> the interface of the tool
   * @param clazz the interface of the tool
   * @param moduleName the name of the module containing the desired implementation
   * @param className the class name of the desired implementation
   * @return the specified implementation of the tool
   */
  private static <T> T getSystemTool(Class<T> clazz, String moduleName, String className) {

    try {
      // deenu modify: return newInstance
      /* ServiceLoader<T> sl = ServiceLoader.load(clazz, ClassLoader.getSystemClassLoader());
          for (T tool : sl) {
              if (Objects.equals(tool.getClass().getModule().getName(), moduleName)) {
                  return tool;
              }
          }
      } catch (ServiceConfigurationError e) {*/
      return Class.forName(className, true, Thread.currentThread().getContextClassLoader())
          .asSubclass(clazz)
          .getConstructor()
          .newInstance();
    } catch (ReflectiveOperationException e) {
      throw new Error(e);
    }
    // return null;
  }
  
    // deenu modify: add new method
  private static <T> T getSystemTool(Class<T> clazz, String moduleName, String className, ClassLoader classLoader) {

    try {
      // deenu modify: return newInstance
      /* ServiceLoader<T> sl = ServiceLoader.load(clazz, ClassLoader.getSystemClassLoader());
          for (T tool : sl) {
              if (Objects.equals(tool.getClass().getModule().getName(), moduleName)) {
                  return tool;
              }
          }
      } catch (ServiceConfigurationError e) {*/
      return Class.forName(className, true, classLoader)
          .asSubclass(clazz)
          .getConstructor()
          .newInstance();
    } catch (ReflectiveOperationException e) {
      throw new Error(e);
    }
    // return null;
  }
}
