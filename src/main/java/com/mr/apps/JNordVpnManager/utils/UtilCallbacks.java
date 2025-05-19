package com.mr.apps.JNordVpnManager.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

import com.mr.apps.JNordVpnManager.Starter;
import com.mr.apps.JNordVpnManager.commandInterfaces.base.CallCommand;
import com.mr.apps.JNordVpnManager.gui.dialog.JModalDialog;

public class UtilCallbacks
{
   /**
    * Callback to activate/deactivate the Supporter Edition
    * @return <code>true</code> if all is ok, <code>false</code> in case of error.
    */
   public static boolean cbManageSupporterEdition()
   {
      // 1st step
      JModalDialog dlg = JModalDialog.JDropFileSelectDialog("Manage Supporter Edition [Step 1 - Import Key File]",
            "Import Key file to activate the Supporter Edition:\n"
                  + "-> 1st - Drag&Drop key file in the box below or select the file with the '...' button.\n"
                  + "-> 2nd - 'Import Selected Key File' - to activate the Supporter Edition with the selected file.\n"
                  + "(Changes require a restart of the application)\n"
                  + "---\nor:\n"
                  + "* 'Cancel' - returns to the application.\n"
                  + "* 'Import Add-On Library' - Skip this step and continue with Import add-on library.\n"
                  + "* 'Reset to Basis Edition' - removes the Supporter Edition features.\n",
              "Cancel,Import Selected Key File,Import Add-On Library,Reset to Basis Edition", Starter.APPLICATION_DATA_ABS_PATH, "Key File [lic]");
      switch (dlg.getResult()) {
         case 1 :
            File fpFile = dlg.getSelectedFile();
            if (null != fpFile)
            {
               Preferences prefAddOns = Preferences.userRoot().node("com/mr/apps/JNordVpnManager/Settings/AddOns");
               Starter._m_logError.LoggingInfo("Import Supporter Edition key from file '" + fpFile + "'.");
               try (Stream<String> lines = Files.lines(Paths.get(fpFile.getPath())))
               {
                  int iLineNb = 0;
                  for (String line : (Iterable<String>) lines::iterator)
                  {
                     switch (iLineNb) {
                        case 0:
                           prefAddOns.put("Security.Key", line);
                           break;
                        case 1:
                           prefAddOns.put("Data.1", line);
                           break;
                     }
                     iLineNb++;
                  }
               }
               catch (IOException e1)
               {
                  Starter._m_logError.LoggingExceptionMessage(3, 10901, e1);
                  JModalDialog.showError("Key file import failed",
                           "The key file is invalid or could not be read.\n\n"
                           + "Please check the console for more information.");
                  return true;
               }
            }
            else
            {
               Starter._m_logError.LoggingInfo("Manage Supporter Edition: Skip import key file - no file selected.");
            }
            break;

         case 2 : // skip
            Starter._m_logError.LoggingInfo("Manage Supporter Edition: Skip import key file - continue with step 2.");
            break;

         case 3 : // remove add-on
            if (Starter.isSupporterEdition())
            {
               CallCommand.invokeAddonMethod("AddonManager", "reset");
               JModalDialog.showInfo("Removed Supporter Edition features.\n\nPlease restart the application.");
            }
            return true;

         default : // cancel
            Starter._m_logError.LoggingInfo("Manage Supporter Edition: Cancelled import key file.");
            return true;
      }

      // 2nd step
      dlg = JModalDialog.JDropFileSelectDialog("Manage Supporter Edition [Step 2 - Import Add-on library]",
            "Import add-on library file to the application add-on directory:\n"
            + "-> 1st - Drag&Drop library file in the box below or select the file with the '...' button.\n"
            + "-> 2nd - 'Import Selected Add-On Library' - imports the selected add-on library in the application add-ons folder.\n"
            + "(Changes require a restart of the application)\n"
            + "---\nor:\n"
            + "* 'Cancel' - returns to the application.\n"
            + "* 'Reset to Basis Edition' - removes the Supporter Edition features.\n",
            "Cancel,Import Selected Add-On Library,Reset to Basis Edition", Starter.APPLICATION_DATA_ABS_PATH, "Java Archive File [jar]");
      switch (dlg.getResult()) {
         case 1 : 
            try
            {
               File fpFile = dlg.getSelectedFile();
               if (null != fpFile)
               {
                  File fpAppAddonDir = new File(Starter.APPLICATION_DATA_ABS_PATH, "addons");
                  String jarFile = CallCommand.getAddonLibraryName();
                  File fpTarget = new File(fpAppAddonDir, jarFile);

                  Starter._m_logError.LoggingInfo("Manage Supporter Edition: file copy " + fpFile.getAbsolutePath() + " to " + fpTarget.getAbsolutePath());
                  Files.copy(fpFile.toPath(), fpTarget.toPath(), StandardCopyOption.REPLACE_EXISTING);
               }
               else
               {
                  Starter._m_logError.LoggingInfo("Manage Supporter Edition: Skip copy add-on library - no file selected.");
                  return true;
               }
            }
            catch (IOException e1)
            {
               Starter._m_logError.LoggingExceptionMessage(4, 10901, e1);
               return true;
            }
            break;

         case 2 :
            if (Starter.isSupporterEdition())
            {
               CallCommand.invokeAddonMethod("AddonManager", "reset");
               JModalDialog.showInfo("Removed Supporter Edition features.\nPlease restart the application.");
            }
            return true;

         default: // cancel
            Starter._m_logError.LoggingInfo("Manage Supporter Edition: Cancelled in copy add-on.");
            return true;
      }

      // check the key and access to add-on library
      if (CallCommand.initClassLoader(UtilPrefs.getAddonsPath()))
      {
         // ok
         JModalDialog.showInfo("Supporter Edition activated successfully.\n\nPlease restart the application.");
      }
      else
      {
         // test - ko
         JModalDialog.showWarning("Supporter Edition initialization failed:\n"
               + "Ensure that the key file is correct and the required add-on library is in the application add-on directory:\n"
               + "'" + Starter.APPLICATION_DATA_ABS_PATH + "'\n\n"
               + "Please check the console for more information and restart the application.");
      }

      return true;
   }
}
