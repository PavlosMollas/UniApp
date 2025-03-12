/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uniapp;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.swing.JOptionPane;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import static uniapp.Main.getEM;

//Κλάση για τη κλήση στο API και διαχείρηση των Json
public class HttpConnector {
    private static final OkHttpClient client = new OkHttpClient();              //Αντικείμενο OkHttpClient για τη σύνδεση στο internet
    private static Gson gson = new Gson();                                      //Αντικείμενο Gson για διαχείρηση των JSON
    
    //Μέθοδος για σύνδεση στο internet και αναζήτηση πανεπιστημίων με βάση το όνομα και/ή τη χώρα
    private static String getJson(String name, String country) {
        String jsonResponse = "";

        //Επιλογή σωστού URL ανάλογα με τη χώρα
        String url;

        //Προσθήκη των παραμέτρων στο URL
        if ("united states".equalsIgnoreCase(country)) {
            url = "https://uniapp.hmu.gr/search?country=united%20states";       //Διαφορετικό Url για united states για σωστά αποτελέσματα
        } else {
            url = "http://universities.hipolabs.com/search?";                   //Κυρίως url για τις υπόλοιπες αναζητήσεις

            // Προσθήκη των παραμέτρων στο URL
            if (name != null && !name.isEmpty()) {
                url += "name=" + name;
            }
            if (country != null && !country.isEmpty()) {
                if (url.contains("name=")) {
                    url += "&country=" + country;
                } else {
                    url += "country=" + country;
                }
            }
        }

        //Δημιουργία του HTTP αιτήματος με το URL
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            //Αν η απάντηση είναι επιτυχής επιστρέφουμε το JSON
            if (response.isSuccessful() && response.body() != null) {
                jsonResponse = response.body().string();
            }
        } catch (IOException e) {
            //Σε περίπτωση αποτυχίας σύνδεσης εμφανίζεται μήνυμα
            JOptionPane.showMessageDialog(null, "Failed to connect to the internet. Please check your connection and try again.", 
                                          "Connection Error", JOptionPane.ERROR_MESSAGE);
            return null;                                                        //Επιστρέφουμε null αν αποτύχει η σύνδεση
        }

        return jsonResponse;                                                    //Επιστροφή του JSON ως String
    }
    
    //Μέθοδος για αναζήτηση πανεπιστημίων με βάση το όνομα και/ή τη χώρα
    public static List<University> getUniversities(String name, String country) {
        EntityManager em = getEM();                                             //Χρησιμοποιούμε τον EntityManager για πρόσβαση στη βάση δεδομένων

        String returnedJson = getJson(name, country);                           //Κλήση της μεθόδου getJson για να πάρουμε τα δεδομένα από το API
        
        if (returnedJson == null) {                                             //Αν δεν υπάρχει σύνδεση ή δεν έχουμε απάντηση επιστρέφουμε null
            return null;  
        }

        if (returnedJson.isEmpty()) {                                           //Αν η απάντηση είναι κενή επιστρέφουμε άδεια λίστα
            return new ArrayList<>();
        }

        JsonElement jTree = JsonParser.parseString(returnedJson);               //Μετατροπή του JSON string σε JsonElement για επεξεργασία
        JsonArray jsonarr = jTree.getAsJsonArray();                             //Δημιουργία πίνακα από τα πανεπιστήμια

        List<University> universities = new ArrayList<>();
        em.getTransaction().begin();                                            //Διαδικασία για εισαγωγή/ενημέρωση βάσης δεδομένων

        Set<String> addedUniversities = new HashSet<>();                        //Χρησιμοποιούμε Set για να αποφεύγουμε τα διπλότυπα πανεπιστήμια               
        
        //Επεξεργασία κάθε στοιχείου από το JSON array
        for (JsonElement element : jsonarr) {
            JsonObject object = element.getAsJsonObject();
            String uniName = object.get("name").getAsString();                  //Όνομα πανεπιστημίου
            String uniCountry = object.get("country").getAsString();            //Χώρα πανεπιστημίου
            String uniAlphaTwoCode = object.get("alpha_two_code").getAsString();//Κωδικός χώρας

            String uniqueKey = uniName + "-" + uniCountry;                      //Το uniqueKey είναι name, country για να αποφύγουμε διπλότυπα

            if (addedUniversities.contains(uniqueKey)) {                        //Αν το πανεπιστήμιο υπάρχει ήδη στη λίστα το παραλείπουμε
                continue;
            }

            // Αναζητούμε στη βάση δεδομένων με βάση το όνομα και τη χώρα
            TypedQuery<University> query = em.createQuery(
                "SELECT u FROM University u WHERE u.name = :name AND u.country = :country", University.class
            );
            query.setParameter("name", uniName);
            query.setParameter("country", uniCountry);
            
            //Παίρνουμε το πανεπιστήμιο από τη βάση, αν υπάρχει
            University university = query.getResultList().stream().findFirst().orElse(null);
            
            if (university == null) {                                           //Αν το πανεπιστήμιο δεν υπάρχει, το δημιουργούμε
                university = new University();
                university.setName(uniName);
                university.setCountry(uniCountry);
                university.setSearches(1);                                      //Αρχικοποιούμε το πεδίο searches
                
                //Αν υπάρχουν πεδία domains, τα προσθέτουμε
                if (object.has("domains") && object.get("domains").isJsonArray() && object.get("domains").getAsJsonArray().size() > 0) {
                    String[] output = gson.fromJson(object.get("domains"), String[].class);
                    university.setDomains(String.join(", ", output));
                }
                
                // Αν υπάρχουν πεδία web_pages, τα προσθέτουμε
                if (object.has("web_pages") && object.get("web_pages").isJsonArray() && object.get("web_pages").getAsJsonArray().size() > 0) {
                    String[] output = gson.fromJson(object.get("web_pages"), String[].class);
                    university.setWebPages(String.join(", ", output));
                }

                university.setAlphaTwoCode(uniAlphaTwoCode);                    
                //Αν υπάρχει πεδίο state-province το προσθέτουμε
                university.setStateProvince(object.has("state-province") ? gson.fromJson(object.get("state-province"), String.class) : null);

                em.persist(university);                                         //Αποθήκευση του νέου πανεπιστημίου στη βάση δεδομένων
            } else {
                university.setSearches(university.getSearches() + 1);           //Αν το πανεπιστήμιο υπάρχει ήδη στη βάση αυξάνουμε το πεδίο searches
                em.merge(university);                                           //Ενημέρωση του πανεπιστημίου στη βάση δεδομένων
            }

            addedUniversities.add(uniqueKey);                                   //Προσθήκη του πανεπιστημίου στη λίστα και στο Set για να αποφύγουμε διπλότυπα
            universities.add(university);
        }

        em.getTransaction().commit();                                           //Ολοκλήρωση της διαδικασίας στη βάση δεδομένων

        return universities;                                                    //Επιστρέφουμε τη λίστα με τα πανεπιστήμια
    }  
    
    //Μέθοδος για πανεπιστήμια που έχουν αναζητήσεις
    public static List<Object[]> getUniversitiesWithSearches() {
        //Querry για τα πεδία name και searches, όπου τα searches είναι θετικά
        String queryString = "SELECT u.name, u.searches FROM University u WHERE u.searches IS NOT NULL AND u.searches > 0 ORDER BY u.searches DESC"; 

        Query query = Main.em.createQuery(queryString);                         //Δημιουργία Querry
        
        query.setMaxResults(40);                                                //Παίρνουμε τα πρώτα 40 αποτελέσματα ως δημοφιλέστερα αν υπάρχουν περισσότερα από αυτά

        return query.getResultList();       
    }
}