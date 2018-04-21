package br.com.pedro.contador;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Spinner;

import br.com.pedro.contador.exceptions.NoSongInQueueException;
import br.com.pedro.contador.song.SongManager;
import br.com.pedro.contador.song.SongManagerMediaPlayer;
import br.com.pedro.contador.song.SoundSamples;
import br.com.pedro.contador.timer.Timer;

/**
 * @author Pedro9558
 */
public class MainActivity extends AppCompatActivity {
    // Texto de ajuda
    private String helpText;
    private boolean userChanged = false;
    private Timer t;
    // Componentes da tela
    private Spinner songs;
    private SongManager songManager;
    private Button previewButton;
    private ImageButton help1;
    private ImageButton help2;
    private View mainLayout;
    private EditText horas;
    private EditText minutos;
    private EditText segundos;
    @SuppressWarnings("all")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Passa o Context da aplicação para o SoundSamples para que se haja a instância da biblioteca
        SoundSamples.setContext(getApplicationContext());
        // Instancia dos valores
        songs = findViewById(R.id.sons);
        help1 = findViewById(R.id.ajuda1);
        help2 = findViewById(R.id.ajuda2);
        horas = findViewById(R.id.horas_texto);
        minutos = findViewById(R.id.minutos_texto);
        segundos = findViewById(R.id.segundos_texto);
        previewButton = findViewById(R.id.preview);
        mainLayout = findViewById(R.id.layout);
        songManager = SongManagerMediaPlayer.getSongManager();
        songs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(userChanged) {
                    // Caso o som escolhido seja nenhum, desliga-se o botão de prévia por não haver nenhum som a ser reproduzido
                    if(songs.getSelectedItem().toString().equals(getResources().getStringArray(R.array.sons)[0])) {
                        previewButton.setEnabled(false);
                    }else{
                        previewButton.setEnabled(true);
                        // Caso haja algum som sendo reproduzido no meio da mudança do som, interrompe-se o mesmo
                        if(songManager.getSong() != null && songManager.isPlaying()) {
                            try {
                                songManager.stop();
                            } catch (NoSongInQueueException e) {
                            }
                        }
                    }
                }else{
                    userChanged = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        // Animação de seleção dos botões de ajuda
        help1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:help1.setBackground(getResources().getDrawable(R.drawable.roundedbutton));
                        break;
                    case MotionEvent.ACTION_DOWN:help1.setBackground(getResources().getDrawable(R.drawable.roundedbutton_clicked));
                        break;
                }
                return false;
            }
        });
        help2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:help2.setBackground(getResources().getDrawable(R.drawable.roundedbutton));
                        break;
                    case MotionEvent.ACTION_DOWN:help2.setBackground(getResources().getDrawable(R.drawable.roundedbutton_clicked));
                        break;
                }
                return false;
            }
        });
        new Thread(new Rainbow()).start();
    }

    /**
     * Handler dos botões de ajuda
     * @param v - Instancia do botão
     */
    public void helpHandler(View v) {
        if(v instanceof ImageButton){
            // Altera a mensagem de ajuda dependendo do botão de ajuda apertado
            switch (v.getId()) {
                case R.id.ajuda1:this.setHelpText(getResources().getString(R.string.ajuda1));
                    break;
                case R.id.ajuda2:this.setHelpText(getResources().getString(R.string.ajuda2));
                    break;
            }
            Toast.makeText(getApplicationContext(),this.getHelpText(),Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Handler do botão de prévia
     * @param v - Instancia do botão
     */
    public void preview(View v) {
        if(v instanceof Button){
            if(v.getId() == R.id.preview) {
                if(!songManager.isPlaying()) {
                    // Captura o som selecionado
                    String selected = songs.getSelectedItem().toString();
                    previewButton.setBackgroundColor(getResources().getColor(R.color.green));
                    previewButton.setText(getResources().getString(R.string.previa_tocando));
                    if (selected.equals(getResources().getStringArray(R.array.sons)[1])) {
                        songManager.setSong(SoundSamples.getSample(SoundSamples.ALARME_SAMPLE));
                    } else if (selected.equals(getResources().getStringArray(R.array.sons)[2])) {
                        songManager.setSong(SoundSamples.getSample(SoundSamples.SIRENE_SAMPLE));
                    } else if (selected.equals(getResources().getStringArray(R.array.sons)[3])) {
                        songManager.setSong(SoundSamples.getSample(SoundSamples.DESPERTADOR_SAMPLE));
                    }
                    // Reproduz uma prévia do som
                    try {
                        songManager.play();
                    } catch (NoSongInQueueException e) {
                        e.printStackTrace();
                    }
                    new Thread(new PlayerManager()).start();
                }else{
                    // Interrompe caso usuário aperte o botão de novo
                    try {
                        songManager.stop();
                    } catch (NoSongInQueueException e) {
                    }
                }
            }
        }
    }

    /**
     * Handler do botão iniciar
     * @param v - Instancia do botão
     */
    public void iniciar(View v) {
        // Variaveis responsaveis por capturar os inputs
        int iHoras = 0;
        int iMinutos = 0;
        int iSegundos = 0;
        horas.setBackgroundColor(getResources().getColor(R.color.white));
        minutos.setBackgroundColor(getResources().getColor(R.color.white));
        segundos.setBackgroundColor(getResources().getColor(R.color.white));
        boolean temInputs = true;
        // Captura as variaveis de input
        try {
            iHoras = Integer.parseInt(horas.getText().toString());
            iMinutos = Integer.parseInt(minutos.getText().toString());
            iSegundos = Integer.parseInt(segundos.getText().toString());
        }catch (Exception e) {
            temInputs = false;
            // Caso nada tenha sido passado em pelo menos um dos inputs, imprime mensagem de erro na tela
            if(horas.getText().toString().equals("")) {
                horas.setBackgroundColor(getResources().getColor(R.color.invalido));
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.erro_valor_indefinido).replace("{variavel}","Horas"), Toast.LENGTH_SHORT).show();
            }else if(minutos.getText().toString().equals("")) {
                minutos.setBackgroundColor(getResources().getColor(R.color.invalido));
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.erro_valor_indefinido).replace("{variavel}","Minutos"),Toast.LENGTH_SHORT).show();
            }else if(segundos.getText().toString().equals("")) {
                segundos.setBackgroundColor(getResources().getColor(R.color.invalido));
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.erro_valor_indefinido).replace("{variavel}","Segundos"),Toast.LENGTH_SHORT).show();
            }
        }
        // Caso haja inputs
        if(temInputs) {
            // Testa se os inputs são válidos
            if (iHoras < 0) {
                horas.setBackgroundColor(getResources().getColor(R.color.invalido));
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.erro_valor_negativo).replace("{variavel}", "Horas"), Toast.LENGTH_LONG).show();
            } else {
                if (iMinutos > 59) {
                    minutos.setBackgroundColor(getResources().getColor(R.color.invalido));
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.erro_valor_invalido).replace("{variavel}", "Minutos").replace("{min}", String.valueOf(0)).replace("{max}", String.valueOf(59)), Toast.LENGTH_LONG).show();
                } else if (iMinutos < 0) {
                    minutos.setBackgroundColor(getResources().getColor(R.color.invalido));
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.erro_valor_negativo).replace("{variavel}", "Minutos"), Toast.LENGTH_LONG).show();
                } else {
                    if (iSegundos > 59) {
                        segundos.setBackgroundColor(getResources().getColor(R.color.invalido));
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.erro_valor_invalido).replace("{variavel}", "Segundos").replace("{min}", String.valueOf(0)).replace("{max}", String.valueOf(59)), Toast.LENGTH_LONG).show();
                    } else if (iSegundos < 0) {
                        segundos.setBackgroundColor(getResources().getColor(R.color.invalido));
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.erro_valor_negativo).replace("{variavel}", "Segundos"), Toast.LENGTH_LONG).show();
                    } else {
                        // Caso todos os inputs sejam válidos
                        horas.setBackgroundColor(getResources().getColor(R.color.white));
                        minutos.setBackgroundColor(getResources().getColor(R.color.white));
                        segundos.setBackgroundColor(getResources().getColor(R.color.white));
                        t = Timer.getTimer().setHoras(iHoras)
                                .setMinutos(iMinutos)
                                .setSegundos(iSegundos);
                        // Conector a outra atividade
                        Intent intent = new Intent(this, Contador.class);
                        String selected = songs.getSelectedItem().toString();
                        // Testa que som foi escolhido e adiciona na fila do gerenciador de som
                        if (selected.equals(getResources().getStringArray(R.array.sons)[1])) {
                            songManager.setSong(SoundSamples.getSample(SoundSamples.ALARME_SAMPLE));
                        } else if (selected.equals(getResources().getStringArray(R.array.sons)[2])) {
                            songManager.setSong(SoundSamples.getSample(SoundSamples.SIRENE_SAMPLE));
                        } else if (selected.equals(getResources().getStringArray(R.array.sons)[3])) {
                            songManager.setSong(SoundSamples.getSample(SoundSamples.DESPERTADOR_SAMPLE));
                        } else if(selected.equals(getResources().getStringArray(R.array.sons)[0])) {
                            songManager.setSong(null);
                        }
                        horas.setText("");
                        minutos.setText("");
                        segundos.setText("");
                        // Inicia a atividade do contador
                        startActivity(intent);
                    }
                }
            }
        }
    }
    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(),getResources().getString(R.string.agradecimento),Toast.LENGTH_LONG).show();
        finish();
    }
    public String getHelpText() {
        return this.helpText;
    }
    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }

    /**
     * Gerencia a prévia do som, checando se a mesma continua sendo reproduzida
     */
    class PlayerManager implements Runnable {
        @Override
        public void run() {
            while (songManager.isPlaying()) {
                try{
                    Thread.sleep(1);
                }catch (Exception e) {}
            }
            // Caso o som pare de ser reproduzido, volta as configurações do botão prévia ao seu estado original
            try {
                songManager.stop();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        previewButton.setBackgroundColor(getResources().getColor(R.color.transparent_gray));
                        previewButton.setText(getResources().getString(R.string.previa));
                    }
                });
            } catch (NoSongInQueueException e) {
            }
        }
    }

    /**
     * Responsavel pela a animação arco-iris do background da aplicação
     */
    class Rainbow implements Runnable {
        // Indice RGB
        private int red = 80;
        private int green = 0;
        private int blue = 0;
        @Override
        public void run() {
            // Loop infinito
            while (true) {
                // Altera a cor do background da tela dependendo dos valores do indice RGB
                // Vermelho -> Verde
                while (red > 0) {
                    try {
                        Thread.sleep(30);
                    }catch (Exception e) {}
                    red -= 1;
                    green += 1;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainLayout.setBackgroundColor(Color.argb(100, red, green, blue));
                        }
                    });
                }
                // Verde -> Azul
                while(green > 0) {
                    try {
                        Thread.sleep(30);
                    }catch (Exception e) {}
                    green -= 1;
                    blue += 1;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainLayout.setBackgroundColor(Color.argb(100, red, green, blue));
                        }
                    });
                }
                // Azul -> Vermelho
                while (blue > 0) {
                    try {
                        Thread.sleep(30);
                    }catch (Exception e) {}
                    blue -= 1;
                    red += 1;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainLayout.setBackgroundColor(Color.argb(100, red, green, blue));
                        }
                    });
                }
            }
        }
    }
}
