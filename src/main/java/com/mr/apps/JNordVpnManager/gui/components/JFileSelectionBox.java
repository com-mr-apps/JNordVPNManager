/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.components;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.mr.apps.JNordVpnManager.Starter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Title:        JFileSelectionBox
 * Description:  Area for drag&drop a file and a button that opens a file requester.
 * @author mr
 * @version 1.0
 */
@SuppressWarnings("serial")
public class JFileSelectionBox extends JPanel
{
   private JTextArea m_fileDropArea               = null;
   private JButton   m_jbCopy                     = null;
   private File      m_fpSelectedFile             = null;
   private File      m_fileDialogCurrentDirectory = null;
   private String    m_fileFilter                 = null;

   /**
    * Constructor for File Selection Box
    * 
    * @param fileDialogCurrentDirectory
    *           is the optional current directory for the file selection dialog
    * @param fileFilter
    *           is the optional filter for the file selection dialog
    */
   public JFileSelectionBox(File fileDialogCurrentDirectory, String fileFilter)
   {
      super();

      m_fileDialogCurrentDirectory = fileDialogCurrentDirectory;
      m_fileFilter = fileFilter;

      this.setLayout(new BorderLayout());
      this.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

      m_jbCopy = new JButton("...");
      m_jbCopy.setToolTipText("Select file...");
      m_jbCopy.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e)
         {
            cbFileSelection(e);
         }
      });

      m_fileDropArea = new JTextArea("Drop file here");
            m_fileDropArea.setBackground(new Color(247, 217, 146));
      m_fileDropArea.setEditable(false);
      m_fileDropArea.setDropTarget(new DropTarget() {
         public synchronized void drop(DropTargetDropEvent evt)
         {
            try
            {
               evt.acceptDrop(DnDConstants.ACTION_COPY);
               @SuppressWarnings("unchecked")
               List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
               for (File file : droppedFiles)
               {
                  // process files
                  Starter._m_logError.TraceDebug("Dropped file: " + file);
                  m_fpSelectedFile = file;
                  m_fileDropArea.setText(file.getName());
                  break; // process only one file!
               }
               evt.dropComplete(true);
            }
            catch (Exception ex)
            {
               Starter._m_logError.LoggingExceptionMessage(4, 10200, ex);
            }
         }
      });

      // Layout JPanel
      this.add(m_fileDropArea, BorderLayout.CENTER);
      this.add(m_jbCopy, BorderLayout.LINE_END);
   }

   /**
    * Constructor for File Selection Box
    */
   public JFileSelectionBox()
   {
      this(null, null);
   }

   /**
    * Action, when Select File button is pressed.<p>
    * Open a file requester to select a file.
    * 
    * @param event
    *           is the action event
    */
   public void cbFileSelection(ActionEvent event)
   {
      JFileChooser filedia = new JFileChooser();
      filedia.setDialogType(JFileChooser.OPEN_DIALOG);
      if (null != m_fileDialogCurrentDirectory)
      {
         filedia.setCurrentDirectory(m_fileDialogCurrentDirectory);
      }
      else
      {
         filedia.setCurrentDirectory(new File(System.getProperty("user.home")));
      }
      if (null != m_fileFilter)
      {
         Pattern pattern = Pattern.compile("\\s*[^\\[]+\\[([^\\]]+)\\]+",
               Pattern.CASE_INSENSITIVE);
         Matcher matcher = pattern.matcher(m_fileFilter);
         boolean matchFound = matcher.find();
         if (matchFound)
         {
            filedia.setFileFilter(new FileNameExtensionFilter(m_fileFilter, matcher.group(1)));
         }
      }
      int ret = filedia.showOpenDialog(Starter.getMainFrame());
      if (ret == 0)
      {
         File file = filedia.getSelectedFile();
         if (file != null && file.canRead())
         {
            m_fileDropArea.setText(file.getName());
            m_fpSelectedFile = file;
         }
         else
         {
            Starter._m_logError.TraceDebug("Selected file not valid.");
         }
      }
   }

   /**
    * Get the selected file
    * @return the selected file (or <code>null</> if nothing is selected.
    */
   public File getSelectedFile()
   {
      return m_fpSelectedFile;
   }
}
