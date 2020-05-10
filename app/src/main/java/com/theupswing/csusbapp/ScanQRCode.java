package com.theupswing.csusbapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

public class ScanQRCode extends AppCompatActivity {

    CodeScanner codeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        codeScanner = new CodeScanner(this, scannerView);

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateDatabase(result.getText());
                    }
                });
            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }

    private void updateDatabase(String location){
        DatabaseHelper database = new DatabaseHelper(ScanQRCode.this);
        if(database.isInDatabase(database.COL_LOC, location)){
            database.updateVisited(location, 1);
            showAlertDialog(location);
        } else {
            Toast.makeText(ScanQRCode.this, "Code not recognized", Toast.LENGTH_LONG).show();
        }
    }

    private void showAlertDialog(final String location) {

        // Get data from database to display
        DatabaseHelper database = new DatabaseHelper(ScanQRCode.this);
        String description = database.getDescription(location);
        int image = database.getImage(location);

        // Inflate alert_dialog.xml in order to edit its views
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootLayout = inflater.inflate(R.layout.alert_dialog, (ViewGroup) findViewById(R.id.root_container));

        // Edit the views
        TextView descriptionText = rootLayout.findViewById(R.id.description_text);
        descriptionText.setText(description);
        ImageView imageView = rootLayout.findViewById(R.id.location_image);
        imageView.setImageResource(image);

        // Build and show the alert dialog
        new AlertDialog.Builder(ScanQRCode.this)
                .setView(rootLayout)
                .setTitle("You added " + location + "!")
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(ScanQRCode.this, ListOfPlaces.class);
                        startActivity(intent);
                    }
                })
                .show();
    }
}
