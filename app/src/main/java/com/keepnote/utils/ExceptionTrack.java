package com.keepnote.utils;


import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Objects;

public class ExceptionTrack {


    private static ExceptionTrack instance = null;
    private ExceptionTrack() {

    }
    public static ExceptionTrack getInstance() {
        if (instance == null) {
            instance = new ExceptionTrack();
        }
        return instance;
    }



    public void TrackLog(Exception e){

        setTrack(e);
        FirebaseCrashlytics.getInstance().recordException(e);
    }

    private void setTrack(Exception e){
        FirebaseCrashlytics.getInstance().setUserId("");
        FirebaseCrashlytics.getInstance().log(Objects.requireNonNull(e.getMessage()));

    }
}
