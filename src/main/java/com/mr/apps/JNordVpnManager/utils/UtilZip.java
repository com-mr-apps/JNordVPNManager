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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Vector;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.mr.apps.JNordVpnManager.utils.String.StringFormat;

public class UtilZip
{

   public static String unzipTmp(InputStream fileZip, String prefix) throws IOException
   {
      File destDir = File.createTempFile(prefix, "");
      destDir.delete(); // delete the created file and use the name as directory for the unzipped files

      intUnzip(fileZip, destDir.toString());
       
      return destDir.toString();
   }

   public static void unzip(InputStream fileZip, String destDir) throws IOException
   {
      // TODO: if destDir == null, create a default directory in the location of the ZIP file with name of the ZIP file (w/0 extension)
      intUnzip(fileZip, destDir);
   }

   /*
    * TODO: destDir - in case of isTemp==true, Id for delete
    */
   private static void intUnzip(InputStream fileZip, String destDir) throws IOException
   {
      File fpDestDir = new File(destDir);

      byte[] buffer = new byte[1024];
      try (ZipInputStream zis = new ZipInputStream(fileZip))
      {
         ZipEntry zipEntry = zis.getNextEntry();
         while (zipEntry != null)
         {
            while (zipEntry != null)
            {
               File newFile = newFile(fpDestDir, zipEntry);
               if (zipEntry.isDirectory())
               {
                  if (!newFile.isDirectory() && !newFile.mkdirs())
                  {
                     throw new IOException("Failed to create directory " + newFile);
                  }
               }
               else
               {
                  // fix for Windows-created archives
                  File parent = newFile.getParentFile();
                  if (parent.exists() && !parent.isDirectory())
                  {
                     throw new IOException("Error, File exists with name of directory " + parent);
                  }
                  if (!parent.exists() && !parent.mkdirs())
                  {
                     throw new IOException("Failed to create directory " + parent);
                  }

                  // write file content
                  FileOutputStream fos = new FileOutputStream(newFile);
                  int len;
                  while ((len = zis.read(buffer)) > 0)
                  {
                     fos.write(buffer, 0, len);
                  }
                  fos.close();
               }
               zipEntry = zis.getNextEntry();
            }
         }

         zis.closeEntry();
         zis.close();
      }
   }

   private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException
   {
      File destFile = new File(destinationDir, zipEntry.getName());

      String destDirPath = destinationDir.getCanonicalPath();
      String destFilePath = destFile.getCanonicalPath();

      if (!destFilePath.startsWith(destDirPath + File.separator))
      {
         throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
      }

      return destFile;
   }
   
   public static void unzipTmpCleanup(String tempDir)
   {
      File finename = new File(tempDir);
      Path pathToBeDeleted = finename.toPath();
      try (Stream<Path> paths = Files.walk(pathToBeDeleted))
      {
         paths.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   public static void unzipSplitResourceAsStream(Class<?> c, String inFile, String destDir) throws IOException
   {
      Vector<InputStream> inputStreams = new Vector<>();
      int iNumber = 1;
      String sNumber = "001";
      String fileName = inFile + "." + sNumber;
      InputStream inStream = c.getResourceAsStream(fileName);
      while (inStream != null)
      {
         inputStreams.add(inStream);
         ++iNumber;
         sNumber = StringFormat.int2String(iNumber, "000");
         fileName = inFile + "." + sNumber;
         inStream = c.getResourceAsStream(fileName);
      }
      SequenceInputStream sequenceInputStream = new SequenceInputStream(inputStreams.elements());
      intUnzip(sequenceInputStream, destDir);
   }
}
