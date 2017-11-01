package com.neurotec.samples;

import java.io.IOException;
import java.util.EnumSet;

import com.neurotec.biometrics.NBiometricOperation;
import com.neurotec.biometrics.NBiometricStatus;
import com.neurotec.biometrics.NBiometricTask;
import com.neurotec.biometrics.NMatchingResult;
import com.neurotec.biometrics.NSubject;
import com.neurotec.biometrics.NVoice;
import com.neurotec.biometrics.client.NBiometricClient;
import com.neurotec.licensing.NLicense;
import com.neurotec.samples.util.LibraryManager;
import com.neurotec.samples.util.Utils;

public class IdentifyVoiceTemplate {
    private static final String DESCRIPTION = "Demonstrates voice identification";
    private static final String NAME = "identify-voice";
    private static final String VERSION = "10.0.0.0";

    private void usage() {
        System.out.println("usage:");
        System.out.format("\t%s [probe voice] [one or more gallery voices]%n", NAME);
        System.out.println();
    }

    //requires array of fileNames. With first fileName being of the testSubject,
    // and the rest from the training data
    public void identify(String[] args) {
        final String components = "Biometrics.VoiceExtraction,Biometrics.VoiceMatching";

        LibraryManager.initLibraryPath();

        //Utils.printTutorialHeader(DESCRIPTION, NAME, VERSION, args);

        if (args.length < 2) {
            usage();
            System.exit(1);
        }

        NBiometricClient biometricClient = null;
        NSubject probeSubject = null;
        NBiometricTask enrollTask = null;

        try {
            if (!NLicense.obtainComponents("/local", 5000, components)) {
                System.err.format("Could not obtain licenses for components: %s%n", components);
                System.exit(-1);
            }

            biometricClient = new NBiometricClient();
            probeSubject = createSubject(args[0], "ProbeSubject");

            NBiometricStatus status = biometricClient.createTemplate(probeSubject);

            if (status != NBiometricStatus.OK) {
                System.out.format("Failed to create probe template. Status: %s.\n", status);
                System.exit(-1);
            }

            enrollTask = biometricClient.createTask(EnumSet.of(NBiometricOperation.ENROLL), null);
            for (int i = 1; i < args.length; i++) {
                enrollTask.getSubjects().add(createSubject(args[i], args[i]/*String.format("GallerySubject_%d", i)*/));
            }
            biometricClient.performTask(enrollTask);
            if (enrollTask.getStatus() != NBiometricStatus.OK) {
                System.out.format("Enrollment was unsuccessful. Status: %s.\n", enrollTask.getStatus());
                if (enrollTask.getError() != null) throw enrollTask.getError();
                System.exit(-1);
            }

            biometricClient.setMatchingThreshold(36);

            status = biometricClient.identify(probeSubject);

            if (status == NBiometricStatus.OK) {
                for (NMatchingResult result : probeSubject.getMatchingResults()) {
                    System.out.format("Matched with ID: '%s' with score %d\n", result.getId(), result.getScore());
                }
            } else if (status == NBiometricStatus.MATCH_NOT_FOUND) {
                System.out.format("Match not found");
            } else {
                System.out.format("Identification failed. Status: %s.\n", status);
                System.exit(-1);
            }
        } catch (Throwable th) {
            //Utils.handleError(th);
            System.out.println("Error in IdentifyVoiceTemplate, try/catch");
        } finally {
            if (enrollTask != null) enrollTask.dispose();
            if (probeSubject != null) probeSubject.dispose();
            if (biometricClient != null) biometricClient.dispose();
        }
    }

    private static NSubject createSubject(String fileName, String subjectId) {
        NSubject subject = new NSubject();
        NVoice voice = new NVoice();
        voice.setFileName(fileName);
        subject.getVoices().add(voice);
        subject.setId(subjectId);
        return subject;
    }

}
