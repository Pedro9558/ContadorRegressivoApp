package br.com.pedro.contador.song;


import br.com.pedro.contador.exceptions.NoSongInQueueException;

/**
 * Gerenciador de som usando MediaPlayer
 * @author Pedro9558
 */
public class SongManagerMediaPlayer implements SongManager {
    private float volume;
    private Song song;
    private static SongManagerMediaPlayer instance;
    private SongManagerMediaPlayer() {}

    /**
     * Retorna instancia do gerenciador de som
     * @return Gerenciador de som
     */
    public static SongManagerMediaPlayer getSongManager() {
        if(instance == null)
            instance = new SongManagerMediaPlayer();
        return instance;
    }

    @Override
    public void play() throws NoSongInQueueException {
        if(song == null)
            throw new NoSongInQueueException();
        if(!this.isPlaying()) {
            song.getAudio().start();
            new Thread(new PlayerManager()).start();
        }
    }

    @Override
    public void pause() throws NoSongInQueueException {
        if(song == null)
            throw new NoSongInQueueException();
        if(this.isPlaying() && !this.isPaused())
            song.getAudio().pause();
    }
    @SuppressWarnings("EmptyCatchBlock")
    @Override
    public void stop() throws NoSongInQueueException {
        if(song == null)
            throw new NoSongInQueueException();
        if(this.isPlaying()) {
            song.getAudio().seekTo(0);
            song.getAudio().stop();
            try {
                song.getAudio().prepare();
            }catch (java.io.IOException e) {}
        }
    }

    @Override
    public float getVolume() {
        return this.volume;
    }

    @Override
    public Song getSong() {
        return this.song;
    }

    @Override
    public void setLoopeable(boolean loop) {
        this.song.getAudio().setLooping(loop);
    }

    @Override
    public boolean isLoopeable() {
        return this.song.getAudio().isLooping();
    }

    @Override
    public void setSong(Song song) {
        if(!this.isPlaying())
            this.song = song;
    }

    @Override
    public void setVolume(float volume) {
        this.volume = (volume > 0.0f) ? volume : 0.0f;
        this.song.getAudio().setVolume(this.getVolume(),this.getVolume());
    }

    @Override
    public boolean isPlaying() {
        try {
            return song.getAudio() != null && song.getAudio().isPlaying();
        }catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    public boolean isPaused() {
        return !song.getAudio().isPlaying() && song.getAudio().getCurrentPosition() != 0;
    }

    /**
     * Classe responsavel por gerenciar se o audio está sendo reproduzido
     */
    class PlayerManager implements Runnable {
        @Override
        public void run() {
            while (isPlaying()) {
                try{
                    Thread.sleep(1);
                }catch (Exception e) {}
            }
            try {
                stop();
            } catch (NoSongInQueueException e) {
            }
        }
    }
}
