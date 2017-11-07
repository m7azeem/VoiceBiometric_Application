package com.neurotec.samples;

import java.util.ArrayList;

public class DataAccessObject {

    public DataAccessObject(){

    }

    public boolean initializeStorage(){
        XMLController XMLController = new XMLController();
        XMLController.makeNewXMLDocument();
        return true;
    }

    public boolean enrollUser(User person){
        XMLController XMLController = new XMLController();
        return XMLController.addUser(person);
    }

    //returns -1 if no old passphraseID found.
    public int getNextPassphraseID(){
        return -1;
    }

    public boolean loadTemplates(){
        //get templates from storage and enroll them.
        return false;
    }

    //saves the current test-templateFile and its test-sound.
    public boolean saveTest(){
        return false;
    }
}
