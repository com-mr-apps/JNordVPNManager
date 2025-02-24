/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.utils.String;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class is a utility to split strings.
 * <p>
 * Created on May 14, 2014, 01:33 PM
 * 
 * @author MRe
 * @version 1.0
 * <BR>$Revision: 1.1 $
 * <BR>$Date: 2014/05/15 18:00:09MESZ $
 */

public class Split
{

   private static String m_delimiter = null;

   /**
    * Method to split an argument list with quoted text and ignoring leading and trailing blanks.<p>
    * e.g. the argument string (delimiter is comma):<br>
    *    "a,aa "  ,'"bb', cc,  dd  ,  ,,<br>
    * will return 7 arguments:<br>
    *  &gt;a,aa &lt; &gt;"bb&lt; &gt;cc&lt; &gt;dd&lt; &gt;&lt; &gt;&lt; &gt;&lt;<br> 
    * @param inString the arguments string
    * @param delimiter the list of delimiters
    * @return the list of arguments
    */
   public static String[] splitQuoted(String inString, String delimiter)
   {
      String [] saRc = null;

      /*
       *  check arguments
       */
      if ((inString == null) || (inString.trim().length() <= 0))
      {
         // empty inString --> return empty string
         saRc = new String[1];
         saRc[0] = "";
         return saRc;
      }

      if ((delimiter == null) || (delimiter.length() <= 0))
      {
         // empty delimiter --> return the inString
         saRc = new String[1];
         saRc[0] = inString;
         return saRc;
      }

      // set member variables for utility methods
      m_delimiter = delimiter;

      /*
       *  split the string
       */
      String sArgsString = inString.trim();

      char quotaSign = '\0';
      int von = 0;
      int bis = -1;

      ArrayList<String> slSplit = new ArrayList<String>();
      for (int i = 0; i < sArgsString.length(); i++)
      {
         // if we are outside quotas, ignore blanks
         if ((i == von) && (quotaSign == '\0') && (sArgsString.charAt(i) == ' '))
         {
            von++;
            continue;
         }

         if ((quotaSign == '\0') && (i == von) && ((sArgsString.charAt(i) == '"') || (sArgsString.charAt(i) == '\'')))
         {
            // start of quoted argument
            quotaSign = sArgsString.charAt(i);
            von++;
            bis = von;
            continue;
         }

         if ((quotaSign != '\0') && (sArgsString.charAt(i) == quotaSign))
         {
            // we are now at the end of an argument in quota, add the argument to the list
            quotaSign = '\0';
            bis = i;
            if (von < bis)
            {
               slSplit.add(sArgsString.substring(von, bis));
            }
            else if (von == bis)
            {
               slSplit.add("");
            }
            bis = -1;

            // skip all following blanks until start of next argument, delimiter (or end of string) must follow
            i++;
            for (; i < sArgsString.length(); i++)
            {
               if (sArgsString.charAt(i) != ' ' && (isDelimiter(sArgsString.charAt(i)) == false)) throw new RuntimeException("Incorrect quoted string value!");
               if (isDelimiter(sArgsString.charAt(i)) == true)
               {
                  // set the end position for the next argument after the delimiter
                  bis = i+1;
                  break;
               }
            }
            von = i + 1;
            continue;
         }

         if ((isDelimiter(sArgsString.charAt(i)) == false) && (quotaSign == '\0') && (sArgsString.charAt(i) != ' '))
         {
            // if we are outside quotas, we ignore blanks at the end of an argument
            bis = i+1;
         }
         if ((isDelimiter(sArgsString.charAt(i)) == true) && (quotaSign == '\0'))
         {
            // delimiter outside quota found --> end of argument
            if (von < i)
            {
               slSplit.add(sArgsString.substring(von, i));
            }
            else if (von == i)
            {
               slSplit.add("");
            }

            // set position after delimiter
            von = i + 1;
            bis = von;
         }
      }

      if (von < bis)
      {
         slSplit.add(sArgsString.substring(von, bis));
      }
      else if (von == bis)
      {
         slSplit.add("");
      }

      // copy the arguments to the return string field
      saRc = new String [slSplit.size()];
      for (int iCnt = 0; iCnt < slSplit.size(); iCnt ++)
      {
         saRc[iCnt] = slSplit.get(iCnt);
      }
      
      return saRc;
   }
   
   private static boolean isDelimiter (char character)
   {
      for (int iCnt = 0; iCnt < m_delimiter.length(); iCnt++)
      {
         if ((character == m_delimiter.charAt(iCnt)))
         {
            return true;
         }
      }
      return false;
   }

   public static void test ()
   {
      String [] callStringSplit = Split.splitQuoted("'ab,c'", ",");
      System.out.println(Arrays.toString(callStringSplit));
      callStringSplit = Split.splitQuoted("abc   '  ", ",");
      System.out.println(Arrays.toString(callStringSplit));
      callStringSplit = Split.splitQuoted(" 'abc' ", ",");
      System.out.println(Arrays.toString(callStringSplit));
      callStringSplit = Split.splitQuoted("'abc'  ,  def  ,,", ",");
      System.out.println(Arrays.toString(callStringSplit));
      callStringSplit = Split.splitQuoted("'abc',def,\"gh\"  ", ",");
      System.out.println(Arrays.toString(callStringSplit));
      callStringSplit = Split.splitQuoted("''", ",");
      System.out.println(Arrays.toString(callStringSplit));
      callStringSplit = Split.splitQuoted(",", ",");
      System.out.println(Arrays.toString(callStringSplit));

   }
}
