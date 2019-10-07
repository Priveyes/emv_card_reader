package com.example.sebastianczuma.czytnikkart;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.sebastianczuma.czytnikkart.LayoutClassesEmvInfo.RecyclerViewAdapterHospital;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import io.github.binaryfoo.DecodedData;
import io.github.binaryfoo.RootDecoder;
import io.github.binaryfoo.cmdline.DecodedWriter;


public class MainActivity extends Activity {
    NfcAdapter nfcAdapter;
    IsoDep isoDep;
    RecyclerView rView;
    List<EmvDetails> allInfo;
    EmvDetails one;
    String ask;
    String answer;
    String decodedd;
    List<String> multipleAns;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pd = new ProgressDialog(this);
        pd.setMessage("Trwa odczyt z karty...");

        GridLayoutManager lLayout = new GridLayoutManager(this, 1);
        rView = (RecyclerView) findViewById(R.id.recycler_view);

        // Recycle View setup
        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);

        SpaceItemDecoration dividerItemDecoration
                = new SpaceItemDecoration(30);
        rView.addItemDecoration(dividerItemDecoration);

        allInfo = new ArrayList<>();
        multipleAns  = new ArrayList<>();


        recreateRecycler();

        //addone();
        // Grab a hold of the nfc sensor
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    void addone(String a, String b, String c) {
        EmvDetails one1 = new EmvDetails();
        one1.setAsk(a);
        one1.setAnswear(b);
        one1.setDecoded(c);
        allInfo.add(one1);
        recreateRecycler();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (this.nfcAdapter != null) {
            this.ensureSensorIsOn();

            // We'd like to listen to all incoming NFC tags that support the IsoDep interface
            nfcAdapter.enableForegroundDispatch(this,
                    PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0),
                    new IntentFilter[]{new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)},
                    new String[][]{new String[]{IsoDep.class.getName()}});
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (this.nfcAdapter != null) {
            this.nfcAdapter.disableForegroundDispatch(this);
        }
    }

    //@Override
    protected void onNewIntent(Intent intent) {
        //super.onNewIntent(intent);



        // Is the intent for a new NFC tag discovery?
        if (intent != null && intent.getAction() == NfcAdapter.ACTION_TECH_DISCOVERED) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            isoDep = IsoDep.get(tag);

            // Does the tag support the IsoDep interface?
            if (isoDep == null) {
                return;
            }

            try {
                pd.show();
                isoDep.connect();
                //decodeTLV(transceive("00 A4 00 00 00"));

                transceive("00 A4 04 00 0E 32 50 41 59 2E 53 59 53 2E 44 44 46 30 31 00");
                //transceive("00 B2 01 0C 1C");
                transceive("00 A4 04 02 07 A0 00 00 00 04 10 10");
                sendMultiple();
                transceive("80 A8 00 00 02 83 00");

/*
                transceive("80 CA 9F 36 00");

                transceive("80 CA 9F 13 00");

                transceive("80 CA 9F 17 00");

                transceive("80 CA 9F 4F 00");
                sendMultiple();

                //transakcja
                /*decodeTLV(transceive("00 A4 04 00 0E 32 50 41 59 2E 53 59 53 2E 44 44 46 30 31 00"));
                decodeTLV(transceive("00 A4 04 02 07 A0 00 00 00 04 10 10"));
                decodeTLV(transceive("80 A8 00 00 0A 83 08 00 00 00 00 00 00 00 00 00"));
                decodeTLV(transceive("00 B2 01 14 00"));
                decodeTLV(transceive("00 B2 01 1C 00"));
                decodeTLV(transceive("00 B2 02 1C 00"));
                decodeTLV(transceive("00 B2 01 24 00"));
                decodeTLV(transceive("00 B2 02 24 00"));
                decodeTLV(transceive("80 AE 50 00 2B 00 00 00 00 01 00 00 00 00 00 00 00 01 24 00 00 00 00 00 01 24 14 02 25 00 ED 03 F6 3B 22 00 00 00 00 00 00 00 00 00 00 1F 03 02 00"));
                */

                isoDep.close();
                pd.dismiss();
            } catch (Exception ex) {
                Log.e("blad ", ex + " ");
            }
        }
    }

    void sendMultiple() {
        for (int sfi = 1; sfi <= 92; sfi++) {
            for (int rec = 1; rec <= 16; rec++) {
                String allData = "00 B2 ";
                String rec1 = Integer.toHexString(rec);
                String sfi1 = Integer.toHexString(sfi);
                if (sfi1.length() < 2) {
                    sfi1 = "0" + sfi1;
                }
                if (rec1.length() < 2) {
                    rec1 = "0" + rec1;
                }
                String end = " 00";
                String full = allData + rec1 + " " + sfi1 + end;
                try {
                    transceive(full);
                } catch (Exception ex) {
                }
            }
        }
    }

    protected byte[] transceive(String hexstr) throws IOException {
        String[] hexbytes = hexstr.split("\\s");
        byte[] bytes = new byte[hexbytes.length];
        for (int i = 0; i < hexbytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hexbytes[i], 16);
        }
        byte[] recv = isoDep.transceive(bytes);
        if (recv.length > 2) {

            easyLog("Send: " + SharedUtils.Byte2Hex(bytes));
            easyLog("Received: " + SharedUtils.Byte2Hex(recv));
            //easyLog("RAW: " + new String(recv, "ISO-8859-1"));

            if(!multipleAns.contains(SharedUtils.Byte2Hex(recv))) {
                multipleAns.add(SharedUtils.Byte2Hex(recv));
                ask = SharedUtils.Byte2Hex(bytes);
                answer = SharedUtils.Byte2Hex(recv);

                easyLog("Length: " + recv.length);

                decodeTLV(recv);
            }
        }

        return recv;
    }

    void decodeTLV(byte[] recv) {
        String look = SharedUtils.Byte2Hex(recv);
        look = look.replace(" ", "");

        List<DecodedData> decoded = new RootDecoder().decode(look, "EMV", "constructed");
        //easyLog("6 lijnia 6 " + decoded.get(0).component6().get(1).component6().get(0).component6().get(0).component6().get(0).getDecodedData());

/*
        easyLog("rozmiar " + decoded.size());
        easyLog("1 lijnia " + decoded.get(0).component1());
        easyLog("2 lijnia " + decoded.get(0).component2());
        easyLog("3 lijnia " + decoded.get(0).component3());
        easyLog("4 lijnia " + decoded.get(0).component4());
        easyLog("5 lijnia " + decoded.get(0).component5());
        easyLog("6 lijnia 1 " + decoded.get(0).component6().get(1).component1());

        easyLog("6 lijnia 2 " + decoded.get(0).component6().get(1).component2());
        easyLog("6 lijnia 3 " + decoded.get(0).component6().get(1).component3());
        easyLog("6 lijnia 4 " + decoded.get(0).component6().get(1).component4());
        easyLog("6 lijnia 5 " + decoded.get(0).component6().get(1).component5());

        easyLog("6 lijnia 6 " + decoded.get(0).component6().get(1).getChildren().size());

        easyLog("6 lijnia 6 " + decoded.get(0).component6().get(1).component6().get(0).component6().get(0).component6().get(0).getChildren().size());
        easyLog("6 lijnia 7 " + decoded.get(0).component6().get(1).component7());
        easyLog("6 lijnia 8 " + decoded.get(0).component6().get(1).component8());
        easyLog("6 lijnia 9 " + decoded.get(0).component6().get(1).component9());
        easyLog("7 lijnia " + decoded.get(0).component7());
        easyLog("8 lijnia " + decoded.get(0).component8());

*/
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        new DecodedWriter(ps).write(decoded, "");
        easyLog(baos.toString());

        decodedd = baos.toString();

        addone(ask, answer, decodedd);

    }

    private void ensureSensorIsOn() {
        if (!this.nfcAdapter.isEnabled()) {
            // Alert the user that NFC is off
            new AlertDialog.Builder(this)
                    .setTitle("NFC Sensor Turned Off")
                    .setMessage("In order to use this application, the NFC sensor must be turned on. Do you wish to turn it on?")
                    .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Send the user to the settings page and hope they turn it on
                            if (android.os.Build.VERSION.SDK_INT >= 16) {
                                startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
                            } else {
                                startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                            }
                        }
                    })
                    .setNegativeButton("Do Nothing", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Do nothing
                        }
                    }).show();
        }
    }

    protected void makeEasyToast(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    protected void easyLog(String msg) {
        Log.e("EASY LOG", msg);
    }

    public void recreateRecycler() {
        RecyclerViewAdapterHospital rcAdapter = new RecyclerViewAdapterHospital(
                allInfo, this);
        rView.setAdapter(rcAdapter);

        rView.setNestedScrollingEnabled(false);
    }

    private class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int space;

        SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = space;
            outRect.left = space;
        }
    }
}