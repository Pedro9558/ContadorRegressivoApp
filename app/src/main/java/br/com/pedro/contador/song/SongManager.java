package br.com.pedro.contador.song;


import br.com.pedro.contador.exceptions.NoSongInQueueException;

/**
 * @author Pedro9558
 */
public interface SongManager {
    /**
     * Reproduz o audio caso haja algum na fila
     * @throws NoSongInQueueException - Caso não haja nenhum som na fila
     */
    void play() throws NoSongInQueueException;

    /**
     * Pausa o audio tocado, caso haja algum audio sendo tocado
     * @throws NoSongInQueueException - Caso não haja nenhum som na fila
     */
    void pause() throws NoSongInQueueException;

    /**
     * Para o audio tocado, caso haja algum audio sendo tocado
     * @throws NoSongInQueueException - Caso não haja nenhum som na fila
     */
    void stop() throws NoSongInQueueException;

    /**
     * Retorna o volume atual do audio
     * @return Volume do audio
     */
    float getVolume();

    /**
     * Retorna uma instancia do som que está na fila
     * @return Uma instancia de Song
     */
    Song getSong();

    /**
     * Seta se o audio poderá ser reproduzido em loop,
     * @param loop
     */
    void setLoopeable(boolean loop);

    /**
     * Retorna se o audio está reproduzindo em loop
     * @return
     */
    boolean isLoopeable();

    /**
     * Adiciona um som na fila para reprodução
     * @param song - Song
     */
    void setSong(Song song);

    /**
     * Altera o volume do audio
     * @param volume - Novo valor do volume
     */
    void setVolume(float volume);

    /**
     * Retorna se o audio está sendo reproduzino
     * @return true se o audio está sendo reproduzido
     */
    boolean isPlaying();

    /**
     * Retorna se o audio está pausado
     * @return true se o audio está pausado
     */
    boolean isPaused();
}
