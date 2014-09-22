// OO jDREW Version 0.89
// Copyright (c) 2005 Marcel Ball
//
// This software is licensed under the LGPL (LESSER GENERAL PUBLIC LICENSE) License.
// Please see "license.txt" in the root directory of this software package for more details.
//
// Disclaimer: Please see disclaimer.txt in the root directory of this package.

package jdrew.oo.gui;

import java.awt.*;

import javax.swing.*;
//import com.borland.jbcl.layout.XYLayout;
//import com.borland.jbcl.layout.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

/**
 * <p>Title: OO jDREW</p>
 *
 * <p>Description: Reasoning Engine for the Semantic Web - Supporting OO RuleML
 * 0.88</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * @author not attributable
 * @version 0.89
 */
public class TypeDefFrame extends JFrame {
    JButton jbParseTypes = new JButton();
    JScrollPane jScrollPane1 = new JScrollPane();
    JTextArea typetext = new JTextArea();

    public TypeDefFrame() {
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        getContentPane().setLayout(null);
        jbParseTypes.setBounds(new Rectangle(475, 452, 141, 24));
        jbParseTypes.setText("Parse Types");
        jbParseTypes.addMouseListener(new
                                      TypeDefFrame_jbParseTypes_mouseAdapter(this));
        typetext.setText("");
        jScrollPane1.setBounds(new Rectangle(5, 5, 620, 435));
        jScrollPane1.getViewport().add(typetext);
        this.getContentPane().add(jScrollPane1, null);
        this.getContentPane().add(jbParseTypes, null);

        this.setTitle("Type Definition");
        this.setSize(640, 540);
    }

    public static void main(String[] args) {
        TypeDefFrame typedefframe = new TypeDefFrame();
    }

    public void jbParseTypes_mouseClicked(MouseEvent e) {
        String typetext = this.typetext.getText().trim();
        jdrew.oo.util.Types.reset();
        try{
            jdrew.oo.util.RDFSParser.parseRDFSString(typetext);
        }catch (Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                                          JOptionPane.ERROR_MESSAGE);
        }
    }
}


class TypeDefFrame_jbParseTypes_mouseAdapter extends MouseAdapter {
    private TypeDefFrame adaptee;
    TypeDefFrame_jbParseTypes_mouseAdapter(TypeDefFrame adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseClicked(MouseEvent e) {
        adaptee.jbParseTypes_mouseClicked(e);
    }
}
