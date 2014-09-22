// OO jDREW Version 0.89
// Copyright (c) 2005 Marcel Ball
//
// This software is licensed under the LGPL (LESSER GENERAL PUBLIC LICENSE) License.
// Please see "license.txt" in the root directory of this software package for more details.
//
// Disclaimer: Please see disclaimer.txt in the root directory of this package.

package jdrew.oo.gui;

import java.awt.*;

import java.io.*;

import javax.swing.*;
//import com.borland.jbcl.layout.XYLayout;
//import com.borland.jbcl.layout.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import jdrew.oo.util.*;
import java.util.*;
import nu.xom.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

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
public class Translator extends JFrame {
	
	public static int currentParser =  RuleMLParser.RULEML88;
	
    public Translator() {
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        getContentPane().setLayout(null);
        jbToPosl.setBounds(new Rectangle(50, 338, 115, 23));
        jbToPosl.setText("To POSL");
        jbToPosl.addMouseListener(new Translator_jbToPosl_mouseAdapter(this));
        jbToRML.setBounds(new Rectangle(200, 338, 120, 23));
        jbToRML.setText("To RuleML 0.88");
        jbToRML.addMouseListener(new Translator_jbToRML_mouseAdapter(this));
        jbToRML91.setBounds(new Rectangle(350, 338, 120, 23));
        jbToRML91.addMouseListener(new Translator_jbToRML91_mouseAdapter(this));
        jbToRML91.setText("To RuleML 0.91");
        this.addWindowListener(new Translator_windowAdapter(this));
        jScrollPane2.setBounds(new Rectangle(5, 370, 610, 300));
        jLabel1.setBounds(new Rectangle(5, 350, 66, 15));
        jScrollPane1.setBounds(new Rectangle(5, 28, 610, 300));
        jLabel2.setBounds(new Rectangle(5, 5, 84, 15));
        jbTypes.setBounds(new Rectangle(500, 337, 115, 23));
        jbTypes.setText("Types");
        jbTypes.addMouseListener(new Translator_jbTypes_mouseAdapter(this));
        rmltext.setLineWrap(false);
        posltext.setLineWrap(false);
        posltext.setWrapStyleWord(true);
        jScrollPane1.getViewport().add(rmltext);
        rmltext.setText("");
        rmltext.setWrapStyleWord(true);

        jLabel1.setText("POSL");
        jScrollPane2.getViewport().add(posltext);
        jLabel2.setText("RuleML");
        this.getContentPane().add(jLabel1, null);
        this.getContentPane().add(jLabel2, null);
        this.getContentPane().add(jScrollPane1, null);
        this.getContentPane().add(jScrollPane2, null);
        this.getContentPane().add(jbTypes);
        this.getContentPane().add(jbToPosl, null);
        this.getContentPane().add(jbToRML, null);
        this.getContentPane().add(jbToRML91, null);
        
        this.setSize(630,730);
        this.setTitle("RuleML <-> POSL Converter");
        this.setResizable(false);
    }

    public static void main(String[] args) {
 	
        Translator translator = new Translator();
        BasicConfigurator.configure();
        Logger root = Logger.getRootLogger();
        root.setLevel(Level.DEBUG);

        translator.show();
        jdrew.oo.Config.PRINTGENOIDS = false;
        jdrew.oo.Config.PRINTGENSYMS = false;
        jdrew.oo.Config.PRINTVARID = false;
        jdrew.oo.Config.PRINTANONVARNAMES = false;
    }

	public static void openFile(){
	
	int arrlen = 10000;
	byte[] infile = new byte[arrlen];
	Frame parent = new Frame();
	FileDialog fd = new FileDialog(parent, "Please choose a file:",
	           FileDialog.LOAD);
	fd.show();
	String selectedItem = fd.getFile();
	if (selectedItem == null) {
		// no file selected
	} else {
		File ffile = new File( fd.getDirectory() + File.separator +
		        fd.getFile());
		// read the file
		System.out.println("reading file " + fd.getDirectory() +
		                         File.separator + fd.getFile() );
		try {
			FileInputStream fis = new FileInputStream(ffile); 
			BufferedInputStream bis = new BufferedInputStream(fis);
			DataInputStream dis = new DataInputStream(bis);
			try {
				int filelength = dis.read(infile);
				String filestring = new String(infile, 0, filelength);
				System.out.println("FILE CONTENT=" + filestring);
			} catch(IOException iox) {
				System.out.println("File read error...");
				iox.printStackTrace();
			}
		} catch (FileNotFoundException fnf) {
			System.out.println("File not found...");
			fnf.printStackTrace();
		}
	}		

  	}

    TypeDefFrame tdf = new TypeDefFrame();

    JScrollPane jScrollPane1 = new JScrollPane();
    JTextArea rmltext = new JTextArea();
    JScrollPane jScrollPane2 = new JScrollPane();
    JTextArea posltext = new JTextArea();
    JLabel jLabel1 = new JLabel();
    JLabel jLabel2 = new JLabel();
    JButton jbToPosl = new JButton();
    JButton jbToRML = new JButton();
    JButton jbToRML91 = new JButton();
    JButton jbTypes = new JButton();
    
    

    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }

    public void jbToRML_mouseClicked(MouseEvent e) {
    	
    	currentParser =  RuleMLParser.RULEML88;
    	
        String posltext = this.posltext.getText().trim();
        POSLParser pp = new POSLParser();
        try{
            pp.parseDefiniteClauses(posltext);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                                          JOptionPane.ERROR_MESSAGE);
        }

        System.out.println("Done Parsing");

        Iterator it = pp.iterator();
        Element as = new Element("Assert");
        Element and = new Element("And");
        as.appendChild(and);

        while(it.hasNext()){
            DefiniteClause dc = (DefiniteClause)it.next();
            and.appendChild(dc.toRuleML());
        }

        java.io.StringWriter sw = new java.io.StringWriter();
        nu.xom.Serializer sl = new nu.xom.Serializer(sw);
        sl.setIndent(3);
        sl.setLineSeparator("\n");
        try {
            sl.write(as);
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                                              JOptionPane.ERROR_MESSAGE);
        }

        this.rmltext.setText(sw.getBuffer().toString());
    }

	public void jbToRML91_mouseClicked(MouseEvent e) {
    	
    currentParser =  RuleMLParser.RULEML91;
    	
        String posltext = this.posltext.getText().trim();
        POSLParser pp = new POSLParser();
        try{
            pp.parseDefiniteClauses(posltext);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                                          JOptionPane.ERROR_MESSAGE);
        }

        System.out.println("Done Parsing");

        Iterator it = pp.iterator();
        Element as = new Element("Assert");
        Element and = new Element("And");
        as.appendChild(and);

        while(it.hasNext()){
            DefiniteClause dc = (DefiniteClause)it.next();
            and.appendChild(dc.toRuleML());
        }

        java.io.StringWriter sw = new java.io.StringWriter();
        nu.xom.Serializer sl = new nu.xom.Serializer(sw);
        sl.setIndent(3);
        sl.setLineSeparator("\n");
        try {
            sl.write(as);
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                                              JOptionPane.ERROR_MESSAGE);
        }

        this.rmltext.setText(sw.getBuffer().toString());
    }


    public void jbToPosl_mouseClicked(MouseEvent e) {
        String rmltext = this.rmltext.getText();
       
        RuleMLParser rmp = new RuleMLParser();
        try{
            rmp.parseRuleMLString(RuleMLParser.RULEML91, rmltext);
        }catch(Exception ex){
        	
        	try{
        	rmp.parseRuleMLString(RuleMLParser.RULEML88, rmltext);
        	}
        	catch(Exception ex2){
        		JOptionPane.showMessageDialog(this, ex2.getMessage(), "Error",
                                         JOptionPane.ERROR_MESSAGE);
        	}
        }

        StringBuffer sb = new StringBuffer();
        Iterator it = rmp.iterator();
        while(it.hasNext()){
            DefiniteClause dc = (DefiniteClause)it.next();
            sb.append(dc.toPOSLString());
            sb.append("\n");
        }

        this.posltext.setText(sb.toString());
    }

    public void jbTypes_mouseClicked(MouseEvent e) {
        //openFile();
        tdf.show();
    }
}


class Translator_jbTypes_mouseAdapter extends MouseAdapter {
    private Translator adaptee;
    Translator_jbTypes_mouseAdapter(Translator adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseClicked(MouseEvent e) {
        adaptee.jbTypes_mouseClicked(e);
    }
}


class Translator_jbToPosl_mouseAdapter extends MouseAdapter {
    private Translator adaptee;
    Translator_jbToPosl_mouseAdapter(Translator adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseClicked(MouseEvent e) {
        adaptee.jbToPosl_mouseClicked(e);
    }
}


class Translator_jbToRML_mouseAdapter extends MouseAdapter {
    private Translator adaptee;
    Translator_jbToRML_mouseAdapter(Translator adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseClicked(MouseEvent e) {
        adaptee.jbToRML_mouseClicked(e);
    }
}

class Translator_jbToRML91_mouseAdapter extends MouseAdapter {
    private Translator adaptee;
    Translator_jbToRML91_mouseAdapter(Translator adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseClicked(MouseEvent e) {
        adaptee.jbToRML91_mouseClicked(e);
    }
}


class Translator_windowAdapter extends WindowAdapter {
    private Translator adaptee;
    Translator_windowAdapter(Translator adaptee) {
        this.adaptee = adaptee;
    }

    public void windowClosing(WindowEvent e) {
        adaptee.windowClosing(e);
    }
}
