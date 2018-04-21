package br.com.pedro.contador.timer;

@SuppressWarnings("all")
public class Timer {
    private int horas;
    private int minutos;
    private int segundos;
    private static Timer timer;
    public static Timer getTimer() {
        if(timer == null)
            timer = new Timer();
        return timer;
    }
    private Timer() {}
    private Timer(int horas, int minutos, int segundos) {
        if(horas < 0 || minutos < 0 || segundos < 0)
            throw new IllegalArgumentException("Tempo não pode ser negativo");
        this.horas = horas;
        this.minutos = minutos;
        this.segundos = segundos;
    }

    public int getHoras() {
        return horas;
    }

    public Timer setHoras(int horas) {
        if(horas<0)
            throw new IllegalArgumentException("Hora não pode ser negativa");
        this.horas = horas;
        return this.updatedTimer();
    }

    public int getMinutos() {
        return minutos;
    }

    public Timer setMinutos(int minutos) {
        if(minutos < 0)
            throw new IllegalArgumentException("Minuto não pode ser negativo");
        this.minutos = minutos;
        return this.updatedTimer();
    }

    public int getSegundos() {
        return segundos;
    }

    public Timer setSegundos(int segundos) {
        if(segundos<0)
            throw new IllegalArgumentException("Segundo não pode ser negativo");
        this.segundos = segundos;
        return this.updatedTimer();
    }
    private Timer updatedTimer() {
        timer = new Timer(getHoras(), getMinutos(), getSegundos());
        return timer;
    }
    public int getMinimo() {
        return 0;
    }
    public int getMaximo() {
        return 59;
    }
}
