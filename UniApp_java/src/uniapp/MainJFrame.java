/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uniapp;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;
import java.util.List;
import javax.persistence.Query;

public class MainJFrame{
    private JFrame frame;                                                       //Κυρίως παράθυρο                             
    private static JDialog listDialog;                                          //Παράθυρο με υποχρεωτική επιλογή ή έξοδο
    private JPanel panel, listPanel, dropdownPanel, searchPanel;                //Πάνελ για δομή των δύο παραθύρων 
    private JButton searchButton, showStatisticsButton, exitButton, clearSelectionButton, listButton;
    private JLabel label;                                                       //Ετικέτα
    private JTextField searchField;                                             //Text Field
    private JScrollPane scrollPane;                                             //Μπάρα κύλισης
    private JList<String> universityList;                                       //Λίστα με τα πανεπιστήμια
    private static JComboBox<String> countryDropdown;                           //Drop down menu
    private static DefaultComboBoxModel<String> countryModel;                   //Μοντέλο δεδομένων για το drop down menu που περιέχει τις χώρες
    private static boolean isCountrySearch = false;                             //Μεταβλητή για να ξεχωρίσουμε αναζήτηση μόνο με χώρα ή όχι
    private static List<University> lastUniversities;                           //Λίστα για αποθήκευση της τρέχον λίστας
    private static String lastSelectedCountry;                                  //Μεταβλητή για αποθήκευση της τρέχον χώρας
    private static MainJFrame instance;                                         //Αναφορά στο αντικείμενο

    public MainJFrame(){
        instance = this;                                                        //Αποθήκευση του αντικειμένου κατά τη δημιουργία
        initialize();                                                           //Άνοιγμα GUI
        updateButtonState();                                                    //Ενημέρωση του κουμπιού αν υπάρχει τουλάχιστον μία αναζήτηση στη βάση
    }
    
    //Μέθοδος υλοποίησης GUI
    public void initialize(){
        frame = new JFrame();                                                   //Δημιουργία χαρακτηριστικών του GUI
        frame.setTitle("Uni App");                                               //Τίτλος
        frame.setSize(900, 750);                                                //Μέγεθος παραθύρου
        frame.setLocationRelativeTo(null);                                      //Τοποθέτηση παραθύρου στο κέντρο της οθόνης
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);             //Δεν κάνουμε τίποτα στο κλείσιμο (γιατί υπάρχει η επιβεβαιωση)
        ImageIcon icon = new ImageIcon(getClass().getResource("/logo.png"));    //Εικόνα για το παράθυρο (αντί της εικόνας της java)
        frame.setIconImage(icon.getImage());                                    //Τοποθέτηση εικόνας μαζί με τον τίτλο του παραθύρου
        
        frame.addWindowListener(new WindowAdapter() {                           //Δημιουργία Listener παραθύρου για την επιβεβαίωση εξόδου
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit(frame);                                              //Καλούμε τη μέθοδο επιβεβαίωσης
            }
        });
        
        panel = new JPanel();
        panel.setBackground(new Color(0x234567));                               //Δημιουργία panel στο GUI
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));                //Δημιουργία κάθετης διάταξης για τα κουμπιά
        frame.add(panel, BorderLayout.CENTER);                                  //Προσθέτουμε το panel στο παράθυρο
        
        label = new JLabel("Welcome to Uni App");                               //Ετικέτα παραθύρου
        label.setFont(new Font("Arial", Font.BOLD, 32));                        //Χαρακτηριστικά ετικέτας
        label.setForeground(Color.WHITE);
        
        //Τοποθέτηση εικόνας στο παράθυρο μαζί με την ετικέτα
        ImageIcon labelIcon = new ImageIcon(getClass().getResource("/logo.png"));   
        label.setIcon(labelIcon);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);                        //Τοποθέτηση της εικόνας στο κέντρο του παραθυρου μαζί με την ετικέτα
        panel.add(label);                                                       //Προσθήκη της εικόνας στο παράθυρο μαζί με την ετικέτα
        
        panel.add(Box.createRigidArea(new Dimension(0, 40)));                   //Προσθήκη κενού στην κορυφή
        label.setIconTextGap(15);                                               //Προσθήκη κενού ανάμεσα στην εικόνα και την ετικέτα
        panel.add(label);                                                       //Προσθήκη του κενού στο παράθυρο

        searchField = new JTextField();
        searchField.setToolTipText("Search by keyword");                        //Ορισμός του ToolTip για το JTextField
        searchField.setText("Enter a keyword...");                              //Χαρακτηριστικά ToolTip
        searchField.setForeground(Color.GRAY);
        searchField.setFont(new Font("SansSerif", Font.ITALIC, 14)); 
        
        searchField.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (searchField.getText().equals("Enter a keyword...")) {       //Όταν κάνουμε κλικ στο textfield σβήνεται το μήνυμα (placeholder)
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                    searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
                }
            }
        });
        
        searchField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Enter a keyword...")) {       //Όταν παίρνει focus το textfield σβήνεται το μήνυμα (placeholder)
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                    searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {                   //Όταν χάνεται το focus εμφανίζει σχετικό μήνυμα (placeholder)
                    searchField.setText("Enter a keyword...");
                    searchField.setForeground(Color.GRAY);
                    searchField.setFont(new Font("SansSerif", Font.ITALIC, 14));
                }
            }
        });
        
        frame.addWindowListener(new WindowAdapter() {                           //Όταν επιστρέφουμε στο αρχικό παράθυρο ενεργοποιείται το placeholder
            @Override
            public void windowActivated(WindowEvent e) {
                if (searchField.getText().trim().isEmpty()) { 
                    searchField.setText("Enter a keyword...");
                    searchField.setForeground(Color.GRAY);
                    searchField.setFont(new Font("SansSerif", Font.ITALIC, 14));
                }
            }
        });
        
        //Εικόνα για το κουμπί "X" για τις επιλογές των textField και dropdown menu
        ImageIcon xbuttonIcon = new ImageIcon(getClass().getResource("/clear1.png"));
        
        //Δημιουργία κουμπιού για εκκαθάριση κειμένου
        clearSelectionButton = new JButton(xbuttonIcon);
        clearSelectionButton.setToolTipText("Clear text");                      //Ορισμός ToolTip
        clearSelectionButton.setPreferredSize(new Dimension(30, 28));

        //Όταν πατηθεί το κουμπί, διαγράφει το κείμενο του searchField
        clearSelectionButton.addActionListener(e -> {
            searchField.setText("Enter a keyword...");                          //Ορισμός ToolTip
            searchField.setForeground(Color.GRAY);
            searchField.setFont(new Font("SansSerif", Font.ITALIC, 14));
        });

        //Δημιουργία panel για το textField και το clear button
        searchPanel = new JPanel(new BorderLayout());
        searchPanel.setMaximumSize(new Dimension(250, 30));
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(clearSelectionButton, BorderLayout.EAST);               //Τοποθέτηση του κουμπιού δεξιά του textField 
        
        countryModel = new DefaultComboBoxModel<>();
        countryDropdown = new JComboBox<>(countryModel);                        //dropdown menu για τις χώρες
        countryDropdown.setToolTipText("Search by country");                    //Ορισμός ToolTip
        loadCountriesFromFile("countries.txt");                                 //Φόρτωση χωρών από text αρχείο τύπου CSV
        
        //Δημιουργία placeholder για την επιλογή χώρας
        countryDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value == null) {
                    setText("Select a country...");                             //Αν δεν είναι η εστίαση στο drop down menu και δεν έχουμε επιλέξει κάτι              
                    setForeground(Color.GRAY);                                  //Θέτουμε τον placeholder
                } else {
                    setText(value.toString()); 
                    setForeground(Color.BLACK);
                }

                return this;
            }
        });

        //Ορισμός αρχικής επιλογής για το placeholder
        countryDropdown.setSelectedIndex(-1);
        
        clearSelectionButton = new JButton(xbuttonIcon);                        //Δημιουργία κουμπιού για αποεπιλογή χώρας
        clearSelectionButton.setPreferredSize(new Dimension(30, 28));
        clearSelectionButton.setToolTipText("Clear selection");                 //Ορισμός ToolTip και επαναφορά του placeholder με το πάτημα του κουμπιού
        clearSelectionButton.addActionListener(e -> countryDropdown.setSelectedIndex(-1));  
        
        //Δημιουργία πάνελ για το dropdown με τις χώρες και το κουμπί εκκαθάρισης
        dropdownPanel = new JPanel(new BorderLayout());
        dropdownPanel.setMaximumSize(new Dimension(250, 30));
        dropdownPanel.add(countryDropdown, BorderLayout.CENTER);                //Προσθήκη κουμπιού και dropdown menu στο πάνελ
        dropdownPanel.add(clearSelectionButton, BorderLayout.EAST);
        
        searchButton = search();                                                //Υλοποίηση συνάρτησης αναζήτησης
        showStatisticsButton = showStatistics();                                //Υλοποίηση συνάρτησης προβολής στατιστικών
        exitButton = exitButton(frame);                                         //Υλοποίηση συνάρτησης εξόδου

        //Ορισμός ίδιου μεγέθους για όλα τα κουμπιά
        Dimension buttonSize = new Dimension(250, 50);  
        searchButton.setMaximumSize(buttonSize);
        showStatisticsButton.setMaximumSize(buttonSize);
        exitButton.setMaximumSize(buttonSize);
        
        //Τοποθέτηση κουμπιών στο κέντρο του παραθύρου
        searchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        showStatisticsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(Box.createRigidArea(new Dimension(0, 80)));                   // Κενό διάστημα ανάμεσα στην ετικέτα και τα κουμπιά
        panel.add(searchButton);
        panel.add(Box.createRigidArea(new Dimension(0, 30))); 
        panel.add(searchPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));                   // Κενό ανάμεσα στα κουμπιά
        panel.add(dropdownPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 30))); 
        panel.add(showStatisticsButton);
        panel.add(Box.createRigidArea(new Dimension(0, 30))); 
        panel.add(exitButton);      
    }
    
    //Μέθοδος για την εμφάνιση του παραθύρου
    public void show() {
        frame.setVisible(true);                                                 
        frame.requestFocusInWindow();                                           //Αφαιρεί το focus από το JTextField
    }
    
    //Μέθοδος για να επιστρέψουμε στο ίδιο παράθυρο όταν θα κλείσει το παράθυρο αναζήτησης (λόγω του JDialog)
    public static MainJFrame getInstance() {                                
        return instance;
    }
    
    //Μέθοδος δημιουργίας κουμπιού για αναζήτηση
    private JButton search() {
        searchButton = new JButton("Search Universities");                      //Κουμπί για αναζήτηση
        searchButton.setFocusable(false);                                       //Απενεργοποίηση εστίασης στο κουμπί
        searchButton.setToolTipText("Click to search for universities");        //Ορισμός ToolTip
        
        searchButton.addActionListener(e -> openSearchWindow());                //Μέθοδος για κουμπί που οδηγεί στην προβολή αναζήτησης
        
        return searchButton;
    }
    
    //Μέθοδος για προβολή αναζήτησης ανάλογα την επιλογή
    private void openSearchWindow() {
        isCountrySearch = false;                                                //Ορισμός μεταβλητής σε false για τις αναζητήσεις με keyword
        String keyword = searchField.getText().trim();
        String selectedCountry = (String) countryDropdown.getSelectedItem();

        if (keyword.equals("Enter a keyword...")) {                             //Καθαρισμός placeholder κατά την αναζήτηση
            keyword = "";
        }
        
        //Έλεγχος για κενή είσοδο
        if (keyword.isEmpty() && (selectedCountry == null || selectedCountry.isEmpty())) {
            JOptionPane.showMessageDialog(frame, "Type a keyword or select a country for search!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        //Έλεγχος για είσοδο άνω των τριών χαρακτήρων
        if (!keyword.isEmpty() && keyword.length() < 4) {
            JOptionPane.showMessageDialog(frame, "Give at least 4 characters.");
            return;
        }

        List<University> universities;                                          //Δημιουργία λίστας

        if (!keyword.isEmpty() && (selectedCountry == null || selectedCountry.isEmpty())) {  
            //Αναζήτηση μόνο με keyword
            universities = HttpConnector.getUniversities(keyword, null);
            if (universities == null) {                                         //Αν η λίστα είναι null, υπάρχει αποτυχία σύνδεσης
                return;
            }
            if (universities.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No universities found.", "No Results", JOptionPane.INFORMATION_MESSAGE);
            } else {
                //Μήνυμα για τον αριθμό των πανεπιστημίων που βρέθηκαν
                JOptionPane.showMessageDialog(frame, (universities.size() == 1) ? "1 university found."
                        : universities.size() + " universities found.", "Search Results", JOptionPane.INFORMATION_MESSAGE);
                openSearchResults(universities, false);                         //Καλούμε τη μέθοδο για εμφάνιση αποτελεσμάτων με JTable
            }
        } else if (!keyword.isEmpty() && selectedCountry != null && !selectedCountry.isEmpty()) {  
            //Αναζήτηση με keyword και country
            universities = HttpConnector.getUniversities(keyword, selectedCountry);
            if (universities == null) {                                         //Αν η λίστα είναι null, υπάρχει αποτυχία σύνδεσης
                return;
            }
            if (universities.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No universities found.", "No Results", JOptionPane.INFORMATION_MESSAGE);
            } else {
                //Μήνυμα για τον αριθμό των πανεπιστημίων που βρέθηκαν
                JOptionPane.showMessageDialog(frame, (universities.size() == 1) ? "1 university found."
                        : universities.size() + " universities found.", "Search Results", JOptionPane.INFORMATION_MESSAGE);
                openSearchResults(universities, false);                         //Καλούμε τη μέθοδο για εμφάνιση αποτελεσμάτων με JTable
            }
        } else {  
            //Αναζήτηση μόνο με country και ορισμός μεταβλητής σε true
            isCountrySearch = true;                                             
            universities = HttpConnector.getUniversities(null, selectedCountry);
            if (universities == null) {                                         //Αν η λίστα είναι null, υπάρχει αποτυχία σύνδεσης
                return;
            }
            if (universities.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No universities found.", "No Results", JOptionPane.INFORMATION_MESSAGE);
            } else {
                //Μήνυμα για τον αριθμό των πανεπιστημίων που βρέθηκαν
                JOptionPane.showMessageDialog(frame, (universities.size() == 1) ? "1 university found."
                        : universities.size() + " universities found.", "Search Results", JOptionPane.INFORMATION_MESSAGE);
                openUniversityList(universities, selectedCountry);              //Καλούμε τη μέθοδο για την εμφάνιση της JList
            }
        }
        //Με το άνοιγμα του νέου παραθύρου καθαρίζεται το text field
        searchField.setText("");                                                
        
        //Σε περίπτωση που υπάρχει αναζήτηση ενώ στην αρχή η βάση ήταν κενή ανανεώνουμε την κατάσταση του κουμπιού
        if (hasSearches()) {   
            showStatisticsButton.setEnabled(true);  
        }
    }
    
    //Μέθοδος για να γίνει πάλι visible το παράθυρο της JList μετά το κλείσιμο της αναζήτησης 
    public static void restoreListFrame() {
        if (isCountrySearch) {                                                  
            //Ελέγχουμε αν η αναζήτηση ήταν με βάση τη χώρα, κι αν ειναι εμφανίζουμε τη λίστα με τα τελευταία αποτελέσματα αναζήτησης
            getInstance().openUniversityList(lastUniversities, lastSelectedCountry); 
        }
    }
    
    //Μέθοδος για άνοιγμα παραθύρου Search
    private void openSearchResults(List<University> universities, boolean includeComments) {
        String[] selectedColumnNames; 
        //Ορίζουμε τα πεδία που θα εμφανιστούν στο παράθυρο Search, με χώρα υπάρχουν και σχόλια ενώ με keyword όχι
         if (includeComments) {
            selectedColumnNames = new String[]{"Name", "Domains", "Web Pages", "ALPHA_TWO_CODE", "COUNTRY", "STATE_PROVINCE", "Comments"};
         } else {
             selectedColumnNames = new String[]{"Name", "Domains", "Web Pages", "ALPHA_TWO_CODE", "COUNTRY", "STATE_PROVINCE"};
         }

        //Άνοιγμα του παραθύρου Search με τα δεδομένα των πανεπιστημίων, το κύριο παράθυρο και τα πεδία που θα εμφανιστούν
        SecondWindow searchWindow = new SecondWindow(universities, frame, selectedColumnNames);
        searchWindow.show();                                                    //Εμφάνιση του παραθύρου με τα αποτελέσματα αναζήτησης
    }

    //Μέθοδος για εμφάνιση της JList
    private void openUniversityList(List<University> universities, String selectedCountry) {
        lastUniversities = universities;                                        //Αποθήκευση λίστας για επανεμφάνιση όταν κλείσει το Search
        lastSelectedCountry = selectedCountry;                                  //Αποθήκευση χώρας για επανεμφάνιση όταν κλείσει το Search

        DefaultListModel<String> listModel = new DefaultListModel<>();          //Δημιουργούμε ένα DefaultListModel για τα αποτελέσματα αναζήτησης
        universities.forEach(u -> listModel.addElement(u.getName()));           //Προσθήκη του ονόματος του κάθε πανεπιστημίου στη λίστα

        universityList = new JList<>(listModel);
        universityList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane = new JScrollPane(universityList);                           //Προσθήκη της JList σε JScrollPane
        
        //Δημιουργία JDialog (ώστε να μην φεύγει το παράθυρο) για την εμφάνιση της JList
        listDialog = new JDialog(frame, "Universities in " + selectedCountry, true);
        listDialog.setSize(350, 250);
        listDialog.setLocationRelativeTo(frame);                                //Τοποθέτηση του παραθύρου στο κέντρο
        listDialog.setIconImage(new ImageIcon(getClass().getResource("/logo.png")).getImage());

        listButton = new JButton("Select");                                     //Κουμπί για επιλογή ενός στοιχείου από τη λίστα
        listButton.setToolTipText("Select an university and click to view more info");                         //Ορισμός ToolTip
        listButton.addActionListener(e -> {
            if (universityList.getSelectedIndex() == -1) {                      //Έλεγχος αν έχει γίνει κάποια επιλογή
                JOptionPane.showMessageDialog(listDialog, 
                    "Please select a university before proceeding.",            //Μήνυμα μη επιλογή στοιχείου απο τη λίστα
                    "No Selection", 
                    JOptionPane.WARNING_MESSAGE);
            } else {
                selectUniversity(universityList, listModel, universities);      //Εμφάνιση στοιχείων πανεπιστημίου
                listDialog.dispose();
            }
        });
        
        //Προσθήκη MouseListener για διπλό κλικ (αν δεν χρησιμοποιήσουμε το κουμπί)
        universityList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {                                 //Έλεγχος για διπλό κλικ
                    selectUniversity(universityList, listModel, universities);
                    listDialog.dispose();
                }
            }
        });
        
        //Προσθήκη MouseMotionListener για tooltip
        universityList.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int index = universityList.locationToIndex(e.getPoint());       //Βρίσκουμε το index του στοιχείου
                if (index > -1) {
                    University uni = universities.get(index);                   //Παίρνουμε το University αντικείμενο από τη λίστα
                    universityList.setToolTipText("University name: \"" + uni.getName() + "\". Double click for more info");
                }
            }
        });

        listPanel = new JPanel(new BorderLayout());
        listPanel.add(scrollPane, BorderLayout.CENTER);                         //Δημιουργία πάνελ για κουμπί και μπάρα κύλισης                   
        listPanel.add(listButton, BorderLayout.SOUTH);

        listDialog.add(listPanel);                                              //Προσθέτουμε το πάνελ στο παράθυρο
        listDialog.setVisible(true);                                            //Εμφανίζουμε το παράθυρο
    }

    //Μέθοδος επιλογής πανεπιστημίου από τη λίστα
    private void selectUniversity(JList<String> universityList, DefaultListModel<String> listModel, List<University> universities) {
        //Παίρνουμε το index του επιλεγμένου στοιχείου από την JList
        int index = universityList.getSelectedIndex();
        
        //Αν υπάρχει επιλογή
        if (index != -1) {
            //Παίρνουμε το όνομα του επιλεγμένου πανεπιστημίου από το listModel, το οποίο περιέχει τα ονόματα
            String selectedUniversityName = listModel.getElementAt(index);
            
            //Βρίσκουμε το αντίστοιχο αντικείμενο University από τη λίστα universities
            University selectedUniversity = universities.stream()               
                    .filter(u -> u.getName().equals(selectedUniversityName))    //Φιλτράρουμε τη λίστα για να βρούμε το πανεπιστήμιο με το ίδιο όνομα
                    .findFirst()                                                //Παίρνουμε το πρώτο που ταιριάζει, αν υπάρχει
                    .orElse(null);                                              //Αν δεν βρεθεί επιστρέφει null

            if (selectedUniversity != null) {
                listDialog.dispose();                                           //Αν υπάρχει επιλογή το παράθυρο της JList κλείνει
                openSearchResults(List.of(selectedUniversity), true);           //Άνοιγμα παραθύρου search με βάση την επιλογή
            }
        }
    }
    
    //Μέθοδος δημιουργίας κουμπιού για στατιστικά
    private JButton showStatistics() {
        showStatisticsButton = new JButton("Show Statistics");                  //Κουμπί προβολής στατιστικών
        showStatisticsButton.setFocusable(false);                               //Απενεργοποίηση εστίασης στο κουμπί
        showStatisticsButton.setToolTipText("Click to view statistics");        //Ορισμός ToolTip
        
        showStatisticsButton.addActionListener(e -> openStatisticsWindow());    //Μέθοδος για κουμπί που οδηγεί στην προβολή στατιστικών
        
        return showStatisticsButton;
    }
    
    //Μέθοδος για προβολή στατιστικών
    private void openStatisticsWindow() {
        List<Object[]> results = HttpConnector.getUniversitiesWithSearches();   //Παίρνουμε το αποτέλεσμα του querry από την getUniversitiesWithSearches
        
        //Δημιουργούμε μια λίστα πανεπιστημίων για να αποθηκεύσουμε τα αποτελέσματα
        List<University> universities = new ArrayList<>();
        for (Object[] row : results) {                                          //Μετατροπή των αποτελεσμάτων από Object σε αντικείμενα University
            University uni = new University();
            uni.setName((String) row[0]);                                       //Name
            uni.setSearches((Integer) row[1]);                                  //Searches
            universities.add(uni);                                              //Τα προσθέτουμε στη λίστα
        }
        
        //Ορίζουμε τις στήλες που θα εμφανιστούν
        String[] selectedColumnNames = {"Name", "Searches"};
        SecondWindow statisticsWindow = new SecondWindow(universities, frame, selectedColumnNames, true);
        statisticsWindow.show();
    }
    
    //Μέθοδος για διάβασμα χωρών από text αρχείο (για το drop down menu)
    private static void loadCountriesFromFile(String fileName) {        
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine();                                        //Το αρχείο είναι τύπου CSV σε μία σειρά

            if (line != null) {
                List<String> countries = Arrays.asList(line.split(",\\s*"));    //Διαχωρισμός των χωρών με βάση το κόμμα
                for (String country : countries) {
                    //Αφαίρεση εισαγωγικών και προσθήκη χωρών στο drop down menu
                    countryModel.addElement(country.replaceAll("\"", "").trim()); 
                }
            }
        } catch (IOException e) {   
            //Μήνυμα αν δεν φορτώνει το text αρχείο και ενημέρωση για το σφάλμα
            JOptionPane.showMessageDialog(null, "Error loading countries: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //Μέθοδος για ελέγξουμε αν υπάρχουν αναζητήσεις σε πανεπιστήμια
    private boolean hasSearches() {
        //Query για να δούμε αν υπάρχουν αναζητήσεις
        String queryString = "SELECT COUNT(u) FROM University u WHERE u.searches IS NOT NULL AND u.searches > 0";  
        Query query = Main.em.createQuery(queryString);
        Long count = (Long) query.getSingleResult();                            //Μετρητής για το πεδίο searches του πίνακα University
        return count > 0;  
    }
    
    //Μέθοδος για την κατάσταση του κουμπιού ανάλογα με τις αναζητήσεις
    private void updateButtonState() {
        boolean hasSearches = hasSearches();
        showStatisticsButton.setEnabled(hasSearches);                           //Αν υπάρχουν αναζητήσεις το κουμπί ενεργοποιείται
    }

    //Μέθοδος για κουμπί εξόδου
    public static JButton exitButton(JFrame frame) {
        JButton exitButton = new JButton("Exit");                               //Κουμπί εξόδου
        exitButton.setFocusable(false);                                         //Απενεργοποίηση εστίασης στο κουμπί
        exitButton.setToolTipText("Click to exit the application");             //Ορισμός ToolTip
        
        exitButton.setBackground(Color.RED);                                    //Αλλαγή του φόντο για το κουμπί σε κόκκινο
        exitButton.setForeground(Color.WHITE);                                  //Αλλαγή των γραμμάτων του κουμπιού σε άσπρο
        
        exitButton.setPreferredSize(new Dimension(130, 75));
        exitButton.addActionListener(e -> confirmExit(frame));                  //Με το πάτημα του κουμπιού γίνεται ερώτημα επιβεβαίωσης στο χρήστη
        
        return exitButton;
    }
    
    //Μέθοδος για επιβεβαίωση εξόδου από το χρήστη
    public static void confirmExit(JFrame frame) {
        JOptionPane optionPane = new JOptionPane(
            "Are you sure you want to exit?",                                   //Μήνυμα και επιλογές για την έξοδο
            JOptionPane.QUESTION_MESSAGE, 
            JOptionPane.YES_NO_OPTION
        );

        JDialog dialog = optionPane.createDialog(frame, "Exit Confirmation");   //Τίτλος παραθύρου επιβεβαίωσης
        dialog.setFocusableWindowState(false);                                  //Απενεργοποίηση εστίασης στα κουμπιά του JOptionPane
        dialog.setVisible(true);                                                //Κάνουμε το παράθυρο επιβεβαίωσης ορατό

        Object selectedValue = optionPane.getValue();
        if (selectedValue instanceof Integer && (Integer) selectedValue == JOptionPane.YES_OPTION) {
            frame.dispose();                                                    //Όταν η απάντηση είναι θετική τερματίζεται το πρόγραμμα
        }
    }
}