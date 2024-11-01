/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.utils;

import java.io.*;
import java.nio.charset.Charset;

/**
 * This class is a common error and message logging utility for com.mr.apps.
 * The constructor gets an file handler to the log file and the trace settings flags.
 * <P>
 * We differ between [Debug]Trace output and messages. The messages have the format:<P>
 * <PRE>
 * [&lt;type&gt;] &lt;classification&gt; (rc=&lt;msgNb&gt;): &lt;short message&gt;
 * + &lt;long messages
 * + ...&gt;
 * </PRE>
 * where:
 * <ul>
 * <li>
 * type - is one of the types: Info, Warning, Error, Fatal Error.
 * </li>
 * <li>
 * classification - is a fix classification text only for common messages upwards 10000.
 * </li>
 * <li>
 * msgNb - is the message number. Common messages starts with 10000.
 * </li>
 * <li>
 * short message - is a one line short message.
 * </li>
 * <li>
 * long message - is the multiple line message text (prefixed by a + sign).
 * </li>
 * </ul>
 * <P>
 * For the Fatal Error, a RuntimeException is thrown, that must be caught in the main application, to force an program termination.
 */

public class UtilLogErr
{
   public static final String  TRACE_Off              = "off";
   public static final String  TRACE_Debug            = "dbg";
   public static final String  TRACE_Cmd              = "cmd";
   public static final String  TRACE_Init             = "ini";
   private static final String FULL_TRACE_SETTINGS    = "ini;dbg;cmd";         // full list of possible tracing settings
   private static final String DEFAULT_TRACE_SETTINGS = TRACE_Off;             // default for trace settings
   private static final String DEFAULT_FILE_ENCODING  = "US-ASCII";

   private BufferedWriter      m_bwLogfile            = null;
   private String              m_sErrorLogFileName    = null;
   private String              m_sFileEncoding        = null;
   private String              m_traceSettings        = DEFAULT_TRACE_SETTINGS;
   private int                 m_lastErrorCode        = 0;
   private String              m_lastErrorMsg         = "";
   private int                 m_nbErrors             = 0;
   private int                 m_nbWarnings           = 0;
   private boolean             m_bConsoleOutput       = false;
   private boolean             m_fatalError           = false;

   /**
    * Constructor for the Common Logging+Error Class
    * <P>
    * The optional environment variable <CODE>MRAPPSOUTPUT</CODE> is used to define an additional console output or defines the
    * fallback file name for the logging file.
    * 
    * @param sErrorLogFileName
    *           is the name of the error log file name. If <CODE>null</CODE>, the environment variable
    *           <CODE>MRAPPSOUTPUT</CODE> is checked to set the logging target(s).
    * @param sFileEncoding
    *           is the file encoding. If <CODE>null</CODE>, the default is taken.
    * @param sTraceSettings
    *           is the trace settings flag. If <CODE>null</CODE>, the value is taken from the
    *           <CODE>COM_MR_APPS_TRACE</CODE> environment variable.
    */
   public UtilLogErr(String sErrorLogFileName, String sFileEncoding, String sTraceSettings)
   {
      m_bwLogfile = null;
      m_sErrorLogFileName = null;

      m_fatalError = false;
      m_lastErrorCode = 0;
      m_lastErrorMsg = "";
      m_nbErrors = 0;
      m_nbWarnings = 0;

      if ((sFileEncoding == null) || (sFileEncoding.length() <= 0))
      {
         sFileEncoding = m_sFileEncoding = DEFAULT_FILE_ENCODING;
      }
      else
      {
         m_sFileEncoding = sFileEncoding;
      }

      // set log variables
      String traceValue = System.getenv("COM_MR_APPS_TRACE");
      if (traceValue == null)
      {
         traceValue = sTraceSettings;
      }

      if (traceValue == null)
      {
         m_traceSettings = DEFAULT_TRACE_SETTINGS;
      }
      else
      {
         m_traceSettings = (traceValue.equalsIgnoreCase("full") == true) ? FULL_TRACE_SETTINGS : traceValue;
      }
      m_traceSettings += ";";

      String envVar = System.getenv("MRAPPSOUTPUT");
      if (envVar != null)
      {
         if ((envVar.equalsIgnoreCase("console")) ||
             (envVar.equalsIgnoreCase("stdout")) ||
             (envVar.equalsIgnoreCase("stderr")))
         {
            // additional output to console
            m_bConsoleOutput = true;
         }
         else
         {
            if ((sErrorLogFileName == null) || (sErrorLogFileName.length() <= 0))
            {
               // Fallback: use MRAPPSOUTPUT as output filename (e.g. if -EDUMP parameter is missing for Download)
               sErrorLogFileName = envVar;
            }
         }
      }
      if ((sErrorLogFileName != null) && (sErrorLogFileName.length() > 0))
      {
         sErrorLogFileName = sErrorLogFileName.replaceFirst("^~", System.getProperty("user.home"));
         try
         {
            // check, if log file already exists
            File fLog = new File (sErrorLogFileName).getCanonicalFile();
            if (fLog.exists())
            {
               if (false == fLog.delete())
               {
                  // existing logfile cannot be deleted -> reset logfile name and redirect output to stdout
                  TranslatorWarning(10900, 
                        "Cannot delete log file",
                        "Error on delete old log file=" + sErrorLogFileName + "<. Output redireced to console.");
                  sErrorLogFileName = null;
               }
            }

            if (sErrorLogFileName != null)
            {
               // check if path exists - if not, create
               String sErrLogPath = new File(sErrorLogFileName).getParent();
               File fpErrLogPath = new File(sErrLogPath);
               if (!fpErrLogPath.exists())
               {
                  // generate the directory
                  fpErrLogPath.mkdirs();
               }

               // force UTF-8 in case of ASCII/ISO
               sFileEncoding = ((sFileEncoding.equalsIgnoreCase("US-ASCII")) || (sFileEncoding.equalsIgnoreCase("ISO-8859-1"))) ? "UTF-8" : m_sFileEncoding;

               // open log file
               FileOutputStream FOStream = new FileOutputStream(sErrorLogFileName);
               OutputStreamWriter osw = new OutputStreamWriter(FOStream, sFileEncoding);
               m_bwLogfile = new BufferedWriter(osw);
               m_sErrorLogFileName = sErrorLogFileName;
            }
         }
         catch (IOException e)
         {
            // Log file could not be opened. Redirect output of logs and errors to stdout.
            TranslatorWarning(10900, 
                  "Cannot open log file",
                  "Error on open log file=" + sErrorLogFileName + "<. Output redireced to console.");
         }
      }

      // output after opening log file
      if (traceValue != null)
      {
         TranslatorInfo("Environment variable COM_MR_APPS_TRACE=" + traceValue + "<.");
      }

      if ((null != sErrorLogFileName) && (sErrorLogFileName.endsWith(".testlog") == false))
      {
         // "..._JUnit.testlog" is the log file extension for the JUnit test cases. 
         // Don't output info for test cases, because it causes differences to the references 
         TranslatorInfo("Log file name=" + sErrorLogFileName + "< [" + m_sFileEncoding + "]");
      }
   }

   /** 
    * Set the trace flag.
    * @param sTraceSettings the current set trace flags.
    */
   public void setTraceFlags (String sTraceSettings)
   {
      if (sTraceSettings != null)
      {
         m_traceSettings = (sTraceSettings.equalsIgnoreCase("full") == true) ? FULL_TRACE_SETTINGS : sTraceSettings;
      }
      else
      {
         m_traceSettings = DEFAULT_TRACE_SETTINGS;
      }
   }

   public void enableTraceFlag(String flag)
   {
      if (isTraceFlagSet(flag)) return;
      m_traceSettings += flag + ";";
   }

   public void disableTraceFlag(String flag)
   {
      if (!isTraceFlagSet(flag)) return;
      m_traceSettings = m_traceSettings.replace(flag + ";", "");
   }

   /** 
    * Get the trace flag.
    * @return the current set trace flags.
    */
   public String getTraceFlags ()
   {
      if (m_traceSettings == null)
      {
         return "";
      }
      else
      {
         return m_traceSettings;
      }
   }

   /** 
    * Check if any trace is set.
    * @return <CODE>true</CODE> if any trace flag is set.
    */
   public boolean isTraceActive ()
   {
      if ((m_traceSettings == null) || (m_traceSettings.length() <= 0) || (m_traceSettings.equalsIgnoreCase(TRACE_Off) == true))
      {
         return false;
      }
      else
      {
         return true;
      }
   }

   /** 
    * Check the trace flag.
    * @param sTraceFlag the trace flag to be checked.
    * @return <CODE>true</CODE> if this trace flag is set.
    */
   public boolean isTraceFlagSet (String  sTraceFlag)
   {
      if ((m_traceSettings == null) || (m_traceSettings.length() <= 0))
      {
         return false;
      }
      else
      {
         return m_traceSettings.contains(sTraceFlag);
      }
   }

   /**
    * Get the Character Set for File Encoding<p>
    * 
    * @param sDefault is the (system) default to be used in case of an error. If <CODE>null</CODE>, also the system default locale is used.  
    * @return the file encoding charset
    */
   public Charset getFileEncodingCharset (Charset sDefault)
   {
      Charset rcCharset = (sDefault == null) ? Charset.defaultCharset() : sDefault;
      
      try
      {
         rcCharset = Charset.forName(m_sFileEncoding);
      }
      catch (Exception e)
      {
         TranslatorError(10900,
               "Invalid Program File Encoding",
               "The defined Charset=" + m_sFileEncoding + "< is invalid. Reset to Charset=" + rcCharset.displayName() + "<!");
      }
      return rcCharset;
   }

   /** 
    * Get the log file name.
    * @return the complete log file path and name.
    */
   public String getLogFileName ()
   {
         return m_sErrorLogFileName;
   }

   /** 
    * Return the last error code written.
    * @return the last error code.
    */
   public int getLastErrCode ()
   {
      return m_lastErrorCode;
   }

   /** 
    * Return the last error message written.
    * @return the last error message.
    */
   public String getLastErrMsg ()
   {
      return m_lastErrorMsg;
   }

   /** 
    * Return a string with the number of errors and warnings as result of parsing.
    * @return a string with summarized numbers of errors and warnings.
    */
   public String getResult ()
   {
      if (m_nbErrors + m_nbWarnings == 0)
      {
         return "[Info] Translator results: No errors or warnings detected.";
      }
      else if (m_nbErrors == 0)
      {
         return "[Warning] Translator results: " + m_nbWarnings + " warning" + ((m_nbWarnings > 1) ? "s" : "") + " detected!";
      }
      else
      {
         return "[Error] Translator results: " + m_nbErrors + " error" + ((m_nbErrors > 1) ? "s" : "") + " and " + m_nbWarnings + " warning" + ((m_nbWarnings > 1) ? "s" : "") + " detected!";         
      }
   }

   /** 
    * Generate a simple text message line.
    * @param sShortMsg is the short message text.
    * @see UtilLogErr#TranslatorWriteMessage(int, int, String, String [])
    */
   public void TranslatorText (String sShortMsg)
   {
      int iSev = 0;
      int iMsgNb = 0;
      String[] sLongMsgArr = null;
      TranslatorWriteMessage (iSev, iMsgNb, sShortMsg, sLongMsgArr);
   }

   /** 
    * Generate a simple info message.
    * @param sShortMsg is the short message text.
    * @see UtilLogErr#TranslatorWriteMessage(int, int, String, String [])
    */
   public void TranslatorInfo (String sShortMsg)
   {
      int iSev = 1;
      int iMsgNb = 0;
      String[] sLongMsgArr = null;
      TranslatorWriteMessage (iSev, iMsgNb, sShortMsg, sLongMsgArr);
   }

   /** 
    * Generate a warning with severity 3.
    * @param iMsgNb is the error number.
    * @param sShortMsg is the short message text.
    * @param sLongMsgArr is the long message text array.
    * @see UtilLogErr#TranslatorWriteMessage(int, int, String, String [])
    */
   public void TranslatorWarning (int iMsgNb, String sShortMsg, String[] sLongMsgArr)
   {
      int iSev = 3;
      TranslatorWriteMessage (iSev, iMsgNb, sShortMsg, sLongMsgArr);
   }
   
   /** 
    * Generate a warning with severity 3.
    * @param iMsgNb is the error number.
    * @param sShortMsg is the short message text.
    * @param sLongMsg is the long message text (split on newline characters).
    * @see UtilLogErr#TranslatorWriteMessage(int, int, String, String [])
    */
   public void TranslatorWarning (int iMsgNb, String sShortMsg, String sLongMsg)
   {
      int iSev = 3;
      TranslatorWriteMessage (iSev, iMsgNb, sShortMsg, sLongMsg);
   }
   
   /** 
    * Generate an Error with severity 4.
    * @param iMsgNb is the error number.
    * @param sShortMsg is the short message text.
    * @param sLongMsgArr is the long message text array.
    * @see UtilLogErr#TranslatorWriteMessage(int, int, String, String [])
    */
   public void TranslatorError (int iMsgNb, String sShortMsg, String[] sLongMsgArr)
   {
      int iSev = 4;
      TranslatorWriteMessage (iSev, iMsgNb, sShortMsg, sLongMsgArr);
   }
   
   /** 
    * Generate an Error with severity 4.
    * @param iMsgNb is the error number.
    * @param sShortMsg is the short message text.
    * @param sLongMsg is the long message text (split on newline characters).
    * @see UtilLogErr#TranslatorWriteMessage(int, int, String, String [])
    */
   public void TranslatorError (int iMsgNb, String sShortMsg, String sLongMsg)
   {
      int iSev = 4;
      TranslatorWriteMessage (iSev, iMsgNb, sShortMsg, sLongMsg);
   }
   
   /** 
    * Generate a Fatal Error with severity 5.
    * @param iMsgNb is the error number.
    * @param sShortMsg is the short message text.
    * @param sLongMsgArr is the long message text array.
    * @see UtilLogErr#TranslatorWriteMessage(int, int, String, String [])
    */
   public void TranslatorAbend (int iMsgNb, String sShortMsg, String[] sLongMsgArr)
   {
      int iSev = 5;
      TranslatorWriteMessage (iSev, iMsgNb, sShortMsg, sLongMsgArr);
   }
   
   /** 
    * Generate a Fatal Error with severity 5.
    * @param iMsgNb is the error number.
    * @param sShortMsg is the short message text.
    * @param sLongMsg is the long message text (split on newline characters).
    * @see UtilLogErr#TranslatorWriteMessage(int, int, String, String [])
    */
   public void TranslatorAbend (int iMsgNb, String sShortMsg, String sLongMsg)
   {
      int iSev = 5;
      TranslatorWriteMessage (iSev, iMsgNb, sShortMsg, sLongMsg);
   }
   
   /** 
    * Generate an Abend Message for java exceptions. Write detailed stack traces, if debug trace is active. 
    * @param iMsgNb is the error number.
    * @param ex is the exception.
    * @see UtilLogErr#TranslatorWriteMessage(int, int, String, String [])
    */
   public void TranslatorException (int iMsgNb, Exception ex)
   {
      int iSev = 5;
      TranslatorExceptionMessage (iSev, iMsgNb, ex);
   }

   /** 
    * Generate a Message for java exceptions. Write detailed stack traces, if debug trace is active. 
    * @param iSev is the severity level.
    * @param iMsgNb is the error number.
    * @param ex is the exception.
    * @see UtilLogErr#TranslatorWriteMessage(int, int, String, String [])
    */
   public void TranslatorExceptionMessage (int iSev, int iMsgNb, Exception ex)
   {
      if (m_fatalError == true)
      {
         // we handled this already.. exit
         return;
      }

      String msgText = ex.getMessage();
      if (msgText == null)
      {
         msgText = "Translator exception (check CONSOLE output or set Trace=dbg for detailled stack trace)\n";
         if (isTraceFlagSet(TRACE_Debug) == false)
         {
            // if we don't get an error message and we don't have set Trace=dbg, we dump the trace now on the console
            ex.printStackTrace();
         }
      }

      if (msgText.startsWith("[Fatal Error]"))
      {
         // Runtime exception thrown in case of iSev=5
         TranslatorWriteMessage(iSev, -1, msgText, "");
         // additional output to console
         if ((m_bwLogfile != null) && (m_bConsoleOutput == false)) System.out.println(msgText + "\n");
      }
      else
      {
         if (isTraceFlagSet(TRACE_Debug) == true)
         {
            // for debug, print message with full stack trace to console and log file
            ex.printStackTrace();
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            TranslatorWriteMessage(iSev, iMsgNb, msgText, writer.toString());
         }
         else
         {
            // no debug, write only message
            TranslatorWriteMessage(iSev, iMsgNb, msgText, "");
         }
      }
   }

   /** 
    * Generate a message.
    * @param iSev is the severity level.
    * @param iMsgNb is the error number.
    * @param sShortMsg is the short message text.
    * @param sLongMsg is the long message text (split on newline characters).
    * @see UtilLogErr#TranslatorWriteMessage(int, int, String, String [])
    */
   public void TranslatorWriteMessage (int iSev, int iMsgNb, String sShortMsg, String sLongMsg)
   {
      String[] sLongMsgArr = null;
      if ((sLongMsg != null) && (sLongMsg.length() > 0))
      {
         sLongMsgArr = sLongMsg.split("[\\r\\n]+|[\\n]+");
      }
      TranslatorWriteMessage (iSev, iMsgNb, sShortMsg, sLongMsgArr);
   }

   /** 
    * Generate a message and write it to the log file. If the log file handler is <CODE>null</CODE> or an
    * error occurred on writing, the message is redirected to the standard output.<P>
    * For user defined messages, message numbers 1..9999 can be used. Messages &lt;=0 are for internal use only.
    * @param iSev is the severity level.
    * @param iMsgNb is the error number.
    * @param sShortMsg is the short message text.
    * @param sLongMsgArr is the long message text array.
    * @throws RuntimeException for fatal errors.
    */
   public void TranslatorWriteMessage (int iSev, int iMsgNb, String sShortMsg, String[] sLongMsgArr)
   {

      if (m_fatalError == true)
      {
         // first check, if we caught in a parent level the fatal exception... -> throw it back!
         throw new RuntimeException("[Fatal Error] Translator is terminating...\nLast Error: >"+ m_lastErrorMsg + "<");
      }

      if (sShortMsg == null) sShortMsg = "";
      boolean forcedAbend = false;

      // add message type
      String headerLine = "";
      switch (iSev) {
         case 0:
            headerLine = "";
            break;
         case 1:
         case 2:
            headerLine = "[Info] ";
            break;
         case 3:
            headerLine = "[Warning] ";
            m_nbWarnings++;
            break;
         case 4:
            headerLine = "[Error] ";
            m_nbErrors++;
            break;
         default:
            forcedAbend = true;
            headerLine = "[Fatal Error] ";
            if ((m_lastErrorCode == 0) || (iMsgNb > 0)) m_lastErrorCode = iMsgNb;
            m_nbErrors++;
      }

      // add message classification
      if (iMsgNb > 9999)
      {
         switch (iMsgNb) {
            // ### Parsing messages
            case 10100:
               headerLine += "Parsing error";
               break;
            case 10200:
               headerLine += "Input failture";
               break;
            case 10500:
               headerLine += "Data inconsistency";
               break;

            case 10900:
               headerLine += "Command Execution Failed";
               break;
            case 10901:
               headerLine += "System IO exception";
               break;
            case 10902:
               headerLine += "File Not Found exception";
               break;
            case 10903:
               headerLine += "Open Webpage exception";
               break;
            // ### internal messages
            case 10996:
               headerLine += "GeoTools Error";
               break;
            case 10997:
               headerLine += "Internal Error";
               break;
            case 10998:
               headerLine += "Software Error";
               break;
            case 10999:
               // reserved for final try/catch exception, to ensure correct program ending on not handled Java system exceptions
               headerLine += "Java System exception";
               forcedAbend = false; // we already come from this exception thrown below...!
               break;
            default:
               headerLine = "";
         }
         headerLine += " (rc=" + iMsgNb + "): " + sShortMsg;
      }
      else if (iMsgNb > 0)
      {
         // user defined local error messages 1..9999
         headerLine += ": " + sShortMsg;
      }

      if (iMsgNb < 0)
      {
         // reserved for internal [Fatal Error]
         headerLine = sShortMsg;
         forcedAbend = false; // we already come from this exception thrown below...!
      }
      else
      {
         // reserved for simple message text
         headerLine += sShortMsg;
      }

      // write header line to log file or console
      writeLog (headerLine);
      if (sLongMsgArr != null)
      {
         for (int iCnt = 0; iCnt < sLongMsgArr.length; iCnt++)
         {
            String traceLine = "+\t" + sLongMsgArr[iCnt];

            // write long message to log file or console
            writeLog (traceLine);
         }
      }

      if ((forcedAbend == true) || (m_fatalError == true))
      {
         if (m_fatalError == false)
         {
            m_fatalError = true;
            m_lastErrorMsg = headerLine; 
         }
         throw new RuntimeException("[Fatal Error] Translator is terminating...\nLast Error: >"+ m_lastErrorMsg + "<");
      }
   }
   
   /** 
    * Generate a debug trace.<P>
    * The trace has the format:
    * <PRE>
    * [Trace::dbg] <text>
    * </PRE>
    * @param sText is the text to be written in the log file, if the trace flag includes the dbg option
    */
   public void TraceDebug (String sText)
   {
      if (isTraceFlagSet(TRACE_Debug) == true)
      {
         String traceLine = "[Trace::dbg] " + sText;

         // write message to log file or console
         writeLog (traceLine);
      }
   }

   /** 
    * Generate a command trace.<P>
    * The trace has the format:
    * <PRE>
    * [Trace::cmd] <text>
    * </PRE>
    * @param sText is the text to be written in the log file, if the trace flag includes the cmd option
    */
   public void TraceCmd (String sText)
   {
      if (isTraceFlagSet(TRACE_Cmd) == true)
      {
         String traceLine = "[Trace::cmd] " + sText;

         // write message to log file or console
         writeLog (traceLine);
      }
   }

   /** 
    * Generate a trace for initialization steps.<P>
    * The trace has the format:
    * <PRE>
    * [Trace::ini] <text>
    * </PRE>
    * @param sText is the text to be written in the log file, if the trace flag includes the ini option
    */
   public void TraceIni (String sText)
   {
      if (isTraceFlagSet(TRACE_Init) == true)
      {
         String traceLine = "[Trace::ini] " + sText;

         // write message to log file or console
         writeLog (traceLine);
      }
   }

   /**
    * Close the error log file.
    */
   public void close ()
   {
      if (m_bwLogfile != null)
      {
         try
         {
            // flush and close
            m_bwLogfile.flush();
            m_bwLogfile.close();
            m_bwLogfile = null;
            m_sErrorLogFileName = null;
         }
         catch (IOException e)
         {
            // ...can be ignored.
         }
      }
   }

   // Utility: write to log file (or standard output)
   private void writeLog (String sText)
   {
      if (sText != null)
      {
         try
         {
            if (m_bwLogfile != null)
            {
               m_bwLogfile.write(sText + "\n");
               m_bwLogfile.flush();
               if (m_bConsoleOutput == true)
               {
                  // additional output to the console
                  System.out.println(formatToHtml(sText));
               }
            }
            else
            {
               System.out.println(sText);
            }
         } // try
         catch (IOException e)
         {
            System.out.println(sText);
         }
      }
   }

   /**
    * Format text for HTML console output
    * 
    * @param sText
    *           is the text to be formatted
    * @return the formatted text
    */
   private String formatToHtml(String sText)
   {
      // format stdout text to HTML only if console is activated!
      if (!m_bConsoleOutput) return sText;

      String sPrefix = "";
      String sPostfix = "";
      int iStartMsg = 0;
      if (sText.startsWith("[Info]"))
      {
         iStartMsg = 7;
         sPrefix = "<font face=\"serif\" color=\"black\">";
         sPostfix = "</font>";
      }
      else if (sText.startsWith("[Warning]"))
      {
         iStartMsg = 10;
         sPrefix = "<font face=\"serif\" color=\"orange\">";
         sPostfix = "</font>";
      }
      else if (sText.startsWith("[Error]"))
      {
         iStartMsg = 8;
         sPrefix = "<font face=\"serif\" color=\"red\">";
         sPostfix = "</font>";         
      }
      else if (sText.startsWith("[Fatal Error]"))
      {
         iStartMsg = 14;
         sPrefix = "<font face=\"serif\" color=\"red\"><b>";
         sPostfix = "</b></font>";
      }
      else if (sText.startsWith("[Trace::ini]"))
      {
         iStartMsg = 13;
         sPrefix = "<font face=\"serif\" color=\"green\">";
         sPostfix = "</font>";         
      }
      else if (sText.startsWith("[Trace::cmd]"))
      {
         iStartMsg = 13;
         sPrefix = "<font face=\"serif\" color=\"blue\"><b>";
         sPostfix = "</b></font>";
      }
      else if (sText.startsWith("[Trace::dbg]"))
      {
         iStartMsg = 13;
         sPrefix = "<font face=\"serif\" color=\"grey\"><em>";
         sPostfix = "</em></font>";
      }

      if (!sPrefix.isEmpty())
      {
         sPrefix = "<p>" + sPrefix;
         // the used JEditorPane to display the output seems to support only 'normal' ASCII signs and transforms 'higher' - like '<' - ASCII codes in &xx; :( 
//         sText = sText.replace("<", "&lt;");
//         sText = sText.replace(">", "&gt;");
//         sText = sText.replace("\"", "&quot;");
//         sText = sText.replace("&", "&amp;");
         sText = sText.replace("\n", "<br>");
         sPostfix = sPostfix + "</p>";
      }

      return sPrefix + sText.substring(iStartMsg) + sPostfix + "\n";
   }

   /**
    * Activate/Deactivate Console output
    * 
    * @param mode
    *           if <code>true</code>, output to console will be activated - else, output to console will be deactivated.
    */
   public void setConsoleOutput(boolean mode)
   {
      m_bConsoleOutput = mode;
   }
}
