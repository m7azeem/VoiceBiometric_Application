package com.neurotec.samples;

public class User {
    int id=-1;
    String name="";
    String passphrase="";
    int phraseId=-1;
    String soundFile="";
    String templateFile="";

    public User() {
    }

    public User(int id, String name, String passphrase, int phraseId, String soundFile, String templateFile) {
        this.id = id;
        this.name = name;
        this.passphrase = passphrase;
        this.phraseId = phraseId;
        this.soundFile = soundFile;
        this.templateFile = templateFile;
    }
}