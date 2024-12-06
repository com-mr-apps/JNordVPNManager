/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.gui.dialog.JModalDialog;
import com.mr.apps.JNordVpnManager.nordvpn.NvpnSettingsData;

@SuppressWarnings("serial")
public class JNordVpnSettingsDialog extends JDialog implements ActionListener
{
   private final static String BUTTON_LIST = "Set,Reset All [to NordVPN Defaults],Cancel";
   private final static int SET_BUTTON = 0;
   private final static int RESET_BUTTON = 1;
   private final static int CANCEL_BUTTON = 2;

   private String m_result;
   private boolean m_requiresUpdate = false;
   private JSettingsPanel m_nvpnSettingsPanel = null;
   private NvpnSettingsData m_nvpnSettingsData = null;

   /**
    * NordVPN Settings Panel Layout definition.
    */
   public JNordVpnSettingsDialog(Frame owner, String title, Map<String, JSettingsPanelField> settingsPanelFieldsMap)
   {
      super(owner, title, true);

      Starter.setWaitCursor();
      Starter.setCursorCanChange(false);

      // get the current NordVPN Settings data
      m_nvpnSettingsData = Starter.getCurrentSettingsData();

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

      JButton jbSave = new JButton("Save UserPrefs");
      jbSave.setToolTipText("Save current shown NordVPN Settings to local User Preferences.");
      jbSave.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            // Save panel settings to user preferences
            
            HashMap<String, String> hm = m_nvpnSettingsPanel.getAllValues();
            m_nvpnSettingsData.setSettingsDataSet(hm, true);
            JModalDialog.showMessage("Save", "NordVPN Settings successfully saved to User Preferences.");
          }
      });
      headerPanel.add(jbSave);

      JButton jbLoad = new JButton("Load UserPrefs");
      jbLoad.setToolTipText("Load current shown NordVPN Settings from local User Preferences.");
      jbLoad.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            // Save panel settings to user preferences
            HashMap<String, String> hm = m_nvpnSettingsData.getSettingsDataSet(true);
            m_nvpnSettingsPanel.setAllSettingValues(hm);
            JModalDialog.showMessage("Load", "NordVPN Settings successfully loaded from User Preferences.");
          }
      });
      headerPanel.add(jbSave);

      JButton jbImport = new JButton("Import");
      jbImport.setToolTipText("Import NordVPN Settings from a file.");
      jbImport.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            // import a settings file
            JFileChooser filedia = new JFileChooser();
            filedia.setDialogType(JFileChooser.OPEN_DIALOG);
            filedia.setCurrentDirectory(new File(System.getProperty("user.home")));
//          filedia.setFileFilter(new FileNameExtensionFilter("Settings File [exp]", "exp"));
            int ret = filedia.showOpenDialog(m_nvpnSettingsPanel);
            if (ret == 0)
            {
               String file = filedia.getSelectedFile().getAbsolutePath();
               if (file != null && ! file.isBlank())
               {
                  HashMap<String, String> hm = m_nvpnSettingsData.importNordVpnSettings(file);
                  if (null != hm)
                  {
                     m_nvpnSettingsPanel.setAllSettingValues(hm);
                     JModalDialog.showMessage("Import", "NordVPN Settings imported successfully.");
                  }
                  else
                  {
                     JModalDialog.showError("Import", "NordVPN Settings import failed.\nPlease check console output.");
                  }
               }
            }
         }
      });
      headerPanel.add(jbImport);

      JButton jbExport = new JButton("Export");
      jbExport.setToolTipText("Export current shown NordVPN Settings to a file.");
      jbExport.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            // export a settings file
            JFileChooser filedia = new JFileChooser();
            filedia.setDialogType(JFileChooser.SAVE_DIALOG);
            filedia.setCurrentDirectory(new File(System.getProperty("user.home")));
//            filedia.setFileFilter(new FileNameExtensionFilter("Settings File [exp]", "exp"));
            int ret = filedia.showSaveDialog(m_nvpnSettingsPanel);
            if (ret == 0)
            {
               String file = filedia.getSelectedFile().getAbsolutePath();
               if (file.lastIndexOf(".") == -1)
               {
                  file = file + ".exp";
               }
               if (file != null && !(file.equals("")))
               {
                  boolean rc = m_nvpnSettingsData.exportNordVpnSettings(file, m_nvpnSettingsPanel.getAllValues());
                  if (rc)
                  {
                     JModalDialog.showMessage("Export", "NordVPN Settings exported successfully.");
                  }
                  else
                  {
                     JModalDialog.showError("Export", "NordVPN Settings export failed.\nPlease check console output.");
                  }
               }
            }
         }
      });
      headerPanel.add(jbExport);

      getContentPane().add(headerPanel,BorderLayout.PAGE_START);

      // ---------------------------------------------------------------------------------------------
      // Settings
      // ---------------------------------------------------------------------------------------------
      m_nvpnSettingsPanel = new JSettingsPanel(title, settingsPanelFieldsMap, m_nvpnSettingsData.getSettingsDataSet(false));
      getContentPane().add(m_nvpnSettingsPanel,BorderLayout.CENTER);
       
      // ---------------------------------------------------------------------------------------------
      //Buttons
      // ---------------------------------------------------------------------------------------------
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

      m_requiresUpdate = false;
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
         case SET_BUTTON :
            Starter._m_logError.TraceDebug("Set changed NordVPN Settings.");
            Starter.setWaitCursor();
            Starter.setCursorCanChange(false);
            m_requiresUpdate = m_nvpnSettingsData.setSettingsDataSet(m_nvpnSettingsPanel.getAllValues(), false);
            if (m_requiresUpdate)
            {
               // in case of update required (settings changed) we force the Windows Gained Focus which triggers an Data and GUI update
               Starter.setForceWindowGainedFocus();         
            }
            Starter.setCursorCanChange(true);
            Starter.resetWaitCursor();
            break;

         case RESET_BUTTON :
            Starter._m_logError.TraceDebug("Set changed NordVPN Settings.");
            if (JModalDialog.showConfirm("'nordvpn set defaults' will be executed.\nThis command will reset all Settings to the NordVPN Defaults.\nDo you want to continue?") == JOptionPane.YES_OPTION)
            {
               // reset settings
               Starter.setWaitCursor();
               Starter.setCursorCanChange(false);
               m_nvpnSettingsData.resetNordVPNSettingsValues();
               HashMap<String, String> hm = m_nvpnSettingsData.getSettingsDataSet(false);
               m_nvpnSettingsPanel.setAllSettingValues(hm);
               Starter.setForceWindowGainedFocus();         
               Starter.setCursorCanChange(true);
               Starter.resetWaitCursor();
            }
            break;

         case CANCEL_BUTTON :
            Starter._m_logError.TraceDebug("Cancel changed NordVPN Settings.");

         default :
            break;
      }

      return iCnt;
   }
}
