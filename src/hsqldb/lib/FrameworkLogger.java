/* Copyright (c) 2001-2010, The HSQL Development Group
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the HSQL Development Group nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL HSQL DEVELOPMENT GROUP, HSQLDB.ORG,
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package hsqldb.lib;

import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;
import java.util.logging.LogManager;
import java.util.Map;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.IOException;
import java.util.HashMap;
import java.lang.reflect.Method;

/**
 * A logging framework wrapper that supports java.util.logging and log4j.
 * <P/>
 * Logger hierarchies are stored at the Class level.
 * Log4j will be used if the Log4j system (not necessarily config files) are
 * found in the runtime classpath.
 * Otherwise, java.util.logging will be used.
 * <P/>
 * This is pretty safe because for use cases where multiple hierarchies
 * are desired, classloader hierarchies will effectively isolate multiple
 * class-level Logger hierarchies.
 * <P/>
 * Sad as it is, the java.util.logging facility lacks the most basic
 * developer-side and configuration-side capabilities.
 * Besides having a non-scalable discovery system, the designers didn't
 * comprehend the need for a level between WARNING and SEVERE!
 * Since we don't want to require log4j in Classpath, we have to live
 * with these constraints.
 * <P/>
 * As with all the popular logging frameworks, if you want to capture a
 * stack trace, you must use the two-parameters logging methods.
 * I.e., you must also pass a String, or only toString() from your
 * throwable will be captured.
 * <P/>
 * Usage example:<CODE><PRE>
 * private static FrameworkLogger logger =
 *        FrameworkLogger.getLog(SqlTool.class);
 * ...
 *   logger.finer("Doing something log-worthy");
 * </PRE> </CODE>
 *
 * @author Blaine Simpson (blaine dot simpson at admc dot com)
 * @version 1.9.0
 * @since 1.9.0
 */
public class FrameworkLogger {

    static private Map     loggerInstances  = new HashMap();
    static private Map     jdkToLog4jLevels = new HashMap();
    static private Method  log4jGetLogger;
    static private Method  log4jLogMethod;
    private Object         log4jLogger;
    private Logger         jdkLogger;
    static private boolean haveLoadedOurDefault;
    static private ConsoleHandler  consoleHandler = new ConsoleHandler();
    // No need for more than one static, since we have only one console

    static {
        reconfigure();
    }

    static void reconfigure() {
        Class log4jLoggerClass = null;
        loggerInstances.clear();

        try {
            log4jLoggerClass = Class.forName("org.apache.log4j.Logger");
        } catch (Exception e) {
            log4jLoggerClass = null;
        }

        if (log4jLoggerClass == null) try {
            log4jGetLogger = null;
            log4jLogMethod = null;
            LogManager lm = LogManager.getLogManager();
            if (haveLoadedOurDefault || isDefaultJdkConfig()) {
                haveLoadedOurDefault = true;
                consoleHandler.setFormatter(
                        new BasicTextJdkLogFormatter(false));
                consoleHandler.setLevel(Level.INFO);
                lm.readConfiguration(
                        FrameworkLogger.class.getResourceAsStream(
                        "/org/hsqldb/resources/jdklogging-default.properties"));
                Logger cmdlineLogger = Logger.getLogger("org.hsqldb.cmdline");
                cmdlineLogger.addHandler(consoleHandler);
                cmdlineLogger.setUseParentHandlers(false);
            } else {
                // Do not intervene.  Use JDK logging exactly as configured by
                // user.
                lm.readConfiguration();
                // The only bad thing about doing this is that if the app has
                // programmatically changed the logging config after starting
                // the program but before using FrameworkLogger, we will
                // clobber those customizations.
            }
        } catch (Exception e) {
            throw new RuntimeException(
                "<clinit> failure initializing JDK logging system", e);
        } else try {
            Method log4jToLevel = Class.forName(
                "org.apache.log4j.Level").getMethod(
                "toLevel", new Class[]{ String.class });

            jdkToLog4jLevels.put(Level.ALL,
                                 log4jToLevel.invoke(null,
                                     new Object[]{ "ALL" }));
            jdkToLog4jLevels.put(Level.FINER,
                                 log4jToLevel.invoke(null,
                                     new Object[]{ "DEBUG" }));
            jdkToLog4jLevels.put(Level.WARNING,
                                 log4jToLevel.invoke(null,
                                     new Object[]{ "ERROR" }));
            jdkToLog4jLevels.put(Level.SEVERE,
                                 log4jToLevel.invoke(null,
                                     new Object[]{ "FATAL" }));
            jdkToLog4jLevels.put(Level.INFO,
                                 log4jToLevel.invoke(null,
                                     new Object[]{ "INFO" }));
            jdkToLog4jLevels.put(Level.OFF,
                                 log4jToLevel.invoke(null,
                                     new Object[]{ "OFF" }));
            jdkToLog4jLevels.put(Level.FINEST,
                                 log4jToLevel.invoke(null,
                                     new Object[]{ "TRACE" }));
            jdkToLog4jLevels.put(Level.WARNING,
                                 log4jToLevel.invoke(null,
                                     new Object[]{ "WARN" }));

            log4jLogMethod = log4jLoggerClass.getMethod("log",
                    new Class[] {
                String.class, Class.forName("org.apache.log4j.Priority"),
                Object.class, Throwable.class
            });

            log4jGetLogger = log4jLoggerClass.getMethod("getLogger",
                    new Class[]{ String.class });

            // This last object is what we toggle on to generate either
            // Log4j or Jdk Logger objects (to wrap).
        } catch (Exception e) {
            throw new RuntimeException(
                "<clinit> failure instantiating present Log4j system", e);
        }
    }

    /**
     * User may not use the constructor.
     */
    private FrameworkLogger(String s) {

        if (log4jGetLogger == null) {
            jdkLogger = Logger.getLogger(s);
        } else {
            try {
                log4jLogger = log4jGetLogger.invoke(null, new Object[]{ s });
            } catch (Exception e) {
                throw new RuntimeException(
                    "Failed to instantiate Log4j Logger", e);
            }
        }

        loggerInstances.put(s, this);
    }

    /**
     * User's entry-point into this logging system.
     * <P/>
     * You normally want to work with static (class-level) pointers to
     * logger instances, for performance efficiency.
     * See the class-level JavaDoc for a usage example.
     *
     * @see FrameworkLogger
     */
    public static FrameworkLogger getLog(Class c) {
        return getLog(c.getName());
    }

    /**
     * This method just defers to the getLog(Class) method unless default
     * (no local configuration) JDK logging is being used;
     * In that case, this method assures that the returned logger has an
     * associated FileHander using the supplied String identifier.
     */
    public static FrameworkLogger getLog(Class c, String contextId) {
        return (contextId == null)
                ?  getLog(c)
                :  getLog(contextId + '.' + c.getName());
    }

    /**
     * Alternative entry-point into this logging sytem, for cases where
     * your want to share a single logger instance among multiple classes,
     * or you want to use multiple logger instances from a single class.
     *
     * @see #getLog(Class)
     */
    public static FrameworkLogger getLog(String s) {

        if (loggerInstances.containsKey(s)) {
            return (FrameworkLogger) loggerInstances.get(s);
        }

        return new FrameworkLogger(s);
    }

    /**
     * Just like FrameworkLogger.log(Level, String),
     * but also logs a stack trace.
     *
     * @param level java.util.logging.Level level to filter and log at
     * @param message Message to be logged
     * @param t Throwable whose stack trace will be logged.
     * @see #log(Level, String)
     * @see Logger#log(Level, String)
     * @see Level
     */
    public void log(Level level, String message, Throwable t) {
        privlog(level, message, t, 2, FrameworkLogger.class);
    }

    /**
     * The "priv" prefix is historical.
     * This is for special usage when you need to modify the reported call
     * stack.
     * If you don't know that you want to do this, then you should not use
     * this method.
     */
    public void privlog(Level level, String message,
    Throwable t, int revertMethods, Class skipClass) {

        if (log4jLogger == null) {
            StackTraceElement elements[] = new Throwable().getStackTrace();
            String            c = elements[revertMethods].getClassName();
            String            m = elements[revertMethods].getMethodName();

            if (t == null) {
                jdkLogger.logp(level, c, m, message);
            } else {
                jdkLogger.logp(level, c, m, message, t);
            }
        } else {
            try {
                log4jLogMethod.invoke(log4jLogger, new Object[] {
                    skipClass.getName(), jdkToLog4jLevels.get(level), message,
                    t
                });
            } catch (Exception e) {
                throw new RuntimeException(
                    "Logging failed when attempting to log: " + message, e);
            }
        }
    }

    public void enduserlog(Level level, String message) {
        /* This method is SqlTool-specific, which is where this class began at.
         * Need to move this back there, but it needs access to the logging
         * structures private to this class.  Thinking...
         */

        if (log4jLogger == null) {
            String c = FrameworkLogger.class.getName();
            String m = "\\l";

            jdkLogger.logp(level, c, m, message);
        } else {
            try {
                log4jLogMethod.invoke(log4jLogger, new Object[] {
                    FrameworkLogger.class.getName(),
                    jdkToLog4jLevels.get(level),
                    message, null
                });

                // Test where SqlFile correct here.
            } catch (Exception e) {
                throw new RuntimeException(
                    "Logging failed when attempting to log: " + message, e);
            }
        }
    }

    // Wrappers

    /**
     * @param level java.util.logging.Level level to filter and log at
     * @param message Message to be logged
     * @see Logger#log(Level, String)
     * @see Level
     */
    public void log(Level level, String message) {
        privlog(level, message, null, 2, FrameworkLogger.class);
    }

    /**
     * @param message Message to be logged
     * @see Logger#finer(String)
     */
    public void finer(String message) {
        privlog(Level.FINER, message, null, 2, FrameworkLogger.class);
    }

    /**
     * @param message Message to be logged
     * @see Logger#warning(String)
     */
    public void warning(String message) {
        privlog(Level.WARNING, message, null, 2, FrameworkLogger.class);
    }

    /**
     * @param message Message to be logged
     * @see Logger#severe(String)
     */
    public void severe(String message) {
        privlog(Level.SEVERE, message, null, 2, FrameworkLogger.class);
    }

    /**
     * @param message Message to be logged
     * @see Logger#info(String)
     */
    public void info(String message) {
        privlog(Level.INFO, message, null, 2, FrameworkLogger.class);
    }

    /**
     * @param message Message to be logged
     * @see Logger#finest(String)
     */
    public void finest(String message) {
        privlog(Level.FINEST, message, null, 2, FrameworkLogger.class);
    }

    /**
     * This is just a wrapper for FrameworkLogger.warning(), because
     * java.util.logging lacks a method for this critical purpose.
     *
     * @param message Message to be logged
     * @see #warning(String)
     */
    public void error(String message) {
        privlog(Level.WARNING, message, null, 2, FrameworkLogger.class);
    }

    /**
     * Just like FrameworkLogger.finer(String), but also logs a stack trace.
     *
     * @param t Throwable whose stack trace will be logged.
     * @see #finer(String)
     */
    public void finer(String message, Throwable t) {
        privlog(Level.FINER, message, t, 2, FrameworkLogger.class);
    }

    /**
     * Just like FrameworkLogger.warning(String), but also logs a stack trace.
     *
     * @param t Throwable whose stack trace will be logged.
     * @see #warning(String)
     */
    public void warning(String message, Throwable t) {
        privlog(Level.WARNING, message, t, 2, FrameworkLogger.class);
    }

    /**
     * Just like FrameworkLogger.severe(String), but also logs a stack trace.
     *
     * @param t Throwable whose stack trace will be logged.
     * @see #severe(String)
     */
    public void severe(String message, Throwable t) {
        privlog(Level.SEVERE, message, t, 2, FrameworkLogger.class);
    }

    /**
     * Just like FrameworkLogger.info(String), but also logs a stack trace.
     *
     * @param t Throwable whose stack trace will be logged.
     * @see #info(String)
     */
    public void info(String message, Throwable t) {
        privlog(Level.INFO, message, t, 2, FrameworkLogger.class);
    }

    /**
     * Just like FrameworkLogger.finest(String), but also logs a stack trace.
     *
     * @param t Throwable whose stack trace will be logged.
     * @see #finest(String)
     */
    public void finest(String message, Throwable t) {
        privlog(Level.FINEST, message, t, 2, FrameworkLogger.class);
    }

    /**
     * Just like FrameworkLogger.error(String), but also logs a stack trace.
     *
     * @param t Throwable whose stack trace will be logged.
     * @see #error(String)
     */
    public void error(String message, Throwable t) {
        privlog(Level.WARNING, message, t, 2, FrameworkLogger.class);
    }

    /**
     * Whether this JVM is configured with java.util.logging defaults.
     *
     * If the JRE-provided config file is not in the expected place, then
     * we return false.
     */
    static public boolean isDefaultJdkConfig() {
        File globalCfgFile = new File(System.getProperty("java.home"),
                "lib/logging.properties");
        if (!globalCfgFile.isFile()) {
            return false;
        }
        FileInputStream fis = null;
        LogManager lm = LogManager.getLogManager();
        try {
            fis = new FileInputStream(globalCfgFile);
            Properties defaultProps = new Properties();
            defaultProps.load(fis);
            Enumeration names = defaultProps.propertyNames();
            int i = 0;
            String name;
            String liveVal;
            while (names.hasMoreElements()) {
                i++;
                name = (String) names.nextElement();
                liveVal = lm.getProperty(name);
                if (liveVal == null) {
                    return false;
                }
                if (!lm.getProperty(name).equals(liveVal)) {
                    return false;
                }
            }
            return true;
        } catch (IOException ioe) {
            return false;
        } finally {
            if (fis != null) try {
                fis.close();
            } catch (IOException ioe) {
                // Intentional no-op
            }
        }
    }
}
