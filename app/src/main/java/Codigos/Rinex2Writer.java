package Codigos;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

//import android.support.v4.BuildConfig;

import static Codigos.FileHelper.getPrivateStorageDir;
import static Codigos.FileHelper.isExternalStorageWritable;
import static Codigos.FileHelper.writeTextFile2External;

public class Rinex2Writer {


    private ArrayList<CoordenadaGPS> listaEpocas;
    private static final String FILE_PREFIX = "GNSS";
    private static final String PSEUDORANGE = "C"; // GPS C/A
    private static final String FREQ_GPS = "L1";
    private static final String RINEX_TYPE = "O"; // Observation File
    private static final String MARKER = "GNSS Mobile Calculator";

    private File newFile;
    private final Context mContext;
    private ArrayList<String> txtContent;

    public boolean gravarRINEX(){
        if (isExternalStorageWritable()){
            newFile = startNewLog();
            criarCabecalho();
//            escrever_observacoes(); TODO
            try {
                writeTextFile2External(newFile,
                                        txtContent.toArray(new String[txtContent.size()]));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }
            return false;
    }

    public Rinex2Writer(Context mContext, ArrayList<CoordenadaGPS> epocas) {
        this.mContext = mContext;
        this.txtContent = new ArrayList<>();
        this.listaEpocas = epocas;
    }

    public Rinex2Writer(Context context){
        this.mContext = context;
        this.txtContent = new ArrayList<>();
    }

    private void criarCabecalho() {
        txtContent.add("     2.11           OBSERVATION DATA    M (MIXED)           RINEX VERSION / TYPE"); // TODO SEPARAR EM 2 COLUNAS COM 2 APPENDS
        txtContent.add("\n");
        txtContent.add("teqc  2018Mar15                         20180605 08:43:32UTCPGM / RUN BY / DATE");
        txtContent.add("\n");
        txtContent.add("PPTE                                                        MARKER NAME");
        txtContent.add("\n");
        txtContent.add("41611M002                                                   MARKER NUMBER");
        txtContent.add("\n");
        txtContent.add("RBMC                IBGE/CGED                               OBSERVER / AGENCY");
        txtContent.add("\n");
        txtContent.add("5215K84090          TRIMBLE NETR9       5.33                REC # / TYPE / VERS");
        txtContent.add("\n");
        txtContent.add("4923353208          TRM59800.00     NONE                    ANT # / TYPE");
        txtContent.add("\n");

        //TODO AUTOMATIZAR
        String Xaprx = new DecimalFormat("########.####").format(3687624.3674);
        String Yaprx = new DecimalFormat("########.####").format(-4620818.6827);
        String Zaprx = new DecimalFormat("########.####").format(-2386880.3805);

        StringBuilder lineAprx = new StringBuilder();

        lineAprx.append("  " + Xaprx);
        lineAprx.append(" "  + Yaprx);
        lineAprx.append(" "  + Zaprx);

        lineAprx.append("                  APPROX POSITION XYZ");
        txtContent.add(lineAprx.toString());
        txtContent.add("\n");

        txtContent.add("        0.0020        0.0000        0.0000                  ANTENNA: DELTA H/E/N");//fixme rever
        txtContent.add("\n");
        txtContent.add("     1     1                                                WAVELENGTH FACT L1/2");
        txtContent.add("\n");

        //        txtContent.add("    11    L1    L2    L5    C1    P1    C2    P2    C5    S1# / TYPES OF OBSERV");
        String typeLine1 = String.format("    %s    %s    %s    %s    %s    %s    %s    %s    %s    %s# / TYPES OF OBSERV",
                                     "  ", "  ", "  ", "  ", "C1", "  ", "  " , "  ", "  ", "  "); // C/A Pseudorange only

        //        txtContent.add("          S2    S5                                          # / TYPES OF OBSERV");
        String typeLine2 = String.format("          %s    %s                                          # / TYPES OF OBSERV",
                                        "  ", "  ");

        txtContent.add(typeLine1);
        txtContent.add("\n");
        txtContent.add(typeLine2);
        txtContent.add("\n");
        txtContent.add("    15.0000                                                 INTERVAL");// FIXME USAR String.form
        txtContent.add("\n");
        txtContent.add("    18                                                      LEAP SECONDS");// FIXME USAR String.form
        txtContent.add("\n");
        txtContent.add("Trabalho de Conclusao de Curso - FCT/UNESP - Computacao     COMMENT");
        txtContent.add("\n");
        txtContent.add("Autor: Rogerio Ramos Rodrigues do Carmo                     COMMENT");
        txtContent.add("\n");
        txtContent.add("Arquivo gerado a partir de medicoes de aparelho Android     COMMENT");
        txtContent.add("\n");

        //TODO AUTOMATIZAR ESSES NÚMEROS
        String firstObs = String.format("  %d     %d     %d     %d     %d    0.0000000     GPS         TIME OF FIRST OBS",
                2018, 6, 4,0,0, new DecimalFormat("#.####### ").format(0.0000000) );

        txtContent.add(firstObs);
        txtContent.add("\n");
        txtContent.add("                                                            END OF HEADER");
        txtContent.add("\n");
    }

    public File startNewLog() {
            File baseDirectory = null;
            if ( isExternalStorageWritable() ) {
                try {
                    String fileName = String.format("%s_%s.18o", FILE_PREFIX, "Teste_01");
                    baseDirectory = getPrivateStorageDir(mContext,fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("RINEX", "Cannot read external storage.");
                return null;
            }

            return baseDirectory;
    }

    private void escrever_observacoes(){
        throw new java.lang.UnsupportedOperationException("Not supported yet.");
    }

    public void send(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        Uri uri = Uri.parse(newFile.getAbsolutePath());
        intent.setDataAndType(uri, "text/plain");
        mContext.startActivity(intent);
    }

}
