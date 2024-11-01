/* Copyright (C) 2024 com.mr.apps - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the “Commons Clause” License Condition v1.0 and the
 * Common Development and Distribution License 1.0.
 *
 * You should have received a copy of the “Commons Clause” license with
 * this file. If not, please visit: https://github.com/com.mr.apps/JNordVpnManager
 */
package com.mr.apps.JNordVpnManager.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;


 public class JModalDialog extends JDialog implements ActionListener
 {
    /**
    * 
    */
   private static final long serialVersionUID = -6232586465653751587L;
   String result;
    String m_buttons;

/*
    public static void htmlDialog(Frame owner, String headline, String msg, String button_text, String url)
    {
       ModalDialog dlg;
       dlg = new ModalDialog(owner,headline,"nix",button_text,url);
       dlg.setVisible(true);
       //return dlg.getResult();
    }
*/

    public static int OKDialog(Dialog owner,String headline, String msg, String button_text)
    {
       JModalDialog dlg;
       dlg = new JModalDialog(owner,headline,msg,button_text);
       dlg.setVisible(true);
       dlg.repaint();
       return dlg.getResult();
    }

    public static int OKDialog(Frame owner, String headline, String msg, String button_text)
    {
       JModalDialog dlg;
       dlg = new JModalDialog(owner,headline,msg,button_text);
       dlg.setVisible(true);
       dlg.repaint();
       return dlg.getResult();
    }

    public static int OKDialog(Frame owner, String headline, String[] msg, String button_text)
    {
       JModalDialog dlg;
       dlg = new JModalDialog(owner,headline,msg,button_text);
       dlg.setVisible(true);
       dlg.repaint();
       return dlg.getResult();
    }
    public static int OKDialog(String headline, String msg, String button_text)
    {
       System.out.println("Anlegen des Dialog 4+ "+ msg);
       JModalDialog dlg;
       dlg = new JModalDialog(headline,msg,button_text);
       dlg.setVisible(true);
       dlg.repaint();
       return dlg.getResult();
    }

    public int OKDialog(Frame owner, String msg)
    {
       System.out.println("Anlegen des Dialog 5+ "+ msg);
       JModalDialog dlg;
       dlg = new JModalDialog(owner,"Message",msg,"OK");
       dlg.setVisible(true);
       dlg.repaint();
       return dlg.getResult();
    }
    
    public static int YesNoDialog(Dialog owner, String msg)
    {
       JModalDialog dlg;
       dlg = new JModalDialog(owner,"Question",msg,"Yes,No");
       dlg.setVisible(true);
       dlg.repaint();
       return dlg.getResult();
    }
    
    public static int YesNoDialog(Frame owner, String msg)
    {
       JModalDialog dlg;
       dlg = new JModalDialog(owner,"Question",msg,"Yes,No");
       dlg.setVisible(true);
       dlg.repaint();
       return dlg.getResult();
    }


/*
    public static int YesNoDialog(Frame owner, String headline, String[] msg, String button_text)
    {
       ModalDialog dlg;
       dlg = new ModalDialog(owner, headline ,msg, button_text);
       dlg.setVisible(true);
       return dlg.getResult();
    }
*/

    public int YesNoCancelDialog(Frame owner,String msg)
    {
       JModalDialog dlg;
       dlg = new JModalDialog(owner,"Question",msg,"Yes,No,Cancel");
       dlg.setVisible(true);
       dlg.repaint();
       return dlg.getResult();
    }



    public JModalDialog(Frame owner, String title, String msg, String buttons)
    {
       super(owner, title, true);
       //System.out.println("Mouse pos ="+owner.getMousePosition().toString());
       m_buttons = new String(buttons);
       //Fenster
       setBackground(Color.lightGray);
       getContentPane().setLayout(new BorderLayout());
       setResizable(false);
       Point parloc = owner.getLocation();
       setLocation(parloc.x + 30, parloc.y + 30);
       //Message
       getContentPane().add("Center", new Label(msg));
       //Buttons
       JPanel panel = new JPanel();
       panel.setLayout(new FlowLayout(FlowLayout.CENTER));
       StringTokenizer strtok = new StringTokenizer(buttons,",");
       while (strtok.hasMoreTokens())
       {
          JButton button = new JButton(strtok.nextToken());
          button.addActionListener(this);
          panel.add(button);
       }
       getContentPane().add("South", panel);
       pack();
    }

    public JModalDialog(Dialog owner,String title, String msg, String buttons)
    {
       super(owner, title, true);
       //System.out.println("Mouse pos ="+owner.getMousePosition().toString());
       m_buttons = new String(buttons);

       //Fenster
       setBackground(Color.lightGray);
       getContentPane().setLayout(new BorderLayout());
       setResizable(false);
       Point parloc = owner.getLocation();
       setLocation(parloc.x + 40, parloc.y + 60);
       //Message
       getContentPane().add("Center", new Label(msg));
       //Buttons
       JPanel panel = new JPanel();
       panel.setLayout(new FlowLayout(FlowLayout.CENTER));
       StringTokenizer strtok = new StringTokenizer(buttons, ",");
       while (strtok.hasMoreTokens())
       {
          JButton button = new JButton(strtok.nextToken());
          button.addActionListener(this);
          panel.add(button);
       }
       getContentPane().add("South", panel);
       pack();
    }

    public JModalDialog( String Title , String msg , String Button )
    {
       super( );
       m_buttons = new String(Button);
       
       //Fenster
       setBackground(Color.lightGray);
       getContentPane().setLayout(new BorderLayout());
       setResizable(false);
       //Messages
       JPanel msg_lines=new JPanel();
       msg_lines.add(new Label(msg));
       getContentPane().add("Center", msg_lines);
       //Buttons
       JPanel panel = new JPanel();
       panel.setLayout(new FlowLayout(FlowLayout.CENTER));
       JButton button = new JButton(Button);
       button.addActionListener(this);
       panel.add(button);
       getContentPane().add("South", panel);
       pack();
       
    }

    public JModalDialog(Frame owner, String title, String[] msg, String buttons)
    {
       super(owner, title, true);
       m_buttons = new String(buttons);
       //System.out.println("Mouse pos ="+owner.getMousePosition().toString());
       //Fenster
       setBackground(Color.lightGray);
       getContentPane().setLayout(new BorderLayout());
       setResizable(false);
       Point parloc = owner.getLocation();
       setLocation(parloc.x + 40, parloc.y + 60);
       //Messages
       JPanel msg_lines=new JPanel();
       msg_lines.setLayout(new GridLayout(msg.length,1));
       for (int i=0;i<msg.length;i++)
       {
         msg_lines.add(new Label(msg[i]));
       }
       getContentPane().add("Center", msg_lines);
       //Buttons
       JPanel panel = new JPanel();
       panel.setLayout(new FlowLayout(FlowLayout.CENTER));
       StringTokenizer strtok = new StringTokenizer(buttons, ",");
       while (strtok.hasMoreTokens())
       {
          JButton button = new JButton(strtok.nextToken());
          button.addActionListener(this);
          panel.add(button);
       }
       getContentPane().add("South", panel);
       pack();
    }

    public void actionPerformed(ActionEvent event)
    {
       result = event.getActionCommand();
       setVisible(false);
       dispose();
    }

    public int getResult()
    {
       int iCnt = 0;

       StringTokenizer strtok = new StringTokenizer(m_buttons, ",");
       while (strtok.hasMoreTokens())
       {
          if (strtok.nextToken().equals(result))
          {
             return (iCnt);
          }
          iCnt++;
       }
       return 0;
    }
/*
    public String getResult()
    {
       return result;
    }
*/
 }
