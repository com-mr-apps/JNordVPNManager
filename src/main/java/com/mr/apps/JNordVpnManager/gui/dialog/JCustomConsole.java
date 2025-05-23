/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.dialog;

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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.utils.UtilLogErr;
import com.mr.apps.JNordVpnManager.utils.UtilPrefs;

@SuppressWarnings("serial")
public class JCustomConsole extends JFrame
{
   private static final Color  COLOR_wheat         = new Color(255, 235, 205);
   private static final Color  COLOR_darkGreen     = new Color(40, 180, 99);

   public static final String  CONSOLE_STDOUT_PIPE = new File(Starter.APPLICATION_DATA_ABS_PATH, "stdout_pipe").toString();
   public static final String  CONSOLE_STDERR_PIPE = new File(Starter.APPLICATION_DATA_ABS_PATH, "stderr_pipe").toString();
   public static final String  CONSOLE_CMD_EXIT    = "#EXIT#";
   public static final String  CONSOLE_CMD_SHOW    = "#SHOW#";
   public static final String  CONSOLE_CMD_HIDE    = "#HIDE#";
   public static final String  CONSOLE_CMD_SWITCH  = "#SWITCH#";

   private static final String    EMPTY_TEXT       = "<html><head><style>"
         + ".dbg {font-style: italic; color: #7b7d7d;}"
         + ".cmd {font-weight: bold; color: blue;}"
         + ".ext {font-style: italic; color: orange;}"
         + ".ini {font-weight: normal; color: #28b463;}"
         + ".out {font-style: italic; font-weight: normal; color: blue;}"
         + ".err {font-style: italic; font-weight: normal; color: orange;}"
         + ".er5 {font-weight: bold; color: #ff334f;}"
         + ".er4 {font-weight: bold; color: red;}"
         + ".er3 {font-weight: normal; color: orange;}"
         + ".inf {font-weight: normal; color: black;}"
         + "p {font-family: Monospaced; font-size:14;margin-top:2px;margin-bottom:2px}"
         + "</style></head>"
         + "<body style=\"background-color:#FFEBCD\" id='body'>Console output start...</body></html>";

   private boolean             m_isVisible         = false;
   private JFrame              m_consoleMainFrame;
   private JScrollPane         m_consoleOutputScrollPane;
   private boolean             m_quitFlag;
   private String              m_pid               = null;

   ExecutorService             m_streamHandlers    = null;

   public JCustomConsole(String sPid)
   {
      super("JNordVPN Manager Console");
       m_consoleMainFrame = this;
       m_pid = sPid;
      
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

      // Content
      JPanel tracesRow = new JPanel();
      tracesRow.setLayout(new BoxLayout(tracesRow, BoxLayout.X_AXIS));
      JLabel lblTrace = new JLabel("Console Settings (Changes require restart!): ");
      tracesRow.add(lblTrace);
      tracesRow.add(Box.createRigidArea(new Dimension(5, 0)));

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
      tracesRow.add(Box.createRigidArea(new Dimension(5, 0)));

      JCheckBox cbxTraceCommand = new JCheckBox("Command");
      cbxTraceCommand.setToolTipText("Flag for Trace Command output.");
      cbxTraceCommand.setForeground(Color.blue);
      cbxTraceCommand.setBackground(COLOR_wheat);
      cbxTraceCommand.setFont(new Font(cbxTraceCommand.getFont().getName(), Font.BOLD, cbxTraceCommand.getFont().getSize()));
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
      tracesRow.add(Box.createRigidArea(new Dimension(5, 0)));

      JCheckBox cbxTraceDebug = new JCheckBox("Debug");
      cbxTraceDebug.setToolTipText("Flag for Trace Debug output.");
      cbxTraceDebug.setForeground(Color.gray);
      cbxTraceDebug.setBackground(COLOR_wheat);
      cbxTraceDebug.setFont(new Font(cbxTraceDebug.getFont().getName(), Font.ITALIC, cbxTraceDebug.getFont().getSize()));
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
      tracesRow.add(Box.createHorizontalGlue());

      JCheckBox cbxOpenConsole = new JCheckBox("Open Console on Application Start");
      cbxOpenConsole.setToolTipText("Change User Settings for Open Console on Application Start.");
      int iOpenConsole = UtilPrefs.isConsoleActive();
      if (1 == iOpenConsole)
      {
         cbxOpenConsole.setSelected(true);
      }
      else
      {
         cbxOpenConsole.setSelected(false);
      }
      cbxOpenConsole.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            JCheckBox cb = (JCheckBox) e.getSource();
            if (cb.isSelected())
            {
               UtilPrefs.setConsoleActive(1);
            }
            else
            {
               UtilPrefs.setConsoleActive(0);
            }
         }
      });
      tracesRow.add(cbxOpenConsole);
      tracesRow.add(Box.createRigidArea(new Dimension(5, 0)));

      JCheckBox cbxWriteToLogfile = new JCheckBox("Write Log File");
      cbxWriteToLogfile.setToolTipText("Change User Settings for log file output.");
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
      editorPane.setCaretColor(COLOR_wheat); // hide caret
      editorPane.setContentType( "text/html" );
      editorPane.setText(EMPTY_TEXT);

      m_consoleOutputScrollPane = new JScrollPane(editorPane, 
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

      JPanel buttonRow = new JPanel();
      JButton btnClear = new JButton("Clear Log");
      JButton btnSave = new JButton("Save Log");
      JButton btnClose = new JButton("Close Console");
      JButton btnExit = new JButton("Force Exit JNordVPN Manager");
      buttonRow.setLayout(new FlowLayout(FlowLayout.CENTER));
      buttonRow.add(btnSave);
      buttonRow.add(btnClear);
      buttonRow.add(btnClose);
      buttonRow.add(btnExit);

      m_consoleMainFrame.getContentPane().setLayout(new BorderLayout());
      m_consoleMainFrame.getContentPane().add(tracesRow, BorderLayout.PAGE_START);
      m_consoleMainFrame.getContentPane().add(m_consoleOutputScrollPane, BorderLayout.CENTER);
      m_consoleMainFrame.getContentPane().add(buttonRow, BorderLayout.PAGE_END);
      m_consoleMainFrame.setVisible(false);

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

      btnClose.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0)
         {
            // Close button
            setConsoleVisible(false);
         }
      });

      FileInputStream stdoutPipe;
      FileInputStream stderrPipe;
      try
      {
         stdoutPipe = new FileInputStream(CONSOLE_STDOUT_PIPE);
         stderrPipe = new FileInputStream(CONSOLE_STDERR_PIPE);
      }
      catch (FileNotFoundException e)
      {
         e.printStackTrace();
         return;
      }
 
      BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdoutPipe));
      BufferedReader stderrReader = new BufferedReader(new InputStreamReader(stderrPipe));

      // start two threads to read stdOut and stdErr
      m_streamHandlers = Executors.newFixedThreadPool(2);
      m_quitFlag = false; // signals the Threads that they should exit
      m_streamHandlers.execute(() -> handleStream(1, stdoutReader));
      m_streamHandlers.execute(() -> handleStream(2, stderrReader));

      if (1 == UtilPrefs.isConsoleActive())
      {
         // open the console at program start
         setConsoleVisible(true);
      }
   }

   /**
    * Runnable thread(s) - read stdOut and stdErr
    */
   public synchronized void handleStream(int nb, BufferedReader stdReader)
   {
      try
      {
         while (true)
         {
            try
            {
               this.wait(10);
            }
            catch (InterruptedException ie)
            {
            }

            String input;
            while ((input = stdReader.readLine()) != null)
            {
               if (nb == 2) // check for stderr commands
               {
                  if (input.equals(CONSOLE_CMD_EXIT))
                  {
                     m_quitFlag = true;
                     btnExitExecute();
                  }
                  else if (input.equals(CONSOLE_CMD_SHOW))
                  {
                     setConsoleVisible(true);
                  }
                  else if (input.equals(CONSOLE_CMD_HIDE))
                  {
                     setConsoleVisible(false);
                  }
                  else if (input.equals(CONSOLE_CMD_SWITCH))
                  {
                     switchConsoleVisible();
                  }
                  else // stderr output
                  {
                     appendText(input);
                  }
               }
               else // stdout output
               {
                  appendText(input);
               }

               if (m_quitFlag)
               {
                  return;
               }
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Action Exit Button pressed
    */
   private synchronized void btnExitExecute()
   {
      if (m_quitFlag || JModalDialog.YesNoDialog("Do you really want to FORCE exit JNordVPNManager?") == 0)
      {
         m_quitFlag = true;
         m_streamHandlers.shutdown(); // Disable new tasks from being submitted
         try
         {
            // Wait a while for existing tasks to terminate
            if (!m_streamHandlers.awaitTermination(1, TimeUnit.SECONDS))
            {
               m_streamHandlers.shutdownNow(); // Cancel currently executing tasks
               // Wait a while for tasks to respond to being cancelled
               if (!m_streamHandlers.awaitTermination(1, TimeUnit.SECONDS))
               {
                  System.out.println("Console Shutdown - Stream Handler threads did not terminate.");
               }
            }
         }
         catch (InterruptedException ie)
         {
            // (Re-)Cancel if current thread also interrupted
            m_streamHandlers.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
         }

         ProcessBuilder killMainApp = new ProcessBuilder("kill", "-9", m_pid);
         try
         {
            killMainApp.start();
         }
         catch (IOException e)
         {
         }

         System.exit(0);
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
      editorPane.setText(EMPTY_TEXT);
   }

   /**
    * Save the log file (html) to disk.
    * 
    * @param file
    *           is the log file name
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
         e.printStackTrace();
      }
   }

   /**
    * Append a message to the end of the console text.
    * 
    * @param msg
    *           is the message to add
    */
   private synchronized void appendText(String msg)
   {
      if (msg.isBlank()) return;
      try
      {
         JViewport viewport = m_consoleOutputScrollPane.getViewport(); 
         JEditorPane editorPane = (JEditorPane)viewport.getView(); 

         HTMLDocument doc = (HTMLDocument) editorPane.getDocument();
         Element elem = doc.getElement("body");
         doc.insertBeforeEnd(elem, msg);

         editorPane.setCaretPosition(doc.getLength());
      }
      catch (BadLocationException | IOException e)
      {
         e.printStackTrace();
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

   public synchronized boolean getConsoleVisible()
   {
      return m_isVisible;
   }
}
