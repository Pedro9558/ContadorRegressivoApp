package br.com.pedro.contador.exceptions;

/**
 * @author Pedro9558
 */
public class NoSongInQueueException extends Exception {
    public NoSongInQueueException() {
        super("Não há nenhum som na fila");
    }
}
