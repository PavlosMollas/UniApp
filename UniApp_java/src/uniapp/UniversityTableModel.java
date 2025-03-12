/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uniapp;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ArrayList;

public class UniversityTableModel extends AbstractTableModel {
    private String[] selectedColumnNames;                                       //Ονόματα των στηλών που έχουν επιλεγεί για εμφάνιση στον πίνακα
    private String[] columnNames;                                               //Όλα τα ονόματα των στηλών για τον πίνακα
    private List<University> universities;                                      //Λίστα με τα αντικείμενα University που θα εμφανίζονται στον πίνακα
    private boolean showDepartment = false;                                     //Μεταβλητή για το αν θα εμφανίζεται η στήλη department
    private boolean showCommunication = false;                                  //Μεταβλητή για το αν θα εμφανίζεται η στήλη communication
    private boolean editable = true;                                            //Μεταβλητή που ορίζει αν ο πίνακας θα είναι επεξεργάσιμος
    
    //Constructor με επιλογή των columns που θα προβληθούν και επιλογή editable τιμών στον πίνακα
    public UniversityTableModel(List<University> universities, String[] selectedColumnNames, boolean editable) {
        this.universities = universities;
        this.selectedColumnNames = selectedColumnNames;                         //Χρησιμοποιούμε την προσαρμοσμένη λίστα
        this.editable = editable;
        updateColumns();                                                        //Ενημερώνουμε τις στήλες βάσει των επιλεγμένων δεδομένων
    }

    //Μέθοδος για να ορίσουμε ποια πεδία θα εμφανίζονται
    public void setDisplayOptions(boolean showDepartment, boolean showCommunication) {
        this.showDepartment = showDepartment;
        this.showCommunication = showCommunication;
        
        int oldColumnCount = columnNames.length;                                //Αποθηκεύουμε τον προηγούμενο αριθμό στηλών
        updateColumns();                                                        //Ενημερώνουμε τις στήλες
        
        if (columnNames.length != oldColumnCount) {                             //Ανανεώνει τη δομή μόνο αν αλλάξει ο αριθμός στηλών
            fireTableStructureChanged();
        }
    }
    
    //Μέθοδος για ενημέρωση των στηλών με βάση τις επιλογές εμφάνισης
    private void updateColumns() {
        List<String> columns = new ArrayList<>(List.of(selectedColumnNames));
        if (showDepartment) 
            columns.add("Department");                                          //Προσθήκη της στήλης Department αν επιλεγεί
        if (showCommunication) 
            columns.add("Communication");                                       //Προσθήκη της στήλης Communication αν επιλεγεί
        columnNames = columns.toArray(new String[0]);                           //Μετατροπή σε πίνακα
        
        fireTableStructureChanged();                                            //Ενημέρωση της δομής του πίνακα
    }

    @Override
    public int getRowCount() {
        return universities.size();                                             //Επιστρέφει τον αριθμό των γραμμών
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;                                              //Επιστρέφει τον αριθμό των στηλών
    }

    @Override
    public String getColumnName(int column) {                                   //Επιστρέφει το όνομα της στήλης
        return columnNames[column];
    }

    //Μέθοδος για να πάρουμε την τιμή του πεδίου που υπάρχει στη βάση
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        University u = universities.get(rowIndex);                              //Παίρνουμε την τρέχουσα γραμμή University 
        if (columnIndex < selectedColumnNames.length) {
            String columnName = columnNames[columnIndex];
            return switch (columnName) {                                        //Επιστρέφει την τιμή της αντίστοιχης στήλης με βάση το όνομα της
                case "Name" -> u.getName();
                case "Domains" -> u.getDomains();
                case "Web Pages" -> u.getWebPages();
                case "ALPHA_TWO_CODE" -> u.getAlphaTwoCode();
                case "COUNTRY" -> u.getCountry();
                case "STATE_PROVINCE" -> u.getStateProvince();
                case "Searches" -> u.getSearches();
                case "Comments" -> u.getUninfo() != null ? u.getUninfo().getComments() : null;
                default -> null;
            };
        } else {
            Uninfo info = u.getUninfo();                                        //Παίρνουμε το Uninfo 
            if (info == null) {
                return null;                                                    //Αν δεν υπάρχει Uninfo επιστρέφει null
            }
            int extraColumnIndex = columnIndex - selectedColumnNames.length;
            if (showDepartment && extraColumnIndex == 0) 
                return info.getDepartment();                                    //Επιστρέφει την τιμή του Department αν επιλεγεί
            if (showCommunication && extraColumnIndex == (showDepartment ? 1 : 0)) 
                return info.getCommunication();                                 //Επιστρέφει την τιμή του Communication αν επιλεγεί
        }
        return null;
    }

    //Μέθοδος για αν τα κελιά είναι επεξεργάσιμα
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        //Αν το editable είναι false, καμία στήλη δεν είναι επεξεργάσιμη
        if (!editable) {
            return false;
        }
        
        //Το Name και το Country είναι composed primary key και δεν είναι επεξεργάσιμα
        if (columnNames[columnIndex].equals("Name") || columnNames[columnIndex].equals("COUNTRY")) {
            return false;
        }
        
        //Όλες οι υπόλοιπες στήλες είναι επεξεργάσιμες
        if (columnIndex < selectedColumnNames.length) {
            return true;
        } else {
            int extraColumnIndex = columnIndex - selectedColumnNames.length;
            return (showDepartment && extraColumnIndex == 0) ||
                   (showCommunication && extraColumnIndex == (showDepartment ? 1 : 0));
        }
    }

    //Μέθοδος για να ενημερώσουμε την τιμή του πεδίου που υπάρχει στη βάση
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        University u = universities.get(rowIndex);                              //Παίρνουμε την τρέχουσα γραμμή University 
        String value = (aValue == null) ? "" : aValue.toString().trim();        //Επεξεργασία της τιμής εισόδου
        
        if (columnIndex < selectedColumnNames.length) {
            switch (columnNames[columnIndex]) {                                 //Ενημερώνει την τιμή της αντίστοιχης στήλης με βάση το όνομα της
                case "Domains" -> u.setDomains(value);
                case "Web Pages" -> u.setWebPages(value);
                case "ALPHA_TWO_CODE" -> u.setAlphaTwoCode(value);
                case "STATE_PROVINCE" -> u.setStateProvince(value);
                case "Comments" -> {                                            //Αν δεν υπάρχει Uninfo δημιουργούμε νέο και ενημερώνουμε τα σχόλια
                    if (u.getUninfo() == null) {
                        u.setUninfo(new Uninfo(u.getName(), u.getCountry()));
                    }
                    u.getUninfo().setComments(value);
                }
            }
        } else {
            Uninfo info = u.getUninfo();                                        //Παίρνουμε το Uninfo 
            if (info == null) {
                info = new Uninfo(u.getName(), u.getCountry());
                u.setUninfo(info);
            }
            int extraColumnIndex = columnIndex - selectedColumnNames.length;
            if (showDepartment && extraColumnIndex == 0) 
                info.setDepartment(value);                                      //Ενημέρωση του Department
            else if (showCommunication && extraColumnIndex == (showDepartment ? 1 : 0)) 
                info.setCommunication(value);                                   //Ενημέρωση του Communication
        }
        fireTableCellUpdated(rowIndex, columnIndex);                             //Ενημερώνουμε τον πίνακα με βάση τις τιμές
    }

    public List<University> getUniversities() {                                 //Επιστρέφει τη λίστα των universities
        return universities;
    }
    
    public void setUniversities(List<University> universities) {                //Ορίζει τη νέα λίστα των universities
        this.universities = universities;
        fireTableDataChanged();                                                 //Ενημέρωση του πίνακα
    }
    
    // Μέθοδος για να ορίσουμε αν τα πεδία είναι επεξεργάσιμα
    public void setEditable(boolean editable) {
        this.editable = editable;
        fireTableDataChanged();                                                 //Ενημέρωση του πίνακα
    }
}