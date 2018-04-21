package br.com.pedro.contador.exceptions;


public class NoSongInQueueException extends Exception {
    public NoSongInQueueException() {
        super("Não há nenhum som na fila");
    }
}
