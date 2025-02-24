/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.settings;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.gui.dialog.JModalDialog;
import com.mr.apps.JNordVpnManager.utils.UtilPrefs;

@SuppressWarnings("serial")
public class JUserPrefsDialog extends JDialog implements ActionListener
{
   private final static String BUTTON_LIST = "Save,Cancel";
   private final static int SAVE_BUTTON = 0;
   private final static int CANCEL_BUTTON = 1;

   private String m_result;
   private JSettingsPanel m_userPrefsPanel = null;

   /**
    * User Preferences Panel Layout definition.
    */
   public JUserPrefsDialog(Frame owner, String title, Map<String, JSettingsPanelField> settingsPanelFields)
   {
      super(owner, title, true);

      Starter.setWaitCursor();
      Starter.setCursorCanChange(false);

      getContentPane().setLayout(new BorderLayout());
      setResizable(false);
      Point parloc = owner.getLocation();
      setLocation(parloc.x + 30, parloc.y + 30);

      setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      addWindowListener(new WindowAdapter()
      {
         @Override public void windowClosing(java.awt.event.WindowEvent event)
         {
            close();
         }
      });

      // ---------------------------------------------------------------------------------------------
      // Header
      // ---------------------------------------------------------------------------------------------
      JPanel headerPanel = new JPanel();
      headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
      JButton jbImport = new JButton("Import");
      jbImport.setToolTipText("Import application User Preferences from a file.");
      jbImport.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            // import a settings file
            JFileChooser filedia = new JFileChooser();
            filedia.setDialogType(JFileChooser.OPEN_DIALOG);
            filedia.setCurrentDirectory(new File(System.getProperty("user.home"), Starter.APPLICATION_DATA_DIR));
//          filedia.setFileFilter(new FileNameExtensionFilter("Settings File [exp]", "exp"));
            int ret = filedia.showOpenDialog(m_userPrefsPanel);
            if (ret == 0)
            {
               String file = filedia.getSelectedFile().getAbsolutePath();
               if (file != null && ! file.isBlank())
               {
                  HashMap<String, String> hm = UtilPrefs.importUserPreferences(file);
                  if (null != hm)
                  {
                     m_userPrefsPanel.setAllSettingValues(hm);
                     JModalDialog.showMessage("Import", "User Preferences imported successfully.");
                  }
                  else
                  {
                     JModalDialog.showError("Import", "User Preferences import failed.\nPlease check console output.");
                  }
               }
            }
         }
      });
      headerPanel.add(jbImport);

      JButton jbExport = new JButton("Export");
      jbExport.setToolTipText("Export current shown application User Preferences to a file.");
      jbExport.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            // export a settings file
            JFileChooser filedia = new JFileChooser();
            filedia.setDialogType(JFileChooser.SAVE_DIALOG);
            filedia.setCurrentDirectory(new File(System.getProperty("user.home", Starter.APPLICATION_DATA_DIR)));
//            filedia.setFileFilter(new FileNameExtensionFilter("Settings File [exp]", "exp"));
            int ret = filedia.showSaveDialog(m_userPrefsPanel);
            if (ret == 0)
            {
               String file = filedia.getSelectedFile().getAbsolutePath();
               if (file.lastIndexOf(".") == -1)
               {
                  file = file + ".exp";
               }
               if (file != null && !(file.equals("")))
               {
                  boolean rc = UtilPrefs.exportUserPreferences(file, m_userPrefsPanel.getAllValues());
                  if (rc)
                  {
                     JModalDialog.showMessage("Export", "User Preferences exported successfully.");
                  }
                  else
                  {
                     JModalDialog.showError("Export", "User Preferences export failed.\nPlease check console output.");
                  }
               }
            }
         }
      });
      headerPanel.add(jbExport);

      JButton jbReset = new JButton("Reset All");
      jbReset.setToolTipText("Reset all current shown application User Preferences to their default values.");
      jbReset.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            // reset settings
            m_userPrefsPanel.setAllSettingValues(null);
         }
      });
      headerPanel.add(jbReset);

      getContentPane().add(headerPanel,BorderLayout.PAGE_START);

      m_userPrefsPanel = new JSettingsPanel(title, settingsPanelFields, UtilPrefs.getUserPreferencesDataSet());

      getContentPane().add(m_userPrefsPanel,BorderLayout.CENTER);
       
      //Buttons
      JPanel buttonsPanel = new JPanel();
      buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
      StringTokenizer strtok = new StringTokenizer(BUTTON_LIST,",");
      while (strtok.hasMoreTokens())
      {
         JButton button = new JButton(strtok.nextToken());
         button.addActionListener(this);
         buttonsPanel.add(button);
      }
      getContentPane().add(buttonsPanel,BorderLayout.PAGE_END);

      Starter.setCursorCanChange(true);
      Starter.resetWaitCursor();

      pack();
      setVisible(true);
      repaint();
   }

   @Override
   public void actionPerformed(ActionEvent e)
   {
      m_result = e.getActionCommand();
      close();
   }

   private void close()
   {
      Starter.setSkipWindowGainedFocus();
      setVisible(false);
      dispose();
   }

   public int getResult()
   {
      int iCnt = 0;

      StringTokenizer strtok = new StringTokenizer(BUTTON_LIST, ",");
      while (strtok.hasMoreTokens())
      {
         if (strtok.nextToken().equals(m_result))
         {
            break;
         }
         iCnt++;
      }

      switch (iCnt)
      {
         case SAVE_BUTTON :
            Starter._m_logError.TraceDebug("Save changed User Preferences.");
            // Update User Preferences
            Starter.setWaitCursor();
            Starter.setCursorCanChange(false);
            UtilPrefs.setUserPreferencesDataSet(m_userPrefsPanel.getAllValues());
            Starter.setCursorCanChange(true);
            Starter.resetWaitCursor();
            break;

         case CANCEL_BUTTON :
            Starter._m_logError.TraceDebug("Cancel changed User Preferences.");

         default :
            break;
      }

      return iCnt;
   }

}
