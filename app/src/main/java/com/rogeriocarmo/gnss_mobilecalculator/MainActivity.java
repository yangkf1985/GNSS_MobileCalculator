package com.rogeriocarmo.gnss_mobilecalculator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import Codigos.CoordenadaGPS;
import Codigos.EpocaGPS;
import Codigos.EpocaObs;
import Codigos.Rinex2Writer;

import static Codigos.ProcessamentoPPS.calcPseudorange;
import static Codigos.ProcessamentoPPS.getObservacoes;
import static Codigos.ProcessamentoPPS.getResultadosGeodeticos;
import static Codigos.ProcessamentoPPS.processar_epoca;
import static Codigos.ProcessamentoPPS.processar_todas_epocas;
import static Codigos.ProcessamentoPPS.readLogger_RawAssets;
import static Codigos.ProcessamentoPPS.readRINEX_RawAssets;

public class MainActivity extends AppCompatActivity {

    private Button btnVisualizar;
    private Button btnRINEX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnVisualizar = findViewById(R.id.idVisualizar);
        btnRINEX = findViewById(R.id.btnRINEX);

        btnVisualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Resultado.class);

                ArrayList<CoordenadaGPS> valores = getResultadosGeodeticos();

                intent.putParcelableArrayListExtra("Coord",valores);
                startActivity(intent);
            }
        });

        btnRINEX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                startActivity(intent);

//
                ArrayList<EpocaObs> observacoes = getObservacoes();//

                Rinex2Writer RINEX = new Rinex2Writer(getApplicationContext(),observacoes);
                RINEX.gravarRINEX();
                RINEX.send();

//                Intent intent = new Intent(getApplicationContext(), RINEX_Activity.class);
//
//                intent.putParcelableArrayListExtra("Obs",observacoes);
//                startActivity(intent);
            }
        });

        try {
            readLogger_RawAssets(MainActivity.this);
        } catch (IOException e) {
            Log.e("ERR_log","Erro ao abrir o arquivo de Log");
            String msg = e.getMessage();
            Toast.makeText(getApplicationContext(),
                "Erro ao abrir o arquivo de log: " + msg, Toast.LENGTH_LONG).show();
        }

        try{
            calcPseudorange();
        } catch (Exception e){
            Log.e("ERR_pr","Erro ao calcular pseudodistâncias");
            String msg = e.getMessage();
            Toast.makeText(getApplicationContext(),
                "Erro ao calcular as pseudodistâncias: " + msg, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        try {
            readRINEX_RawAssets(MainActivity.this);
        } catch (IOException e) {
            Log.e("ERR_ef","Erro ao abrir o RINEX");
            String msg = e.getMessage();
            Toast.makeText(getApplicationContext(),
                "Erro ao abrir o arquivo de efemérides: " + msg, Toast.LENGTH_LONG).show();
        }

        try{
//            processar_todas_epocas();
            processar_epoca(76);
        } catch (Exception e){
            Log.e("ERR_coord","Execucao unica");
            e.printStackTrace();
            String msg = e.getMessage();
            Toast.makeText(getApplicationContext(),
                "Erro ao calcular as coordenadas do satélite: " + msg,
                Toast.LENGTH_LONG).show();
        }

    }
}



















