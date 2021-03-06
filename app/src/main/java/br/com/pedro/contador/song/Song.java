package br.com.pedro.contador.song;


import android.content.Context;
import android.media.MediaPlayer;

/**
 * Classe responsavel por armazenar dados do som
 * @author Pedro9558
 */
public class Song {
    private String name;
    // Instancia do audio
    private MediaPlayer audio;
    public Song(String nome, MediaPlayer audio) {
        this.name = nome;
        this.audio = audio;
    }
    public Song(String nome, Context context, int resId){
        this.name = nome;
        this.audio = MediaPlayer.create(context, resId);
    }
    public String getName() {
        return name;
    }
    public MediaPlayer getAudio() {
        return audio;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAudio(MediaPlayer audio) {
        this.audio = audio;
    }
}
