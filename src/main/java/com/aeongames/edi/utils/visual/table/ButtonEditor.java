/*
 * Copyright 2008-2011 Eduardo Vindas C
 * Created on Jan 25, 2010, 11:11:03 PM
 */
package com.aeongames.edi.utils.visual.table;

import java.awt.Component;
import java.awt.event.*;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;

/**
 *this class is a DefaultCellEditor that allow to add a button within a Jtable
 * this allow the usage of buttons for our required task 
 * @author Eduardo Vindas Cordoba <cartman@aeongames.com>
 */
public abstract class ButtonEditor extends DefaultCellEditor {
  protected JButton button;
  protected String showwhenpress="";
  private boolean isPushed;
  private String value;

  public ButtonEditor(JCheckBox checkBox) {
    super(checkBox);
    button = new JButton();
    button.setOpaque(true);
    ActionListener list= new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        fireEditingStopped();
      }
    };
    button.addActionListener(list);
  }
  public ButtonEditor() {
    super(new JCheckBox());
    button = new JButton();
    button.setOpaque(true);
    ActionListener list= new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        fireEditingStopped();
      }
    };
    button.addActionListener(list);
  }

    @Override
  public Component getTableCellEditorComponent(JTable table, Object value,
      boolean isSelected, int row, int column) {
    if (isSelected) {
      button.setForeground(table.getSelectionForeground());
      button.setBackground(table.getSelectionBackground());
    } else {
      button.setForeground(table.getForeground());
      button.setBackground(table.getBackground());
    }
    this.value=value.toString();
    button.setText(showwhenpress+this.value);
    isPushed = true;
    return button;
  }

    @Override
  public Object getCellEditorValue() {
    if (isPushed) {
     action_triggered(value);
    }
    isPushed = false;
    return value;
  }

    @Override
  public boolean stopCellEditing() {
    isPushed = false;
    return super.stopCellEditing();
  }

    @Override
  protected void fireEditingStopped() {
    super.fireEditingStopped();
  }

    protected abstract void action_triggered(String value);
}
