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
   private JFrame                 m_consoleMainFrame;
   private JEditorPane            m_consoleOutputEditorPane;
   private JScrollPane            m_consoleOutputPanel;
   private Thread                 m_readerThread;
   private Thread                 m_readerThread2;
   private boolean                m_quitFlag;
   private static boolean         m_isVisible         = false;
   private final PipedInputStream m_pipedInputStream  = new PipedInputStream();
   private final PipedInputStream m_pipedInputStream2 = new PipedInputStream();

   public GuiCustomConsole()
   {
      // create all components and add them
      m_consoleMainFrame = new JFrame("JNordVPN Manager Console");
      m_consoleMainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      
      // Close Window with "X"
      m_consoleMainFrame.addWindowListener(new WindowAdapter()
      {
         @Override public void windowClosing(java.awt.event.WindowEvent event)
         {
            Starter.setSkipWindowGainedFocus();
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
      JCheckBox cbxTraceCommand = new JCheckBox("Command");
      cbxTraceCommand.setToolTipText("Flag for Trace Command output.");
      int iTraceCommand = UtilPrefs.getTraceCmd();
      if (1 == iTraceCommand)
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
         public void actionPerformed(ActionEvent e) {
             JCheckBox cb = (JCheckBox) e.getSource();
             if (cb.isSelected()) {
                 UtilPrefs.setTraceCmd(1);
                 Starter._m_logError.enableTraceFlag(UtilLogErr.TRACE_Cmd);
             } else {
                UtilPrefs.setTraceCmd(0);
                Starter._m_logError.disableTraceFlag(UtilLogErr.TRACE_Cmd);
             }
         }
      });
      tracesRow.add(cbxTraceCommand);
      JCheckBox cbxTraceInit = new JCheckBox("Init");
      cbxTraceInit.setToolTipText("Flag for Trace Init output.");
      int iTraceInit = UtilPrefs.getTraceInit();
      if (1 == iTraceInit)
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
         public void actionPerformed(ActionEvent e) {
             JCheckBox cb = (JCheckBox) e.getSource();
             if (cb.isSelected()) {
                 UtilPrefs.setTraceInit(1);
                 Starter._m_logError.enableTraceFlag(UtilLogErr.TRACE_Init);
             } else {
                UtilPrefs.setTraceInit(0);
                Starter._m_logError.disableTraceFlag(UtilLogErr.TRACE_Init);
             }
         }
      });
      tracesRow.add(cbxTraceInit);
      JCheckBox cbxTraceDebug = new JCheckBox("Debug");
      cbxTraceDebug.setToolTipText("Flag for Trace Debug output.");
      int iTraceDebug = UtilPrefs.getTraceDebug();
      if (1 == iTraceDebug)
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
         public void actionPerformed(ActionEvent e) {
             JCheckBox cb = (JCheckBox) e.getSource();
             if (cb.isSelected()) {
                 UtilPrefs.setTraceDebug(1);
                 Starter._m_logError.enableTraceFlag(UtilLogErr.TRACE_Debug);
             } else {
                UtilPrefs.setTraceDebug(0);
                Starter._m_logError.disableTraceFlag(UtilLogErr.TRACE_Debug);
             }
         }
      });
      tracesRow.add(cbxTraceDebug);


      m_consoleOutputEditorPane = new JEditorPane();
      m_consoleOutputEditorPane.setEditable(false);
      m_consoleOutputEditorPane.setCaretColor(new Color(255, 235, 205)); // hide caret
      m_consoleOutputEditorPane.setContentType( "text/html" );
      m_consoleOutputEditorPane.setText("<html><head><style>"
            + "p {font-family: Monospaced; font-size:14;}"
            + "</style></head>"
            + "<body style=\"background-color:#FFEBCD\" id='body'>Console output start...</body></html>");

      m_consoleOutputPanel = new JScrollPane(m_consoleOutputEditorPane, 
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

      JPanel buttonRow = new JPanel();
      JButton btnClear = new JButton("Clear Log");
      JButton btnSave = new JButton("Save Log");
      JButton btnExit = new JButton("Exit JNordVPN Manager");
      buttonRow.setLayout(new FlowLayout(FlowLayout.CENTER));
      buttonRow.add(btnSave);
      buttonRow.add(btnExit);
      buttonRow.add(btnClear);

      m_consoleMainFrame.getContentPane().setLayout(new BorderLayout());
      m_consoleMainFrame.getContentPane().add(tracesRow, BorderLayout.PAGE_START);
      m_consoleMainFrame.getContentPane().add(m_consoleOutputPanel, BorderLayout.CENTER);
      m_consoleMainFrame.getContentPane().add(buttonRow, BorderLayout.PAGE_END);
      m_consoleMainFrame.setVisible(false);

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
      btnClear.addActionListener(this);

      try
      {
         PipedOutputStream pout = new PipedOutputStream(this.m_pipedInputStream);
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
         PipedOutputStream pout2 = new PipedOutputStream(this.m_pipedInputStream2);
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
      //
      m_readerThread = new Thread(this);
      m_readerThread.setDaemon(true);
      m_readerThread.start();
      //
      m_readerThread2 = new Thread(this);
      m_readerThread2.setDaemon(true);
      m_readerThread2.start();
   }

   public synchronized void actionPerformed(ActionEvent evt)
   {
      // Clear log button
      m_consoleOutputEditorPane.setText("");
   }

   public synchronized void run()
   {
      try
      {
         while (Thread.currentThread() == m_readerThread)
         {
            try
            {
               this.wait(100);
            }
            catch (InterruptedException ie)
            {
            }
            if (m_pipedInputStream.available() != 0)
            {
               String input = this.readLine(m_pipedInputStream);
               appendText(input);
            }
            if (m_quitFlag)
               return;
         }

         while (Thread.currentThread() == m_readerThread2)
         {
            try
            {
               this.wait(100);
            }
            catch (InterruptedException ie)
            {
            }
            if (m_pipedInputStream2.available() != 0)
            {
               String input = this.readLine(m_pipedInputStream2);
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

   public synchronized void btnExitExecute()
   {
      int ret = JModalDialog.YesNoDialog("Do you really want to exit JNordVPNManager?");
      if (ret == 0)
      {
         m_quitFlag = true;
         this.notifyAll(); // stop all threads
         try
         {
            m_readerThread.join(1000);
            m_pipedInputStream.close();
         }
         catch (Exception e)
         {
         }
         try
         {
            m_readerThread2.join(1000);
            m_pipedInputStream2.close();
         }
         catch (Exception e)
         {
         }
         Starter.cleanupAndExit(true);
      }
   }

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
            save_log_file(file);
         }
      }
   }

   private synchronized void save_log_file(String file)
   {
      try
      {
         FileOutputStream fos = new FileOutputStream(file);
         Writer w = new BufferedWriter(new OutputStreamWriter(fos));
         w.write(m_consoleOutputEditorPane.getText() + "\n");
         w.flush();
         w.close();
      }
      catch (Exception e)
      {
         Starter._m_logError.TranslatorExceptionMessage(4, 10901, e);
         JModalDialog.showError("Save Logfile Error", "Could not write log file.\n" + e.getMessage());
      }
   }

   public synchronized String readLine(PipedInputStream in) throws IOException
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

   private void appendText(String msg)
   {
      try
      {
         HTMLDocument doc = (HTMLDocument) m_consoleOutputEditorPane.getDocument();
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
         JScrollBar vertical = m_consoleOutputPanel.getVerticalScrollBar();
         vertical.setValue( vertical.getMaximum() );
      }
   }

   public synchronized boolean switchConsoleVisible()
   {
      m_isVisible = !m_isVisible;
      setConsoleVisible(m_isVisible);
      return m_isVisible;
   }

   public synchronized void setConsoleVisible(boolean value)
   {
      m_isVisible = value;
      m_consoleMainFrame.setVisible(value);
      if (value)
      {
         int max = m_consoleOutputPanel.getVerticalScrollBar().getMaximum();
         Point keypoint = new Point(0, max);
         Rectangle keyview = new Rectangle(keypoint);
         m_consoleOutputEditorPane.scrollRectToVisible(keyview);
      }
   }
}
