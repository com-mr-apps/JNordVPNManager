/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.Writer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.gui.dialog.JModalDialog;
import com.mr.apps.JNordVpnManager.utils.UtilLogErr;
import com.mr.apps.JNordVpnManager.utils.UtilPrefs;

public class GuiCustomConsole extends WindowAdapter implements WindowListener, ActionListener, Runnable
{
   private static final Color     COLOR_wheat         = new Color(255, 235, 205);
   private static final Color     COLOR_darkGreen     = new Color(40, 180, 99);

   private boolean                m_isVisible         = false;
   private JFrame                 m_consoleMainFrame;
//   private JEditorPane            m_consoleOutputEditorPane;
   private JScrollPane            m_consoleOutputScrollPane;
   private Thread                 m_readerThreadOut;
   private Thread                 m_readerThreadErr;
   private boolean                m_quitFlag;
   private final PipedInputStream m_pipedInputStreamOut  = new PipedInputStream();
   private final PipedInputStream m_pipedInputStreamErr = new PipedInputStream();

   public GuiCustomConsole()
   {
      // create all components and add them
      m_consoleMainFrame = new JFrame("JNordVPN Manager Console");
      
      // Close Window with "X"
      m_consoleMainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      m_consoleMainFrame.addWindowListener(new WindowAdapter()
      {
         @Override public void windowClosing(java.awt.event.WindowEvent event)
         {
            setConsoleVisible(false);
         }
      });

      // Position
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension frameSize = new Dimension((int) (screenSize.width / 2), (int) (screenSize.height / 2));
      int x = (int) (frameSize.width / 3);
      int y = (int) (frameSize.height / 3);
      m_consoleMainFrame.setBounds(x, y, frameSize.width, frameSize.height/2);
 
      JPanel tracesRow = new JPanel();
      tracesRow.setLayout(new BoxLayout(tracesRow, BoxLayout.X_AXIS));
      JLabel lblTrace = new JLabel("Trace Settings: ");
      tracesRow.add(lblTrace);

      JCheckBox cbxTraceInit = new JCheckBox("Init");
      cbxTraceInit.setToolTipText("Flag for Trace Init output.");
      cbxTraceInit.setForeground(COLOR_darkGreen);
      cbxTraceInit.setBackground(COLOR_wheat);
      int iTraceInit = UtilPrefs.getTraceInit();
      if (1 == iTraceInit || Starter._m_logError.isTraceFlagSet(UtilLogErr.TRACE_Init))
      {
         cbxTraceInit.setSelected(true);
         Starter._m_logError.enableTraceFlag(UtilLogErr.TRACE_Init);
      }
      else
      {
         cbxTraceInit.setSelected(false);
         Starter._m_logError.disableTraceFlag(UtilLogErr.TRACE_Init);
      }
      cbxTraceInit.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            JCheckBox cb = (JCheckBox) e.getSource();
            if (cb.isSelected())
            {
               UtilPrefs.setTraceInit(1);
               Starter._m_logError.enableTraceFlag(UtilLogErr.TRACE_Init);
            }
            else
            {
               UtilPrefs.setTraceInit(0);
               Starter._m_logError.disableTraceFlag(UtilLogErr.TRACE_Init);
            }
         }
      });
      tracesRow.add(cbxTraceInit);

      JCheckBox cbxTraceCommand = new JCheckBox("Command");
      cbxTraceCommand.setToolTipText("Flag for Trace Command output.");
      cbxTraceCommand.setForeground(Color.blue);
      cbxTraceCommand.setBackground(COLOR_wheat);
      cbxTraceCommand.setFont(new Font(cbxTraceCommand.getFont().getName(),Font.BOLD,cbxTraceCommand.getFont().getSize()));
      int iTraceCommand = UtilPrefs.getTraceCmd();
      if (1 == iTraceCommand || Starter._m_logError.isTraceFlagSet(UtilLogErr.TRACE_Cmd))
      {
         cbxTraceCommand.setSelected(true);
         Starter._m_logError.enableTraceFlag(UtilLogErr.TRACE_Cmd);
      }
      else
      {
         cbxTraceCommand.setSelected(false);
         Starter._m_logError.disableTraceFlag(UtilLogErr.TRACE_Cmd);
      }
      cbxTraceCommand.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            JCheckBox cb = (JCheckBox) e.getSource();
            if (cb.isSelected())
            {
               UtilPrefs.setTraceCmd(1);
               Starter._m_logError.enableTraceFlag(UtilLogErr.TRACE_Cmd);
            }
            else
            {
               UtilPrefs.setTraceCmd(0);
               Starter._m_logError.disableTraceFlag(UtilLogErr.TRACE_Cmd);
            }
         }
      });
      tracesRow.add(cbxTraceCommand);

      JCheckBox cbxTraceDebug = new JCheckBox("Debug");
      cbxTraceDebug.setToolTipText("Flag for Trace Debug output.");
      cbxTraceDebug.setForeground(Color.gray);
      cbxTraceDebug.setBackground(COLOR_wheat);
      cbxTraceDebug.setFont(new Font(cbxTraceDebug.getFont().getName(),Font.ITALIC,cbxTraceDebug.getFont().getSize()));
      int iTraceDebug = UtilPrefs.getTraceDebug();
      if (1 == iTraceDebug || Starter._m_logError.isTraceFlagSet(UtilLogErr.TRACE_Debug))
      {
         cbxTraceDebug.setSelected(true);
         Starter._m_logError.enableTraceFlag(UtilLogErr.TRACE_Debug);
      }
      else
      {
         cbxTraceDebug.setSelected(false);
         Starter._m_logError.disableTraceFlag(UtilLogErr.TRACE_Debug);
      }
      cbxTraceDebug.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            JCheckBox cb = (JCheckBox) e.getSource();
            if (cb.isSelected())
            {
               UtilPrefs.setTraceDebug(1);
               Starter._m_logError.enableTraceFlag(UtilLogErr.TRACE_Debug);
            }
            else
            {
               UtilPrefs.setTraceDebug(0);
               Starter._m_logError.disableTraceFlag(UtilLogErr.TRACE_Debug);
            }
         }
      });
      tracesRow.add(cbxTraceDebug);

      JCheckBox cbxWriteToLogfile = new JCheckBox("Log File");
      cbxWriteToLogfile.setToolTipText("Flag for output in the log file.");
      int isLogFileActive = UtilPrefs.isLogfileActive();
      if (1 == isLogFileActive || Starter._m_logError.isLogFileActive())
      {
         cbxWriteToLogfile.setSelected(true);
      }
      else
      {
         cbxWriteToLogfile.setSelected(false);
      }
      cbxWriteToLogfile.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            JCheckBox cb = (JCheckBox) e.getSource();
            boolean logFileActive;
            if (cb.isSelected())
            {
               logFileActive = Starter._m_logError.setLogFileActive(true);
            }
            else
            {
               logFileActive = Starter._m_logError.setLogFileActive(false);
            }
            if (logFileActive)
            {
               UtilPrefs.setLogfileActive(1);
            }
            else
            {
               UtilPrefs.setLogfileActive(0);
            }
         }
      });
      tracesRow.add(cbxWriteToLogfile);

      JEditorPane editorPane = new JEditorPane();
      editorPane.setEditable(false);
      editorPane.setCaretColor(new Color(255, 235, 205)); // hide caret
      editorPane.setContentType( "text/html" );
      editorPane.setText("<html><head><style>"
            + "p {font-family: Monospaced; font-size:14;}"
            + "</style></head>"
            + "<body style=\"background-color:#FFEBCD\" id='body'>Console output start...</body></html>");

      m_consoleOutputScrollPane = new JScrollPane(editorPane, 
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

      JPanel buttonRow = new JPanel();
      JButton btnClear = new JButton("Clear Log");
      JButton btnSave = new JButton("Save Log");
      JButton btnClose = new JButton("Close Log");
      JButton btnExit = new JButton("Exit JNordVPN Manager");
      buttonRow.setLayout(new FlowLayout(FlowLayout.CENTER));
      buttonRow.add(btnSave);
//      buttonRow.add(btnExit);
      buttonRow.add(btnClear);
      buttonRow.add(btnClose);

      m_consoleMainFrame.getContentPane().setLayout(new BorderLayout());
      m_consoleMainFrame.getContentPane().add(tracesRow, BorderLayout.PAGE_START);
      m_consoleMainFrame.getContentPane().add(m_consoleOutputScrollPane, BorderLayout.CENTER);
      m_consoleMainFrame.getContentPane().add(buttonRow, BorderLayout.PAGE_END);
      m_consoleMainFrame.setVisible(false);
      Starter._m_logError.setConsoleOutput(true);

      m_consoleMainFrame.addWindowListener(this);
      btnSave.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0)
         {
            btnSaveExecute();
         }
      });
      btnExit.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0)
         {
            btnExitExecute();
         }
      });
      btnClear.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0)
         {
            btnClearExecute();
         }
      });

      btnClose.addActionListener(this);

      try
      {
         PipedOutputStream pout = new PipedOutputStream(this.m_pipedInputStreamOut);
         System.setOut(new PrintStream(pout, true));
      }
      catch (java.io.IOException io)
      {
         Starter._m_logError.setConsoleOutput(false);
         Starter._m_logError.TranslatorExceptionMessage(4, 10901, io);
         Starter._m_logError.setConsoleOutput(true);
      }
      catch (SecurityException se)
      {
         Starter._m_logError.setConsoleOutput(false);
         Starter._m_logError.TranslatorExceptionMessage(4, 10901, se);
         Starter._m_logError.setConsoleOutput(true);
      }

      try
      {
         PipedOutputStream pout2 = new PipedOutputStream(this.m_pipedInputStreamErr);
         System.setErr(new PrintStream(pout2, true));
      }
      catch (java.io.IOException io)
      {
         Starter._m_logError.setConsoleOutput(false);
         Starter._m_logError.TranslatorExceptionMessage(4, 10901, io);
         Starter._m_logError.setConsoleOutput(true);
      }
      catch (SecurityException se)
      {
         Starter._m_logError.setConsoleOutput(false);
         Starter._m_logError.TranslatorExceptionMessage(4, 10901, se);
         Starter._m_logError.setConsoleOutput(true);
      }

      m_quitFlag = false; // signals the Threads that they should exit

      // Starting two separate threads to read from the PipedInputStreams
      m_readerThreadOut = new Thread(this);
      m_readerThreadOut.setDaemon(true);
      m_readerThreadOut.start();
      //
      m_readerThreadErr = new Thread(this);
      m_readerThreadErr.setDaemon(true);
      m_readerThreadErr.start();

   }

   /**
    * Runnable thread(s) - read stdOut and stdErr
    */
   public synchronized void run()
   {
      try
      {
         while (Thread.currentThread() == m_readerThreadOut)
         {
            try
            {
               this.wait(100);
            }
            catch (InterruptedException ie)
            {
            }
            if (m_pipedInputStreamOut.available() != 0)
            {
               String input = this.readLine(m_pipedInputStreamOut);
               appendText(input);
            }
            if (m_quitFlag)
               return;
         }

         while (Thread.currentThread() == m_readerThreadErr)
         {
            try
            {
               this.wait(100);
            }
            catch (InterruptedException ie)
            {
            }
            if (m_pipedInputStreamErr.available() != 0)
            {
               String input = this.readLine(m_pipedInputStreamErr);
               appendText(input);
            }
            if (m_quitFlag)
               return;
         }
      }
      catch (Exception e)
      {
         Starter._m_logError.setConsoleOutput(false);
         Starter._m_logError.TranslatorExceptionMessage(4, 10901, e);
         Starter._m_logError.setConsoleOutput(true);
      }
   }

   /**
    * Action Close Button pressed
    */
   public synchronized void actionPerformed(ActionEvent evt)
   {
      // Close button
      setConsoleVisible(false);
   }

   /**
    * Action Exit Button pressed
    */
   private synchronized void btnExitExecute()
   {
      int ret = JModalDialog.YesNoDialog("Do you really want to exit JNordVPNManager?");
      if (ret == 0)
      {
         m_quitFlag = true;
         this.notifyAll(); // stop all threads
         try
         {
            m_readerThreadOut.join(1000);
            m_pipedInputStreamOut.close();
         }
         catch (Exception e)
         {
         }
         try
         {
            m_readerThreadErr.join(1000);
            m_pipedInputStreamErr.close();
         }
         catch (Exception e)
         {
         }
         Starter.cleanupAndExit(true);
      }
   }

   /**
    * Action Save Button pressed
    */
   private synchronized void btnSaveExecute()
   {
      JFileChooser filedia = new JFileChooser();
      filedia.setDialogType(JFileChooser.SAVE_DIALOG);
      filedia.setCurrentDirectory(new File(System.getProperty("user.home")));
      filedia.setFileFilter(new FileNameExtensionFilter("Log File [html]", "html"));
      int ret = filedia.showSaveDialog(m_consoleMainFrame);
      if (ret == 0)
      {
         String file = filedia.getSelectedFile().getAbsolutePath();
         if (file.lastIndexOf(".") == -1)
         {
            file = file + ".html";
         }
         if (file != null && !(file.equals("")))
         {
            saveLogFileToDisk(file);
         }
      }
   }

   /**
    * Action Clear Button pressed
    */
   private synchronized void btnClearExecute()
   {
      JViewport viewport = m_consoleOutputScrollPane.getViewport(); 
      JEditorPane editorPane = (JEditorPane)viewport.getView(); 
      editorPane.setText("");
   }


   /**
    * Save the log file (html) to disk.
    * @param file is the log file name
    */
   private synchronized void saveLogFileToDisk(String file)
   {
      try
      {
         JViewport viewport = m_consoleOutputScrollPane.getViewport(); 
         JEditorPane editorPane = (JEditorPane)viewport.getView(); 

         FileOutputStream fos = new FileOutputStream(file);
         Writer w = new BufferedWriter(new OutputStreamWriter(fos));
         w.write(editorPane.getText() + "\n");
         w.flush();
         w.close();
      }
      catch (Exception e)
      {
         Starter._m_logError.TranslatorExceptionMessage(4, 10901, e);
         JModalDialog.showError("Save Logfile Error", "Could not write log file.\n" + e.getMessage());
      }
   }

   /**
    * Read from the stream byte by byte until 'EndOfLine'
    * 
    * @param in
    *           is the input stream
    * @return a complete line
    * @throws IOException
    */
   private synchronized String readLine(PipedInputStream in) throws IOException
   {
      StringBuffer line = new StringBuffer();
      
      // Read from the stream byte by byte until 'EndOfLine' - to return always one complete line
      int nbRead = 0;
      byte b[] = new byte[1];
      do
      {
         int available = in.available();
         if (available == 0)
            break;
         nbRead = in.read(b);
         if (b[0] != '\n') line.append((char)b[0]);
      }
      while (nbRead != 0 && b[0] != '\n' && !m_quitFlag);

      return line.toString();
   }

   /**
    * Append a message to the end of the console text.
    * 
    * @param msg
    *           is the message to add
    */
   private void appendText(String msg)
   {
      try
      {
         JViewport viewport = m_consoleOutputScrollPane.getViewport(); 
         JEditorPane editorPane = (JEditorPane)viewport.getView(); 

         HTMLDocument doc = (HTMLDocument) editorPane.getDocument();
         Element elem = doc.getElement("body");
         doc.insertBeforeEnd(elem, msg);
      }
      catch (BadLocationException | IOException e)
      {
         Starter._m_logError.setConsoleOutput(false);
         Starter._m_logError.TranslatorExceptionMessage(4, 10901, e);
         Starter._m_logError.setConsoleOutput(true);
      }
      finally
      {
         JScrollBar vertical = m_consoleOutputScrollPane.getVerticalScrollBar();
         vertical.setValue( vertical.getMaximum() );
      }
   }

   /**
    * Show/Hide the console.
    */
   private synchronized void setConsoleVisible(boolean value)
   {
      m_isVisible = value;
      m_consoleMainFrame.setVisible(value);
      if (value)
      {
         JViewport viewport = m_consoleOutputScrollPane.getViewport(); 
         JEditorPane editorPane = (JEditorPane)viewport.getView(); 

         int max = m_consoleOutputScrollPane.getVerticalScrollBar().getMaximum();
         Point keypoint = new Point(0, max);
         Rectangle keyview = new Rectangle(keypoint);
         editorPane.scrollRectToVisible(keyview);
      }
      else
      {
         // close window -> return focus to main frame
         Starter.setSkipWindowGainedFocus();
      }
   }

   /**
    * Switch the visibility status of the console between on and off.<p>
    * From outside, com.mr.apps.JNordVpnManager.Starter.switchConsoleWindow() should be called.
    * @return the current visibility status of the console
    */
   public synchronized boolean switchConsoleVisible()
   {
      m_isVisible = !m_isVisible;
      setConsoleVisible(m_isVisible);
      return m_isVisible;
   }
}
