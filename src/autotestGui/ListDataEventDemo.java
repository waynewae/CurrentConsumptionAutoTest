package autotestGui;

/*
 * ListDataEventDemo.java requires the Java Look and Feel Graphics
 * Repository (jlfgr-1_0.jar).  You can download this file from
 * http://java.sun.com/developer/techDocs/hi/repository/
 * Put it in the class path using one of the following commands
 * (assuming jlfgr-1_0.jar is in a subdirectory named jars):
 *
 *   java -cp .;jars/jlfgr-1_0.jar ListDataEventDemo [Microsoft Windows]
 *   java -cp .:jars/jlfgr-1_0.jar ListDataEventDemo [UNIX]
 */

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import java.io.IOException;
import java.util.*;

import handleFile.*;

public class ListDataEventDemo extends JPanel 
                               implements ListSelectionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JList<String> list;
    private DefaultListModel<String> listModel;
    private FileHandler fileHandler;
    ArrayList<String> localFiles;

    private static final String exportString = "Export";
    private static final String upString = "Move up";
    private static final String downString = "Move down";

    private JButton exportButton;
    private JButton upButton;
    private JButton downButton;
    
    private JCheckBox CurrMeasBox;

    private JTextArea log;
    static private String newline = "\n";
    
    private int CurrMeas = 0;

    public ListDataEventDemo() {
        super(new BorderLayout());
        
        //Create and populate the list model.
        FileParser localParser = new FileParser();
    	localFiles = localParser.parseLocalFiles();
        
        listModel = new DefaultListModel<String>();
        for (int i = 0; i < localFiles.size(); i++) {
    		if(!localFiles.get(i).equals("AutoTestGen.jar"))
    			listModel.addElement(localFiles.get(i));
    	}
        listModel.addListDataListener(new MyListDataListener());

        //Create the list and put it in a scroll pane.
        list = new JList<String>(listModel);
        list.setSelectionMode(
            ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setSelectedIndex(0);
        list.addListSelectionListener(this);
        JScrollPane listScrollPane = new JScrollPane(list);

        //Create the list-modifying buttons.
        exportButton = new JButton(exportString);
        exportButton.setActionCommand(exportString);
        exportButton.addActionListener(new ControlListener());
        
        upButton = new JButton("Move up");
        
        upButton.setToolTipText("Move the currently selected list item higher.");
        upButton.setActionCommand(upString);
        upButton.addActionListener(new ControlListener());
        
        downButton = new JButton("Move down");
        
        downButton.setToolTipText("Move the currently selected list item lower.");
        downButton.setActionCommand(downString);
        downButton.addActionListener(new ControlListener());

        JPanel upDownPanel = new JPanel(new GridLayout(1, 2));
        upDownPanel.add(upButton);
        upDownPanel.add(downButton);

        //Create a control panel, using the default FlowLayout.
        JPanel controlButtonPane = new JPanel();
        controlButtonPane.add(exportButton);
        controlButtonPane.add(upDownPanel);

        //Create the log for reporting list data events.
        log = new JTextArea(10, 20);
        JScrollPane logScrollPane = new JScrollPane(log);

        //Create a split pane for the log and the list.
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                        listScrollPane, logScrollPane);
        splitPane.setResizeWeight(0.5);
                
        // Create a button for checking if current measurement is enabled
        CurrMeasBox = new JCheckBox("Current measurement");
        CurrMeasBox.addItemListener(new SelectedListener());
              
        // Create a checkBuntton panel
        JPanel checkButtonPane = new JPanel();
        checkButtonPane.add(CurrMeasBox);
                
        //Put everything together.
        add(controlButtonPane, BorderLayout.PAGE_START);
        add(splitPane, BorderLayout.CENTER);
        add(checkButtonPane, BorderLayout.EAST);
    }

    class MyListDataListener implements ListDataListener {
        public void contentsChanged(ListDataEvent e) {
            log.append("contentsChanged: " + e.getIndex0() +
                       ", " + e.getIndex1() + newline); 
            log.setCaretPosition(log.getDocument().getLength());
        }
        public void intervalAdded(ListDataEvent e) {
            log.append("intervalAdded: " + e.getIndex0() +
                       ", " + e.getIndex1() + newline); 
            log.setCaretPosition(log.getDocument().getLength());
        }
		@Override
		public void intervalRemoved(ListDataEvent e) {
			// TODO Auto-generated method stub
			log.append("intervalRemoved: " + e.getIndex0() +
                    ", " + e.getIndex1() + newline); 
			log.setCaretPosition(log.getDocument().getLength());
		}
    }
    
    class SelectedListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			// TODO Auto-generated method stub
			Object source = e.getItemSelectable();
			if (e.getStateChange() == ItemEvent.DESELECTED) {
				if(source == CurrMeasBox) {
					CurrMeas = 0;
					log.append("Disable current measurement" + newline);
				}
	        } else {
	        	if (source == CurrMeasBox) {
					CurrMeas = 1;
					log.append("Enable current measurement" + newline);
				}
	        }
		}
    	
    }
    
    class ControlListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //This method can be called only when
            //there's a valid selection,
            //so go ahead and move the list item.
            int moveMe = list.getSelectedIndex();

            if (e.getActionCommand().equals(upString)) {
            //UP ARROW BUTTON
                if (moveMe != 0) {     
                //not already at top
                    swap(moveMe, moveMe-1);
                    list.setSelectedIndex(moveMe-1);
                    list.ensureIndexIsVisible(moveMe-1);
                }
            } else if (e.getActionCommand().equals(downString)) {
            //DOWN ARROW BUTTON
                if (moveMe != listModel.getSize()-1) {
                //not already at bottom
                    swap(moveMe, moveMe+1);
                    list.setSelectedIndex(moveMe+1);
                    list.ensureIndexIsVisible(moveMe+1);
                }
            } else {
            	if(listModel.isEmpty() == false) {
	            	fileHandler = new FileHandler();
	            	try {
	            		fileHandler.exportBatchfile(list.getModel() , CurrMeas);
	            		fileHandler.exportScript(list.getModel());
	    				log.append("Export File success" + newline); 
	    			} catch (IOException e1) {
	    				// TODO Auto-generated catch block
	    				e1.printStackTrace();
	    			}
            	} else {
            		log.append("Export File failed"); 
            	}
            }
        }
    }
    
    // Listen for

    //Swap two elements in the list.
    private void swap(int a, int b) {
        String aObject = listModel.getElementAt(a);
        String bObject = listModel.getElementAt(b);
        listModel.set(a, bObject);
        listModel.set(b, aObject);
    }

    //Listener method for list selection changes.
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {

            if (list.getSelectedIndex() == -1) {
            //No selection: disable delete, up, and down buttons.
                upButton.setEnabled(false);
                downButton.setEnabled(false);

            } else if (list.getSelectedIndices().length > 1) {
            //Multiple selection: disable up and down buttons.
                upButton.setEnabled(false);
                downButton.setEnabled(false);

            } else {
            //Single selection: permit all operations.
                upButton.setEnabled(true);
                downButton.setEnabled(true);
            }
        }
    }

    /** 
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("ListDataEventDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new ListDataEventDemo();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
        
        //Don't let the content pane get too small.
        //(Works if the Java look and feel provides
        //the window decorations.)
        newContentPane.setMinimumSize(
                new Dimension(
                        newContentPane.getPreferredSize().width,
                        100));

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}