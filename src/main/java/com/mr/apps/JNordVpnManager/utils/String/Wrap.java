/* Copyright (C) 2025 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.utils.String;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Wrap
{

   // Define a "long" word - (text, like html-links that will start in a new line if they break the line limit)
   private final static int LONG_WORDS = 30;

   /**
    * Wraps a source String into a series of lines having a maximum specified length. The source is wrapped at: spaces,
    * horizontal tabs, system newLine characters, or a specified newLine character sequence. Existing newLine character
    * sequences in the source string, whether they be the system newLine or the specified newLine, are honored. Existing
    * whitespace (spaces and horizontal tabs) is preserved.
    * <p>
    * When <tt>wrapLongWords</tt> is true, words having a length greater than the specified <tt>lineLength</tt> will be
    * broken, the specified <tt>longWordBreak</tt> terminator appended, and a new line initiated with the text of the
    * specified <tt>longWordLinePrefix</tt> string. The position of the break will be unceremoniously chosen such that
    * <tt>lineLength</tt> is honored. One use of <tt>longWordLinePrefix</tt> is to effect "hanging indents" by specifying
    * a series of spaces for this parameter. This parameter can contain the lineFeed character(s). Although
    * <tt>longWordLinePrefix</tt> can contain the horizontal tab character, the results are not guaranteed because no
    * attempt is made to determine the quantity of character positions occupied by a horizontal tab.<br>
    * (mr2025)Words that are longer than the defined <tt>lineLength<tt> will start with the first part in a new line.
    * </p>
    * <p>
    * Example usage:
    * 
    * <pre>
    * wrap("  A very long word is Abracadabra in my book", 11, "\n", true, "-", "  ");
    * </pre>
    * 
    * returns (note the effect of the single-character lineFeed):
    * 
    * <pre>
    *   A very
    * long word
    * is 
    *   Abracada-
    *   bra in my 
    *   book
    * </pre>
    * 
    * Whereas, the following:
    * 
    * <pre>
    * wrap("  A very long word is Abracadabra in my book", 11, null, true, null, "  ");
    * </pre>
    * 
    * returns (due to the 2-character system linefeed):
    * 
    * <pre>
    *   A very
    * long
    * word is 
    *   Abracad
    *   abra in
    * my book
    * </pre>
    * </p>
    *
    * @param src
    *           the String to be word wrapped, may be null
    * @param lineLength
    *           the maximum line length, including the length of <tt>newLineStr</tt> and, when applicable,
    *           <tt>longWordLinePrefix</tt>. If the value is insufficient to accommodate these two parameters + 1
    *           character, it will be increased accordingly.
    * @param newLineStr
    *           the string to insert for a new line, or <code>null</code> to use the value reported as the system line
    *           separator by the JVM
    * @param wrapLongWords
    *           when <tt>false</tt>, words longer than <tt>wrapLength</t> will not be broken
    * @param longWordLinePostfix string with which to precede <tt>newLineStr</tt> on each line of a broken word, excepting the
    *           last line, or <tt>null</tt> if this feature is not to be used
    * @param longWordLinePrefix
    *           string with which to prefix each line of a broken word, subsequent to the first line, or <tt>null</tt>
    *           if no prefix is to be used
    * @return a line with newlines inserted, or <code>null</code> if <tt>src</tt> is null
    */
   public static String wrap(String src, int lineLength, String newLineStr, boolean wrapLongWords,
         String longWordLinePostfix, String longWordLinePrefix)
   {
      // Trivial cases
      if (src == null) return null;
      if (src.length() <= lineLength) return src;

      // default values
      if (newLineStr == null) newLineStr = System.getProperty("line.separator");
      if (longWordLinePostfix == null) longWordLinePostfix = "";
      if (longWordLinePrefix == null) longWordLinePrefix = "";

      // Adjust maximum line length to accommodate the newLine string
      lineLength -= newLineStr.length();
      if (lineLength < 1)
      {
         lineLength = 1;
      }

      // Guard for long word break or prefix that would create an infinite loop
      if (wrapLongWords && lineLength - longWordLinePostfix.length() - longWordLinePrefix.length() < 1)
      {
         lineLength += longWordLinePostfix.length() + longWordLinePrefix.length();
      }

      int remaining = lineLength;
      int breakLength = longWordLinePostfix.length();

      Matcher m = Pattern.compile(".+?[ \\t]|.+?(?:" + newLineStr + ")|.+?$").matcher(src);

      StringBuilder cache = new StringBuilder();

      while (m.find())
      {
         String word = m.group();

         // Breakup long word
         boolean first = true;
         while (wrapLongWords && word.length() > lineLength)
         {
            if (first && lineLength > LONG_WORDS)
            {
               // (mr2025) Break a [long]word exceeding lineLength - to start in a new line
               first = false;
               cache.append(longWordLinePostfix)
                    .append(newLineStr);
               remaining = lineLength;
            }
            cache.append(word.substring(0, remaining - breakLength))
                 .append(longWordLinePostfix)
                 .append(newLineStr);
            word = longWordLinePrefix + word.substring(remaining - breakLength);
            remaining = lineLength;
         } // while

         // Line feed if word exceeds remaining space
         if (word.length() > remaining)
         {
            cache.append(newLineStr)
                 .append(word);
            remaining = lineLength;
         } // if

         // Word fits in remaining space
         else
         {
            cache.append(word);
            if (word.endsWith(newLineStr))
            {
               remaining = lineLength;
            }
            else
            {
               remaining -= word.length();
            }
         }
      } // while

      return cache.toString();
   } // wrap()
}
