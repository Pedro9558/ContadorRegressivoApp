package br.com.pedro.contador;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.VibrationEffect;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Vibrator;
import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import br.com.pedro.contador.exceptions.NoSongInQueueException;
import br.com.pedro.contador.song.SongManager;
import br.com.pedro.contador.song.SongManagerMediaPlayer;
import br.com.pedro.contador.timer.Timer;
/**
 * @author Pedro9558
 */
public class Contador extends AppCompatActivity {
    // Variaveis de controle
    private boolean contando = false;
    private boolean perguntar = true;
    private boolean animacao = true;
    private boolean interrompidoPeloUsuario = false;
    // Timer do cronometro
    private Timer t;
    // Variaveis de controle do cronometro
    private int iHoras,iMinutos,iSegundos;
    private String sHoras,sMinutos,sSegundos;
    // Gerente de som
    private SongManager songManager;
    // Conteudo da tela
    private TextView contador;

    private Vibrator vibrator;
    private int[] colors;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contador);
        // Instanciando as variaveis
        contador = findViewById(R.id.timer);
        songManager = SongManagerMediaPlayer.getSongManager();
        t = Timer.getTimer();
        contando = true;
        // Captura um serviço do sistema Android responsavel pela vibração do celular
        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        // Captura o tempo setado pelo usuário no Timer
        iHoras = t.getHoras();
        iMinutos = t.getMinutos();
        iSegundos = t.getSegundos();
        // Monta a interface do contador de acordo com os valores iniciais
        if(iHoras < 10) {
            sHoras = String.format(Locale.getDefault(),"%02d",iHoras);
        }else{
            sHoras = String.valueOf(iHoras);
        }
        if(iMinutos < 10) {
            sMinutos = String.format(Locale.getDefault(),"%02d",iMinutos);
        }else{
            sMinutos = String.valueOf(iMinutos);
        }
        if(iSegundos < 10) {
            sSegundos = String.format(Locale.getDefault(), "%02d",iSegundos);
        }else{
            sSegundos = String.valueOf(iSegundos);
        }
        // Instancia o array de cores para a animação
        colors = new int[]{getResources().getColor(R.color.bright_pink),
                getResources().getColor(R.color.red),
                getResources().getColor(R.color.orange),
                getResources().getColor(R.color.yellow),
                getResources().getColor(R.color.chartreuse),
                getResources().getColor(R.color.green),
                getResources().getColor(R.color.spring_green),
                getResources().getColor(R.color.cyan),
                getResources().getColor(R.color.azure),
                getResources().getColor(R.color.blue),
                getResources().getColor(R.color.violet),
                getResources().getColor(R.color.magenta)};
        new Thread(new Tempo()).start();
    }
    @Override
    public void onBackPressed() {
        // Caso o contador esteja rodando ainda, é necessário perguntar ao usuário se ele deseja interrompe-lo e voltar ao menu inicial
        if(perguntar) {
            new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.alerta_titulo))
                    .setMessage(getResources().getString(R.string.alerta_mensagem))
                    .setPositiveButton(getResources().getString(R.string.alerta_sim), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            contando = false;
                            interrompidoPeloUsuario = true;
                            animacao = false;
                            if (songManager.isPlaying()) {
                                try {
                                    songManager.stop();
                                } catch (NoSongInQueueException e) {
                                    e.printStackTrace();
                                }
                            }
                            Toast.makeText(getApplicationContext(), "Contador interrompido!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.alerta_nao), null)
                    .setIcon(getResources().getDrawable(android.R.drawable.alert_light_frame))
                    .show();
        }else{
            contando = false;
            interrompidoPeloUsuario = true;
            animacao = false;
            if (songManager.isPlaying()) {
                try {
                    songManager.stop();
                } catch (NoSongInQueueException e) {
                    e.printStackTrace();
                }
            }
            Toast.makeText(getApplicationContext(), "Contador interrompido!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        // Requere uma API superior a 16 para as notificações funcionarem
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (contando) {
                Notification.Builder notification = new Notification.Builder(getApplicationContext())
                        .setContentTitle(getResources().getString(R.string.notificacao_titulo))
                        .setContentText(getResources().getString(R.string.notificacao_mensagem))
                        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                        .setAutoCancel(true);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Intent intent = new Intent(getApplicationContext(), Contador.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent intentPendente = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                notification.setContentIntent(intentPendente);
                if (manager != null) {
                    manager.notify(android.R.drawable.ic_lock_idle_alarm, notification.build());
                }
            }
        }
    }
    /**
     * Handler do botão interromper
     * @param v - Instancia do botão
     */
    public void interromper(View v) {
        if(v instanceof Button) {
            // Testa se o botão apertado é o interromper
            if(v.getId() == R.id.botaoc) {
                // Caso o contador esteja rodando ainda, é necessário perguntar ao usuário se ele deseja interrompe-lo e voltar ao menu inicial
                if (perguntar) {
                    new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.alerta_titulo))
                            .setMessage(getResources().getString(R.string.alerta_mensagem))
                            .setPositiveButton(getResources().getString(R.string.alerta_sim), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    contando = false;
                                    interrompidoPeloUsuario = true;
                                    animacao = false;
                                    if (songManager.isPlaying()) {
                                        try {
                                            songManager.stop();
                                        } catch (NoSongInQueueException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    Toast.makeText(getApplicationContext(), "Contador interrompido!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .setNegativeButton(getResources().getString(R.string.alerta_nao), null)
                            .setIcon(getResources().getDrawable(android.R.drawable.alert_light_frame))
                            .show();
                }else{
                    contando = false;
                    interrompidoPeloUsuario = true;
                    animacao = false;
                    if (songManager.isPlaying()) {
                        try {
                            songManager.stop();
                        } catch (NoSongInQueueException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(getApplicationContext(), "Contador interrompido!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    /**
     * Thread do contador
     */
    class Tempo implements Runnable {
        private byte aux = 0;
        @Override
        public void run() {
            contador.setText(getResources().getString(R.string.timer_ativo).replace("{h}",sHoras).replace("{m}",sMinutos).replace("{s}",sSegundos));
            // Contador inicia
            while (contando) {
                // Finaliza contador caso ele tenha zerado
                if(t.getHoras() + t.getMinutos() + t.getSegundos() <= 0) {
                    contando = false;
                    break;
                }
                try{
                    Thread.sleep(1000);
                }catch (Exception e) {}
                // Tempo
                if(t.getSegundos() > 0) {
                    t.setSegundos(t.getSegundos() - 1);
                }else{
                    t.setSegundos(59);
                    if(t.getMinutos() > 0) {
                        t.setMinutos(t.getMinutos() - 1);
                    }else{
                        t.setMinutos(59);
                        if(t.getHoras() > 0) {
                            t.setHoras(t.getHoras() - 1);
                        }
                     }
                }
                // Alterar interface de uma outra Thread não autorizada exige que um metódo autorizado seja chamado
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iHoras = t.getHoras();
                        iMinutos = t.getMinutos();
                        iSegundos = t.getSegundos();
                        // Altera cor da tela para cada segundo que passa
                        findViewById(R.id.contadorBackground).setBackgroundColor(colors[(int)(Math.random()*colors.length)]);
                        if(iHoras < 10) {
                            sHoras = String.format(Locale.getDefault(),"%02d",iHoras);
                        }else{
                            sHoras = String.valueOf(iHoras);
                        }
                        if(iMinutos < 10) {
                            sMinutos = String.format(Locale.getDefault(),"%02d",iMinutos);
                        }else{
                            sMinutos = String.valueOf(iMinutos);
                        }
                        if(iSegundos < 10) {
                            sSegundos = String.format(Locale.getDefault(), "%02d",iSegundos);
                        }else{
                            sSegundos = String.valueOf(iSegundos);
                        }
                        contador.setText(getResources().getString(R.string.timer_ativo).replace("{h}",sHoras).replace("{m}",sMinutos).replace("{s}",sSegundos));
                    }
                });
            }
            // Contador terminou, mas não por interrupção do usuário
            if(!interrompidoPeloUsuario) {
                perguntar = false;
                // Reproduz som escolhido caso algum tenha sido escolhido
                if(songManager.getSong() != null) {
                    songManager.setLoopeable(true);
                    try {
                        songManager.play();
                    } catch (NoSongInQueueException e) {
                        e.printStackTrace();
                    }
                }
                // Animação para chamar a atenção do usuário que o tempo do contador acabou
                while (animacao) {
                    try{
                        Thread.sleep(500);
                    }catch (Exception e) {}
                    // Em versões mais antigas do Android, se usa o metódo antigo que passa só os milisegundos
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    }else{
                        vibrator.vibrate(200);
                    }
                    if(aux == 0) {
                        aux++;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                findViewById(R.id.contadorBackground).setBackgroundColor(getResources().getColor(R.color.red));
                            }
                        });
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                findViewById(R.id.contadorBackground).setBackgroundColor(getResources().getColor(R.color.blue));
                            }
                        });
                        aux--;
                    }
                }
            }
        }
    }
}
