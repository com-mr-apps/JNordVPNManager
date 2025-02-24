/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com-mr-apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.utils.String;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

public class StringFormat
{

   // some locale have a different minus sign representation (unicode) which causes on (re)conversion from string to number problems.
   // we force here the number format (in the converted string) to the US locale with minus sign "-" and decimal point ".".
   private static DecimalFormatSymbols _m_decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
   private static String               DEFAULT_INTEGER_FORMAT  = "0";
   private static String               DEFAULT_DECIMAL_FORMAT  = "###.######";

   /**
    * Print a string and check for empty or null.
    * 
    * @param sString
    *           is the string to format
    * @param sEmptyValue
    *           is the string that is returned in case of empty sString (required!)
    * @param sNullValue
    *           is the string that is returned in case of sString = <CODE>null</CODE>. If not set, sEmptyValue is used.
    * @return the formatted string
    */
   public static String printString(String sString, String sEmptyValue, String sNullValue)
   {
      if (sString == null)
      {
         // in case of null return the NullValue or if not set the EmptyValue
         return (sNullValue == null) ? sEmptyValue : sNullValue;
      }
      else if (sString.trim().length() == 0)
      {
         // in case of empty return the EmptyValue
         return sEmptyValue;
      }
      else
      {
         // return the string unchanged
         return sString;
      }
   }

   /**
    * Print a string and check for null.
    * 
    * @param sString
    *           is the string to format
    * @param sNullValue
    *           is the string that is returned in case of sString = <CODE>null</CODE>
    * @return the formatted string
    */
   public static String printString(String sString, String sNullValue)
   {
      return (sString == null) ? sNullValue : sString;
   }

   /**
    * Convert an integer value to a String, using a decimal format.
    * @param iVal is the value to be formatted
    * @param sFormat is the format e.g. "#0"
    * @return the formatted string
    */
   public static String int2String(int iVal, String sFormat)
   {
      String oString = null;

      if ((sFormat == null) || (true == sFormat.isBlank())) sFormat = DEFAULT_INTEGER_FORMAT;
      DecimalFormat f = new DecimalFormat(sFormat, _m_decimalFormatSymbols);
      oString = f.format(iVal);

      return oString;
   }

   /**
    * Convert an integer array to a String, using a decimal format.
    * @param intArr is the array to be formatted
    * @param sFormat is the format e.g. "#0"
    * @return the formatted string
    */
   public static String int2String (ArrayList<Integer> intArr, String sFormat)
   {
      StringBuffer oString = new StringBuffer();
      oString.append("[");

      if (null != intArr)
      {
         if ((sFormat == null) || (true == sFormat.isBlank())) sFormat = DEFAULT_INTEGER_FORMAT;
         DecimalFormat f = new DecimalFormat(sFormat, _m_decimalFormatSymbols);
         for (int iVal : intArr)
         {
            if (oString.length() > 1) oString.append(",");
            oString.append(f.format(iVal));
         }
      }
      oString.append("]");

      return oString.toString();
   }

   /**
    * Convert an integer value to a String, using a decimal format.
    * @param lVal is the value to be formatted
    * @param sFormat is the format e.g. "#0"
    * @return the formatted string
    */
   public static String long2String(long lVal, String sFormat)
   {
      String oString = null;

      if ((sFormat == null) || (true == sFormat.isBlank())) sFormat = DEFAULT_INTEGER_FORMAT;
      DecimalFormat f = new DecimalFormat(sFormat, _m_decimalFormatSymbols);
      oString = f.format(lVal);

      return oString;
   }

   /**
    * Convert a double value to a String, using a decimal format.
    * @param dVal is the value to be formatted
    * @param sFormat is the format e.g. "#0.0##". If empty, use the format "###.######"
    * @return the formatted string
    */
   public static String number2String(double dVal, String sFormat)
   {
      String oString = null;

      if ((sFormat == null) || (true == sFormat.isBlank())) sFormat = DEFAULT_DECIMAL_FORMAT;
      DecimalFormat f = new DecimalFormat(sFormat, _m_decimalFormatSymbols);

      if (dVal == 0.0) dVal = 0.0; // remove the - from -0.0...
      oString = f.format(dVal);
      return oString;
   }

   /**
    * Convert a string value to a Double, using a decimal format.
     * @return the double value
    */
   public static Double string2number(String sVal)
   {
      Double oDouble = 0.0;

      if (null != sVal)
      {
         sVal = sVal.replace(',', '.');
         oDouble = Double.parseDouble(sVal);
      }
      return oDouble;
   }
   
   /**
    * Return the boolean value of a string values representing a boolean value
    * <p>
    * Valid boolean string values are 1|true|enable|on|enabled or 0|false|disable|off|disabled
    * 
    * @param value
    *           is the String value that represents a boolean
    * @return true if the string values represents true, else false
    */
   public static boolean string2boolean(String value)
   {
      if (null == value) return false;
      return value.matches("1|[Tt]rue|[Ee]nable[d]?|[Oo]n");
   }


   /**
    * Check, if two string values representing boolean values are equal
    * <p>
    * Valid boolean string values are 1|true|enable|on|enabled or 0|false|disable|off|disabled
    * 
    * @param value1
    *           is the first String value that represents a boolean
    * @param value2
    *           is the second String value that represents a boolean
    * @return true if two string values are equal
    */
   public static boolean equalBoolean(String value1, String value2)
   {
      return StringFormat.string2boolean(value1) == StringFormat.string2boolean(value2);
   }
}
