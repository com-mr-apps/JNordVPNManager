/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.utils;

import java.io.File;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import com.mr.apps.JNordVpnManager.Starter;

/**
 * Some common utilities
 */
public class UtilSystem
{
   private static final int COMMAND_TIMEOUT = 10;
   
   private static String m_lastErrorMessage = null;

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
      StringBuffer result = new StringBuffer();
      StringBuffer result_err = new StringBuffer();
      ProcessBuilder processBuilder = new ProcessBuilder();

      Starter.setWaitCursor();

      Starter._m_logError.TraceCmd("Execute command=" + joinCommand(command));
      m_lastErrorMessage = null;
      processBuilder.command(command);
      try
      {
         Process process = processBuilder.start();

         BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
         BufferedReader reader_err = new BufferedReader(new InputStreamReader(process.getErrorStream()));

         String line = null;
         while ((line = reader.readLine()) != null)
         {
            if (!result.isEmpty()) result.append("\n");
            result.append(line);
         }

         String line_err = null;
         while ((line_err = reader_err.readLine()) != null)
         {
            if (!result_err.isEmpty()) result_err.append("\n");
            result_err.append(line_err);
         }

         if (!process.waitFor(COMMAND_TIMEOUT, TimeUnit.SECONDS))
         {
            JOptionPane.showMessageDialog(null, "The Command needed too long for execution. The command was cancelled.", "Process Command Timeout", JOptionPane.ERROR_MESSAGE);
            process.destroy();
         }
         int exitCode = process.exitValue();
         if (0 != exitCode)
         {
            // return error
            m_lastErrorMessage = "Command '" + joinCommand(command) + "' returned with error code: " + 
                                 exitCode + ".";
         }
         Starter._m_logError.TraceCmd("Returncode=" + exitCode);
         if (null != result && !result.isEmpty()) Starter._m_logError.TraceCmd("[stdout]\n" + result + "\n");
         if (null != result_err && !result_err.isEmpty()) Starter._m_logError.TraceCmd("[stderr]\n" + result_err + "\n");
         if (!result_err.isEmpty())
         {
            m_lastErrorMessage += result_err.toString();
         }
      }
      catch (IOException e)
      {
         m_lastErrorMessage = "Command '" + joinCommand(command) + "' returned with: IOException.";
         Starter._m_logError.TranslatorExceptionMessage(4, 10900, e);
      }
      catch (InterruptedException e)
      {
         m_lastErrorMessage = "Command '" + joinCommand(command) + "' returned with: InterruptedException.";
         Starter._m_logError.TranslatorExceptionMessage(4, 10900, e);
      }
      finally
      {
         Starter.resetWaitCursor();
      }

      if (null != m_lastErrorMessage) Starter._m_logError.TranslatorError(10900, "Command Error Message:", m_lastErrorMessage);
      return result.toString();
   }

   /**
    * Utility to join the VARARG command
    * @param command is the VARARG command
    * @return the joined command
    */
   private static String joinCommand(String... command)
   {
      StringBuffer fullCommand = new StringBuffer();
      for (String s : command)
      {
         if (!fullCommand.isEmpty()) fullCommand.append(" ");
         fullCommand.append(s);
      }
      return fullCommand.toString();
   }

   /**
    * Get number of days from a time stamp until now
    * @param timestamp
    * @return
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
      long daysBetween = ChronoUnit.DAYS.between(date1, date2) ;

      return daysBetween;
   }
   
   public static boolean openWebpage(URI uri)
   {
      Starter._m_logError.TranslatorInfo("Open URL in WebBrower: " + uri.toString());
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
            Starter._m_logError.TranslatorExceptionMessage(4, 10903, e);
         }
      }
      else
      {
         Starter._m_logError.TranslatorError(10903, "The Desktop does not support to open URL's in WebBrowers!", "URL: " + uri.toString() + " cannot be opened.");
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
         Starter._m_logError.TranslatorExceptionMessage(4, 10903, e);
      }
      return false;
   }

}
