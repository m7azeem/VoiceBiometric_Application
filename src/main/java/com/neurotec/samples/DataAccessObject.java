package com.neurotec.samples;

public class DataAccessObject {

    public DataAccessObject(){

    }
    public boolean initializeStorage(){
        return false;
    }

    public boolean enrollUser(){
        return false;
    }

    //returns -1 if no old passphraseID found.
    public int getNextPassphraseID(){
        return -1;
    }

    public boolean loadTemplates(){
        return false;
    }

    //saves the current test-template and its test-sound.
    public boolean saveTest(){
        return false;
    }
}
