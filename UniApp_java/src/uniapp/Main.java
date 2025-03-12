/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package uniapp;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.swing.SwingUtilities;

public class Main {
    //Factory για τη δημιουργία EntityManager, που διαχειρίζεται τη σύνδεση με τη βάση δεδομένων
    private static EntityManagerFactory emf;
    //EntityManager για την εκτέλεση λειτουργιών
    static EntityManager em;
    
    //Μέθοδος για την επιστροφή του EntityManager, ώστε να χρησιμοποιείται σε άλλες κλάσεις
    public static EntityManager getEM() {
        return em;
    }

    public static void main(String[] args) {
        //Δημιουργία του EntityManagerFactory για το Persistence
        emf = Persistence.createEntityManagerFactory("UniAppPU");
        //Δημιουργία του EntityManager για τη διαχείριση των συναλλαγών με τη βάση δεδομένων
        em = emf.createEntityManager();
        
        //Εμφάνιση GUI
        SwingUtilities.invokeLater(() -> {
            MainJFrame frame = new MainJFrame();    
            frame.show();
        });
    } 
}