/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.gui.dialog.JModalDialog;
import com.mr.apps.JNordVpnManager.utils.String.StringFormat;

/**
 * Some common system related utilities
 */
public class UtilSystem
{
   private static final int    COMMAND_TIMEOUT    = UtilPrefs.getCommandTimeout();

   private static String       m_lastErrorMessage = null;
   private static int          m_lastExitCode   = 0;
   private static Process      m_process          = null;
   private static StringBuffer m_stdOut           = null;
   private static StringBuffer m_stdErr           = null;

   /**
    * Check error condition of last executed command
    * @return true if there is an active error, else false
    */
   public static boolean isLastError()
   {
      return (null == m_lastErrorMessage) ? false : true;
   }

   /**
    * Get the last error
    * <p>
    * This method resets the error.
    * @return the last error string (null if there was no error)
    */
   public static String getLastError()
   {
      String message = m_lastErrorMessage;
      m_lastErrorMessage = null;
      return message;
   }

   /**
    * Set the last error
    * <p>
    * This method sets the last error.
    * 
    * @param msg
    *           is the last error message (null if there was no error)
    * @param rc
    *           is the last error return code
    */
   public static void setLastError(String msg, int rc)
   {
      m_lastErrorMessage = msg;
      m_lastExitCode = rc;
   }

   /**
    * Utility to display a dialog with a command success or error return message
    * 
    * @param shortMsg
    *           is the dialog short message
    * @param msg
    *           is the dialog message
    * @return the last error code, 0 for ok
    */
   public static int showResultDialog(String shortMsg, String msg, boolean autoClose)
   {
      if (UtilSystem.isLastError())
      {
         JModalDialog.showError(shortMsg, UtilSystem.getLastError() + "\n" + StringFormat.printString (msg, "<empty message>", "<null message>"));
         return m_lastExitCode;
      }
      else if (autoClose)
      {
         JModalDialog.showMessageAutoClose(shortMsg, StringFormat.printString(msg, "<empty message>", "<null message>"));
      }
      else
      {
         JModalDialog.showMessage(shortMsg, StringFormat.printString(msg, "<empty message>", "<null message>"));
      }
      return 0;
   }

   /**
    * Get the last command exit code
    * @return the last command exit
    */
   public static int getLastExitCode()
   {
      return m_lastExitCode;
   }

   /**
    * Delete a directory with all files recursively
    * @param dirPath is the directory to delete
    */
   public static void deleteDir(File dirPath)
   {
      File[] contents = dirPath.listFiles();
      if (contents != null)
      {
         for (File f : contents)
         {
            deleteDir(f);
         }
      }
      dirPath.delete();
   }
   
   /**
    * Run a command
    * <p>
    * after call of runCommand() the error condition has to be checked with isLastError(), getLastError()
    * @param command is the command as VARARG e.g. "nordvpn", "connect"
    * @return the result of the command. Multiple lines in one string, delimited by carriage return
    */
   public static String runCommand(String... command)
   {
      Starter._m_logError.TraceCmd("Execute command=" + joinCommand(command));
      m_lastErrorMessage = null;
      m_lastExitCode = -1;

      if (Starter.isInstallMode() && command[0].equalsIgnoreCase("nordvpn"))
      {
         // in install mode (snap 'strict') we cannot execute the nordvpn command
         m_lastErrorMessage = "JNordVPN Manager runs in installer mode, command cannot be executed!";
         m_lastExitCode = -1;
         return m_lastErrorMessage;
      }

      Starter.setWaitCursor();
      ProcessBuilder processBuilder = new ProcessBuilder();
      processBuilder.command(command);
      try
      {
         m_stdOut = new StringBuffer();
         m_stdErr = new StringBuffer();
         ExecutorService streamHandlers = Executors.newFixedThreadPool(2);

         m_process = processBuilder.start();

         BufferedReader stdOut = new BufferedReader(new InputStreamReader(m_process.getInputStream()));
         BufferedReader stdErr = new BufferedReader(new InputStreamReader(m_process.getErrorStream()));

         streamHandlers.execute(() -> handleStream(stdOut, 1));
         streamHandlers.execute(() -> handleStream(stdErr, 2));

         if (!m_process.waitFor(COMMAND_TIMEOUT, TimeUnit.SECONDS))
         {
            JModalDialog.showError("Process Command Timeout", 
                  "The Command needed too long for execution. The command was cancelled.");
            m_process.destroyForcibly();
            m_process.waitFor();
         }

         m_lastExitCode = m_process.exitValue();
         Starter._m_logError.TraceCmd("Returncode=" + m_lastExitCode);
         if (0 != m_lastExitCode)
         {
            // return error
            m_lastErrorMessage = "Command '" + joinCommand(command) + "' returned with error code: " + m_lastExitCode + ".";
         }

         if (null != m_stdOut && m_stdOut.length() > 0) Starter._m_logError.TraceCmd("[stdout]\n" + m_stdOut + "\n");
         if (null != m_stdErr && m_stdErr.length() > 0) Starter._m_logError.TraceCmd("[stderr]\n" + m_stdErr + "\n");

         if (m_stdErr.length() > 0)
         {
            // add stderr to error message
            m_lastErrorMessage += m_stdErr.toString();
         }
      }
      catch (IOException e)
      {
         Starter._m_logError.LoggingExceptionMessage(4, 10900, e);
         m_lastExitCode = m_process.exitValue();
         m_lastErrorMessage = "Command '" + joinCommand(command) + "' returned with: IOException.";
      }
      catch (InterruptedException e)
      {
         Starter._m_logError.LoggingExceptionMessage(4, 10900, e);
         m_lastExitCode = m_process.exitValue();
         m_lastErrorMessage = "Command '" + joinCommand(command) + "' returned with: InterruptedException.";
      }
      catch (SecurityException e)
      {
         Starter._m_logError.LoggingExceptionMessage(4, 10900, e);
         m_lastExitCode = m_process.exitValue();
         m_lastErrorMessage = "Command '" + joinCommand(command) + "' returned with: SecurityException.";
      }
      finally
      {
         Starter.resetWaitCursor();
      }

      if (null != m_lastErrorMessage) Starter._m_logError.LoggingError(10900, "Command Error Message:", m_lastErrorMessage);
      return m_stdOut.toString();
   }

   /**
    * Read the command streams.
    * 
    * @param inputStream
    *           is the stream
    * @param which
    *           is 1 for stdout and 2 for stderr
    */
   private static void handleStream(BufferedReader inputStream, int which)
   {
      try (BufferedReader stdOutReader = inputStream)
      {
         String line;
         while ((line = stdOutReader.readLine()) != null)
         {
            if (1 == which)
            {
               if (m_stdOut.length() > 0) m_stdOut.append("\n");
               m_stdOut.append(line);
            }
            else
            {
               if (m_stdErr.length() > 0)  m_stdErr.append("\n");
               m_stdErr.append(line);
            }
         }
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   /**
    * Utility to join the VARARG command
    * 
    * @param command
    *           is the VARARG command
    * @return the joined command
    */
   public static String joinCommand(String... command)
   {
      StringBuffer fullCommand = new StringBuffer();
      for (String s : command)
      {
         if (fullCommand.length() > 0) fullCommand.append(" ");
         fullCommand.append(s);
      }
      return fullCommand.toString();
   }

   /**
    * Get number of days from a time stamp until now
    * 
    * @param timestamp
    * @return the days between
    */
   public static long getDaysUntilNow(long timestamp)
   {
      // Get the current date and time
      LocalDateTime now = LocalDateTime.now();

      // Define the format
      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

      // Format the current date and time
      String formattedNow = now.format(dtf);
      // Format the time stamp date and time
      String formattedTimestamp = new SimpleDateFormat("yyyy-MM-dd").format(timestamp);

      LocalDateTime date1 = LocalDate.parse(formattedTimestamp, dtf).atStartOfDay();
      LocalDateTime date2 = LocalDate.parse(formattedNow, dtf).atStartOfDay();
      long daysBetween = ChronoUnit.DAYS.between(date1, date2);

      return daysBetween;
   }
   
   public static boolean openWebpage(URI uri)
   {
      Starter._m_logError.LoggingInfo("Open URL in WebBrower: " + uri.toString());
      Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
      if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
      {
         try
         {
            desktop.browse(uri);
            return true;
         }
         catch (Exception e)
         {
            Starter._m_logError.LoggingExceptionMessage(4, 10903, e);
         }
      }
      else
      {
         Starter._m_logError.LoggingError(10903,
               "The Desktop does not support to open URL's in WebBrowers!",
               "URL: " + uri.toString() + " cannot be opened.\n" +
               "Try Fallback with xdg-open [url] command...");
         String status = runCommand("xdg-open", uri.toString());
         if (UtilSystem.isLastError())
         {
            Starter._m_logError.LoggingError(10903,
                  "Command 'xdg-open [url]' returned with error",
                  status);
         }
         else
         {
            return true;
         }
      }
      return false;
   }

   public static boolean openWebpage(URL url)
   {
      try
      {
         return openWebpage(url.toURI());
      }
      catch (URISyntaxException e)
      {
         Starter._m_logError.LoggingExceptionMessage(4, 10903, e);
      }
      return false;
   }


   public static void CopyTextFile(String fromFileName, String toFileName, String sFileEncoding, boolean force) throws IOException
   {
      File fromFile = new File(fromFileName);
      File toFile = new File(toFileName);
      Starter._m_logError.TraceDebug("Copy file '" + fromFileName + "' to '" + toFileName + "'.");

      if (!fromFile.exists()) throw new IOException("CopyTextFile: " + "no such source file: " + fromFileName);
      if (!fromFile.isFile()) throw new IOException("CopyTextFile: " + "can't copy directory: " + fromFileName);
      if (!fromFile.canRead()) throw new IOException("CopyTextFile: " + "source file is unreadable: " + fromFileName);

      if (toFile.isDirectory()) toFile = new File(toFile, fromFile.getName());

      if (toFile.exists())
      {
         if (!toFile.canWrite()) throw new IOException("CopyTextFile: " + "destination file is unwriteable: " + toFileName);
         if (force == false) throw new IOException( "CopyTextFile: " + "existing file was not overwritten.");
      }
      else
      {
         String parent = toFile.getParent();
         if (parent == null) parent = System.getProperty("user.dir");
         File dir = new File(parent);
         if (!dir.exists()) throw new IOException("CopyTextFile: " + "destination directory doesn't exist: " + parent);
         if (dir.isFile()) throw new IOException("CopyTextFile: " + "destination is not a directory: " + parent);
         if (!dir.canWrite()) throw new IOException("CopyTextFile: " + "destination directory is unwriteable: " + parent);
      }

      BufferedReader fbr = null;
      BufferedWriter fbw = null;
      try
      {
         FileInputStream fIn = new FileInputStream(fromFile);
         InputStreamReader isr = new InputStreamReader(fIn, "UTF-8");
         fbr = new BufferedReader(isr);

         FileOutputStream FOStream = new FileOutputStream(toFile);
         OutputStreamWriter osw = new OutputStreamWriter(FOStream, sFileEncoding);
         fbw = new BufferedWriter(osw);

         String buffer = "";

         while ((buffer = fbr.readLine()) != null)
         {
            fbw.write(buffer + "\n");
         }
      }
      finally
      {
         if (fbr != null) try
         {
            fbr.close();
         }
         catch (IOException e)
         {
            ;
         }
         if (fbw != null) try
         {
            fbw.close();
         }
         catch (IOException e)
         {
            ;
         }
      }
   }

}
