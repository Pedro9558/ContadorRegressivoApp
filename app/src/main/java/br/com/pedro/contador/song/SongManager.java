package br.com.pedro.contador.song;


import br.com.pedro.contador.exceptions.NoSongInQueueException;

public interface SongManager {
    void play() throws NoSongInQueueException;
    void pause() throws NoSongInQueueException;
    void stop() throws NoSongInQueueException;
    float getVolume();
    Song getSong();
    void setLoopeable(boolean loop);
    boolean getLoopeable();
    void setSong(Song song);
    void setVolume(float volume);
    boolean isPlaying();
    boolean isPaused();
}
