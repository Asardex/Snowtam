package com.example.quent.snowtam;


import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Snowtam {

    /** Contient le snowtam non traduit **/
    private String indicateurEmplacement; //A)
    private String publicationDate; //B)
    private ArrayList<Runway> runways; //C) - P)
    private String airDeTrafic; //R)
    private String prochaineObservation; //S)
    private String remarques; //T)

    /** Contient le snowtam traduit **/
    private String indicateurEmplacement_decode; //A)
    private String publicationDate_decode; //B)
    //voir dans Runway pour C) - P)
    private String airDeTrafic_decode; //R)
    private String prochaineObservation_decode; //S)
    private String remarques_decode; //T)


    private String snowtam;
    private String snowtamTranslated;

    public Snowtam(String snowtamString) {
        if(snowtamString.contains("(") && snowtamString.contains(".)")) {
            snowtam = snowtamString;
            parseSnowtam(snowtamString);
            translateSnowtam();
        } else {
            Log.d("SNOWTAM1", "Snowtam non ouvert ou terminé");
        }
    }

    public String toString() {
        String ret = new String();
        ret = "A) " + indicateurEmplacement + "\n" +
                "B) " + publicationDate + "\n";
        for (Runway r : runways) {
            ret += r.toString() + "\n";
        }
        if(!airDeTrafic.isEmpty())
            ret += "R)" + airDeTrafic + "\n";
        if(!prochaineObservation.isEmpty())
            ret += "S)" + prochaineObservation + "\n";
        if(!remarques.isEmpty())
            ret += "T)" + remarques;
        Log.d("SNOWTAM", "'" + ret + "'");
        return ret;
    }

    private void translateSnowtam() {
        translateA();
        translateB();
        translateR();
        translateS();
        translateT();

        snowtamTranslated = new String();
        snowtamTranslated = indicateurEmplacement_decode + "\n" +
                publicationDate_decode + "\n";
        for (Runway r : runways) {
            snowtamTranslated += r.translated();
        }
        if(!airDeTrafic_decode.isEmpty())
            snowtamTranslated += airDeTrafic_decode + "\n";
        if(!prochaineObservation_decode.isEmpty())
            snowtamTranslated += prochaineObservation_decode + "\n";
        if(!remarques_decode.isEmpty())
            snowtamTranslated += remarques_decode;
        Log.d("SNOWTAM", "'" + snowtamTranslated + "'");
    }

    private void translateA() {
        //TODO : récuperer le nom de l'aéroport avec le travail de marine. En attendant :
        indicateurEmplacement_decode = new String(indicateurEmplacement);
        Log.d("TRANSLATE", "A) : '" + indicateurEmplacement_decode + "'");
    }

    private void translateB() {
        //mmddhhmm -- 11250621
        publicationDate_decode = new String();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddhhmm");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(publicationDate);
        } catch (ParseException e) {
            Log.e("DATE", "Erreur lors du parsing de la date");
        }
        convertedDate.setYear(Calendar.getInstance().get(Calendar.YEAR)-1900);
        publicationDate_decode = convertedDate.toString();
        Log.d("TRANSLATE", "B) : '" + publicationDate_decode + "'");
    }

    private void translateR() {
        airDeTrafic_decode = new String();
        if(!airDeTrafic.isEmpty()) {
            if(airDeTrafic.contains("NO")) {
                airDeTrafic_decode = "aprons are unusable";
            }
            else {
                for (char c : airDeTrafic.toCharArray()) {
                    if('0' <= c && c <= '9') {
                        int numero = c - '0';
                        airDeTrafic_decode = "aprons are " + condition(numero);
                    }
                }
            }
        }
        Log.d("TRANSLATE", "R) : '" + airDeTrafic_decode + "'");
    }

    private String condition(int numero) {
        switch (numero) {
            case 0: return "CLEAR AND DRY";
            case 1: return "DAMP";
            case 2: return "WET";
            case 3: return "RIME";
            case 4: return "DRY SNOW";
            case 5: return "WET SNOW";
            case 6: return "SLUSH";
            case 7: return "ICE";
            case 8: return "COMPACTED";
            case 9: return "FROZEN RUTS";
            default: return "";
        }
    }

    private void translateS() {
        prochaineObservation_decode = new String();
        if(!prochaineObservation.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMddhhmm");
            Date convertedDate = new Date();
            try {
                convertedDate = dateFormat.parse(prochaineObservation);
            } catch (ParseException e) {
                Log.e("DATE", "Erreur lors du parsing de la date");
            }
            convertedDate.setYear(Calendar.getInstance().get(Calendar.YEAR)-1900);
            prochaineObservation_decode = convertedDate.toString();
        }
        Log.d("TRANSLATE", "S) : '" + prochaineObservation_decode + "'");
    }

    private void translateT() {
        remarques_decode = new String();
        if(!remarques.isEmpty()) {
            remarques_decode = remarques;
        }
        Log.d("TRANSLATE", "T) : '" + remarques_decode + "'");
    }

    private void parseSnowtam(String str) {
        indicateurEmplacement = parseA(str);
        publicationDate = parseB(str);
        runways = parseRunways(str);
        airDeTrafic = parseR(str);
        prochaineObservation = parseS(str);
        remarques = parseT(str);
    }

    private String parseA(String str) {
        String a = new String();
        if(str.contains("A)")) {
            a = str.split("A\\)")[1].split("[B-T]\\)")[0]; //récupère la chaine de caractère comprise entre A) et Une lettre + ')'
            a = a.trim(); //enlève les " " et "\n" de la chaine de caractère
        }
        Log.d("A)", "'" + a + "'");
        return a;
    }

    private String parseB(String str) {
        String b = new String();
        if(str.contains("B)")) {
            b = str.split("B\\)")[1].split("[C-T]\\)")[0];
            b = b.trim();
        }
        Log.d("B)", "'" + b + "'");
        return b;
    }

    private ArrayList<Runway> parseRunways(String str) {
        ArrayList<Runway> run = new ArrayList<Runway>();
        int count = 0, i = 0;
        while((i = str.indexOf("C)", i)) != -1) {
            count++; i++;
        }
        Log.d("NB_RUNWAY", Integer.toString(count));
        for(i = 0; i < count; i++) {
            run.add(new Runway(str, i));
        }
        return run;
    }

    private String parseR(String str) {
        String r = new String();
        if(str.contains("R)")) {
            r = str.split("R\\)")[1].split("[S-T]\\)")[0];
            r = r.trim();
        }
        Log.d("R)", "'" + r + "'");
        return r;
    }

    private String parseS(String str) {
        String s = new String();
        if(str.contains("S)")) {
            s = str.split("S\\)")[1].split("T\\)")[0];
            s = s.trim();
        }
        Log.d("S)", "'" + s + "'");
        return s;
    }

    private String parseT(String str) {
        String t = new String();
        if(str.contains("T)")) {
            t = str.split("T\\)")[1].split("\\)")[0];
            t = t.trim();
        }
        Log.d("T)", "'" + t + "'");
        return t;
    }

    public String translated() {
        return snowtamTranslated;
    }
}
