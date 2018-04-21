package br.com.pedro.contador.song;


import android.content.Context;
import android.media.MediaPlayer;

import br.com.pedro.contador.R;

public class SoundSamples {
    public static final short ALARME_SAMPLE = 0;
    public static final short SIRENE_SAMPLE = 1;
    public static final short DESPERTADOR_SAMPLE = 2;
    public static final short CAR_LOCK_SAMPLE = 3;

    private static Song ALARME = null;
    private static Song SIRENE = null;
    private static Song DESPERTADOR = null;
    private static Song CAR_LOCK = null;
    private static Context context;
    public static void setContext(Context context1) {
        context = context1;
        installSamples();
    }
    private static void installSamples() {
        ALARME = new Song("Alarme",MediaPlayer.create(context, R.raw.alarme));
        SIRENE = new Song("Sirene",MediaPlayer.create(context, R.raw.sirene));
        DESPERTADOR = new Song("Despertador",MediaPlayer.create(context, R.raw.despertador));
        CAR_LOCK = new Song("Destravar",MediaPlayer.create(context, R.raw.destravar));
    }
    public static Song getSample(short SampleID) {
        if (context == null)
            throw new NullPointerException("Context is null! SoundSamples.setContext() must be called first!");
        switch (SampleID) {
            case ALARME_SAMPLE: return ALARME;
            case SIRENE_SAMPLE: return SIRENE;
            case DESPERTADOR_SAMPLE: return DESPERTADOR;
            case CAR_LOCK_SAMPLE : return CAR_LOCK;
        }
        return null;
    }
}
