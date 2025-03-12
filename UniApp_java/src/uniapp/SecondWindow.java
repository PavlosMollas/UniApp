/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uniapp;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.persistence.EntityManager;
import javax.swing.*;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import javax.persistence.EntityTransaction;

public class SecondWindow {
    private JFrame secFrame;
    private JPanel secPanel, topPanel, newInfoPanel;
    private JButton updateButton, exportToPdfButton;
    private static JCheckBox departmentCheckBox, communicationCheckBox;
    private JTable table;
    private UniversityTableModel tableModel;
    private JScrollPane scrollPane;
    private JFrame mainJFrame;
    private static Set<String> selectedColumns = new HashSet<>();
    private String[] selectedColumnNames;
    private boolean isStatistics;                                               //Μεταβλητή για επιλογή δεύτερου constructor
    
    //Constructor για προβολή αναζήτησης
    public SecondWindow(List<University> universities, JFrame mainJFrame, String[] selectedColumnNames){
        this.selectedColumnNames = selectedColumnNames;                         //Μεταβλητή για τα πεδία προβολής
        this.mainJFrame = mainJFrame;
        initializeSec();                                                        
        searchOption();                                                         //Χρήση μεθόδου για αναζήτηση
        tableModel.setUniversities(universities);                               //Ενημέρωση των πανεπιστημίων στον πίνακα
        restorePreviousSelection();                                             //Μέθοδος για επαναφορά προηγούμενων επιλογών
    }
    
    //Constructor για προβολή στατιστικών
    public SecondWindow(List<University> universities, JFrame mainJFrame, String[] selectedColumnNames, boolean isStatistics){
        this.isStatistics = true;                                               //Μεταβλητή που ορίζει την προβολή στατιστικών αντί για αναζήτηση
        this.selectedColumnNames = selectedColumnNames;     
        this.mainJFrame = mainJFrame;
        initializeSec();
        statisticsOption();                                                     //Χρήση μεθόδου για στατιστικά
        tableModel.setUniversities(universities);
    }
    
    //Παράθυρο και για τους 2 contructor
    public void initializeSec() {
        secFrame = new JFrame();                                                //Δημιουργία χαρακτηριστικών του GUI
        secFrame.setTitle("UniApp");
        secFrame.getContentPane().setBackground(new Color(0x234567));           //Επιλογή χρώματος 
        secFrame.setSize(900, 500);
        secFrame.setLocationRelativeTo(null);
        secFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        ImageIcon icon = new ImageIcon(getClass().getResource("/logo.png"));
        secFrame.setIconImage(icon.getImage());
        
        secPanel = new JPanel(new BorderLayout());                              //Δημιουργία του βασικού panel
        secFrame.add(secPanel, BorderLayout.CENTER);

        topPanel = new JPanel();
        topPanel.setBackground(new Color(0x234567));
    }
    
    //Μέθοδος για χαρακτηριστικά αναζήτησης
    public void searchOption() {
        //Όταν κλείνει το παράθυρο αναζήτησης εμφανίζεται το κυρίως παράθυρο
        secFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (table.isEditing()) {                                        //Έλεγχος αν υπάρχουν μη αποθηκευμένες αλλαγές
                    int option = JOptionPane.showConfirmDialog(
                        secFrame, 
                        "You have unsaved changes. Do you really want to exit?", 
                        "Unsaved Changes", 
                        JOptionPane.YES_NO_OPTION, 
                        JOptionPane.WARNING_MESSAGE
                    );

                    if (option == JOptionPane.NO_OPTION || option == JOptionPane.CLOSED_OPTION) {
                        return;                                                 //Ακύρωση κλεισίματος με επιλογή No ή εξόδου από το JOptionPane
                    }
                }
                mainJFrame.setVisible(true);                                    //Εμφάνιση του κυρίως παραθύρου
                MainJFrame.restoreListFrame();                                  //Επαναφέρει το listFrame
                secFrame.dispose();                                             //Κλείσιμο του secFrame με επιλογή Yes
                    
            }
        });
        
        updateButton = new JButton("Update and Exit");                          //Δημιουργία κουμπιού για αποθήκευση
        updateButton.setToolTipText("Click to update database and return to the main menu");    // Ορισμός του ToolTip
        updateButton.setFocusable(false);
        updateButton.addActionListener(e -> submitChanges());                   //Εκτέλεση μεθόδου υποβολής αλλαγών
        
        topPanel.add(updateButton);                                             //Νέο πάνελ για το κουμπί
        topPanel.add(Box.createHorizontalStrut(50));

        //Δημιουργία JCheckBox για προσθήκη στήλης και ToolTip για αυτά
        departmentCheckBox = new JCheckBox("Department");
        departmentCheckBox.setToolTipText("Add Department column");

        communicationCheckBox = new JCheckBox("Communication");
        communicationCheckBox.setToolTipText("Add Communication column");
        
        departmentCheckBox.addActionListener(e -> updateTableColumns());
        communicationCheckBox.addActionListener(e -> updateTableColumns());
        
        newInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));  
        newInfoPanel.add(departmentCheckBox);                                   //Ξεχωριστό πάνελ μαζί με το κουμπί
        newInfoPanel.add(communicationCheckBox);
        
        topPanel.add(newInfoPanel);                                             //Προσθήκη του πάνελ με τα check box στο πάνελ με το κουμπί
        secPanel.add(topPanel, BorderLayout.NORTH);
        
        //Καλούμε το table model με όρισμα τα πεδία που θα δώσουμε στη MainJFrame και θα είναι editable
        tableModel = new UniversityTableModel(List.of(), selectedColumnNames, true);
        table = new JTable(tableModel);
        scrollPane = new JScrollPane(table);
        secPanel.add(scrollPane, BorderLayout.CENTER);
        table.setRowHeight(35);
        
        // Προσθήκη MouseListener για την εμφάνιση του tooltip
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                //Εύρεση της θέσης του ποντικιού στον πίνακα
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                
                //Έλεγχος αν το κελί είναι κενό και ορισμός tooltip
                Object value = table.getValueAt(row, col);
                
                //Παίρνουμε το όνομα της στήλης
                String columnName = table.getColumnName(col);

                if (value == null || value.toString().trim().isEmpty()) {
                    table.setToolTipText("This field is empty. Double click to make changes");
                } else {
                    //Ελέγχουμε αν η στήλη είναι Name ή Country και εμφανίζουμε toolTip με το πεδίο
                    if (columnName.equals("Name")) {
                        table.setToolTipText("University name: \"" + value.toString() + "\"");
                    } else if (columnName.equals("COUNTRY")) {
                        table.setToolTipText("Country: \"" + value.toString() + "\"");
                    } else {
                        //ToolTip για τις αλλαγές
                        table.setToolTipText("Double click to change \"" + value.toString() + "\" field from " + columnName);
                    }
                }
            }
        });
    }
    
    //Μέθοδος για χαρακτηριστικά στατιστικών
    public void statisticsOption() {
        secFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                mainJFrame.setVisible(true);
                secFrame.dispose();
            }
        });
        
        exportToPdfButton = new JButton("Export statistics to pdf");            //Κουμπί για export σε pdf
        exportToPdfButton.setToolTipText("Click to export statistics and return to the main menu"); //Ορισμός του ToolTip
        exportToPdfButton.setFocusable(false);
        exportToPdfButton.addActionListener(e -> exportToPdf());                //Εκτέλεση μεθόδου αποθήκευσης pdf
        
        topPanel.add(exportToPdfButton);                                        //Πάνελ για το κουμπί
        topPanel.add(Box.createHorizontalStrut(50));
        
        secPanel.add(topPanel, BorderLayout.NORTH);
        
        //Καλούμε το table model με όρισμα τα πεδία που θα δώσουμε στη MainJFrame χωρίς να είναι editable
        tableModel = new UniversityTableModel(List.of(), selectedColumnNames, false);
        table = new JTable(tableModel);
        scrollPane = new JScrollPane(table);
        secPanel.add(scrollPane, BorderLayout.CENTER);
        
        //Προσθήκη MouseListener για την εμφάνιση του tooltip
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                //Εύρεση της θέσης του ποντικιού στον πίνακα
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                
                //Έλεγχος αν το κελί είναι έγκυρο και ορισμός tooltip
                if (row >= 0 && col >= 0) {
                    Object value = table.getValueAt(row, col);

                    //Ορισμός του tooltip ανάλογα με τη στήλη
                    if (col == 0) {                                             //Αν είναι η στήλη Name
                        table.setToolTipText("University name: \"" + value + "\"");
                    }
                    if (col == 1) {                                             //Αν είναι η στήλη Searches
                        table.setToolTipText("Total searches: \"" + value + "\"");
                    }
                }
            }
        });
        
    }
    
    //Μέθοδος για εμφάνιση του παραθύρου
    public void show() {
        mainJFrame.setVisible(false);
        secFrame.setVisible(true);
    }
    
    //Μέθοδος για την υποβολή των αλλαγών στον πίνακα
    private void submitChanges() {
        if (table.isEditing()) {                                                //Ελέγχει αν υπάρχει ενεργό κελί και σταματάει την επεξεργασία
            table.getCellEditor().stopCellEditing();                            
        }
        
        //Παίρνουμε τη λίστα των ενημερωμένων πανεπιστημίων από το μοντέλο του πίνακα
        List<University> updatedUniversities = tableModel.getUniversities();
        
        EntityManager em = Main.getEM();                                        //Παίρνουμε τον EntityManager
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();                                                         //Ξεκινάει τη διαδικασία
            for (University u : updatedUniversities) {                          //Για κάθε πανεπιστήμιο ενημερώνει την εγγραφή στη βάση δεδομένων
                em.merge(u);
            }
            tx.commit();                                                        //Γίνεται commit της διαδικασίας
            //Μήνυμα για επιτυχία
            JOptionPane.showMessageDialog(secFrame, "Changes saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            tx.rollback();                                                      //Αν υπάρξει σφάλμα, κάνει rollback τη διαδικασία
            //Μήνυμα για αποτυχία
            JOptionPane.showMessageDialog(secFrame, "Error saving changes!", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        
        mainJFrame.setVisible(true);                                            //Εμφάνιση του κυρίως παραθύρου
        MainJFrame.restoreListFrame();                                          //Επαναφέρει το listFrame
        secFrame.dispose();                                                     //Κλείσιμο παραθύρου
    }
    
    private void updateTableColumns() {
        //Ελέγχει αν τα CheckBox για το τμήμα και την επικοινωνία είναι επιλεγμένα
        boolean showDepartment = departmentCheckBox.isSelected();
        boolean showCommunication = communicationCheckBox.isSelected();
        
        selectedColumns.clear();                                                //Καθαρίζει τις προηγούμενες επιλογές στηλών
        
        if (showDepartment) selectedColumns.add("Department");                  //Αν το κάποιο είναι επιλεγμένο, προσθέτει την αντίστοιχη στήλη στη λίστα
        if (showCommunication) selectedColumns.add("Communication");
        
        //Ενημερώνει τον πίνακα με τις νέες επιλογές στηλών
        tableModel.setDisplayOptions(showDepartment, showCommunication);
        
        table.revalidate();                                                     //Επανασχεδιάζει και ανανεώνει τον πίνακα για να εμφανίσει τις νέες στήλες
        table.repaint();
        
        table.setModel(tableModel);                                             //Ορίζει το μοντέλο του πίνακα
    }
    
    //Μέθοδος για επιλογή ή αποεπιλογή των check boxes
    private void restorePreviousSelection() {
        departmentCheckBox.setSelected(selectedColumns.contains("Department"));
        communicationCheckBox.setSelected(selectedColumns.contains("Communication"));

        updateTableColumns();
    }
    
    //Μέθοδος για εξαγωγή στατιστικών σε PDF
    public void exportToPdf() {
        //Αποθήκευση στο path με το project
        String filePath = System.getProperty("user.dir") + "/uni_stats.pdf";
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            //Προσθήκη τίτλου
            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
            Paragraph title = new Paragraph("University Search Statistics", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            //Δημιουργία πίνακα με τον ίδιο αριθμό στηλών όπως στον JTable
            int columnCount = table.getColumnCount();
            PdfPTable pdfTable = new PdfPTable(columnCount);
            pdfTable.setWidthPercentage(100);

            //Προσθήκη κεφαλίδων
            com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
            for (int i = 0; i < columnCount; i++) {
                PdfPCell header = new PdfPCell(new Phrase(table.getColumnName(i), headerFont));
                header.setBackgroundColor(BaseColor.DARK_GRAY);
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfTable.addCell(header);
            }

            //Προσθήκη δεδομένων από τον πίνακα
            for (int row = 0; row < table.getRowCount(); row++) {
                for (int col = 0; col < columnCount; col++) {
                    pdfTable.addCell(table.getValueAt(row, col).toString());
                }
            }

            document.add(pdfTable);

            //Μήνυμα για επιτυχία
            JOptionPane.showMessageDialog(secFrame, "PDF exported successfully");
        } catch (Exception e) {
            e.printStackTrace();                                                //Μήνυμα για αποτυχία
            JOptionPane.showMessageDialog(secFrame, "Error exporting PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            document.close();
        }
        secFrame.dispose();                                                     //Κλείσιμο παραθύρου
        mainJFrame.setVisible(true);                                            //Εμφάνιση του κυρίως παραθύρου
    }
}