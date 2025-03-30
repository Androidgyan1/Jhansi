package com.jhansi;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.OutputStream;

public class PrintReceptScreen extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 100;
    TextView clientid,TxnMsg,TxnTime,rrn,Name,FName,EmailID,Mobile,ReceivedBy,JalkalId,Zone_Name,Ward_Name
            ,Mohlla_Name,AmountRecived,Paymentmode,TotalTax;
    Button print;
    private RelativeLayout main;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_print_recept_screen);
        // Apply window insets
        main = findViewById(R.id.main);
        if (main != null) {
            ViewCompat.setOnApplyWindowInsetsListener(main, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        clientid = findViewById(R.id.refrence_number);
        TxnMsg = findViewById(R.id.TxnMsg);
        TxnTime = findViewById(R.id.TxnTime);
        rrn = findViewById(R.id.RRN);
        Name = findViewById(R.id.Name);
        FName = findViewById(R.id.FName);
        EmailID = findViewById(R.id.EmailID);
        Mobile = findViewById(R.id.Mobile);
        ReceivedBy = findViewById(R.id.ReceivedBy);
        JalkalId = findViewById(R.id.JalkalId);
        Zone_Name = findViewById(R.id.Zone_Name);
        Ward_Name = findViewById(R.id.Ward_Name);
        Mohlla_Name = findViewById(R.id.Mohlla_Name);
        AmountRecived = findViewById(R.id.AmountRecived);
        Paymentmode = findViewById(R.id.Paymentmode);
        TotalTax = findViewById(R.id.TotalTax);
        print = findViewById(R.id.buttonProceed);

        // Get Client ID from Intent
        String clientId = getIntent().getStringExtra("ClntTxnReference");
        String TXNMSg = getIntent().getStringExtra("TxnMsg");
        String TXNTIME = getIntent().getStringExtra("TxnTimere");
        String RRN = getIntent().getStringExtra("RRN");
        String NAME = getIntent().getStringExtra("Name");
        String FNAME = getIntent().getStringExtra("FName");
        String EMAIL = getIntent().getStringExtra("EmailID");
        String MOBILE = getIntent().getStringExtra("Mobile");
        String RECEVED = getIntent().getStringExtra("ReceivedBy");
        String JALKALID = getIntent().getStringExtra("JalkalId");
        String ZONENAME = getIntent().getStringExtra("Zone_Name");
        String WARDNAME = getIntent().getStringExtra("Ward_Name");
        String MOHALLANAME = getIntent().getStringExtra("Mohlla_Name");
        String AMOUNTRECEVIED = getIntent().getStringExtra("AmountRecived");
        String PAYMENTMODE = getIntent().getStringExtra("Paymentmode");
        String TOTALTAX = getIntent().getStringExtra("TotalTax");




        clientid.setText(clientId);
        TxnMsg.setText(TXNMSg);
        TxnTime.setText(TXNTIME);
        rrn.setText(RRN);
        Name.setText(NAME);
        FName.setText(FNAME);
        EmailID.setText(EMAIL);
        Mobile.setText(MOBILE);
        ReceivedBy.setText(RECEVED);
        JalkalId.setText(JALKALID);
        Zone_Name.setText(ZONENAME);
        Ward_Name.setText(WARDNAME);
        Mohlla_Name.setText(MOHALLANAME);
        AmountRecived.setText(AMOUNTRECEVIED);
        Paymentmode.setText(PAYMENTMODE);
        TotalTax.setText(TOTALTAX);



        String finalClientId = clientId;
        String finalTxnmsg = TXNMSg;
        String finalTxnTime = TXNTIME;
        print.setOnClickListener(v -> createAndSavePdf(finalClientId,finalTxnmsg,finalTxnTime));



    }

    private void createAndSavePdf(String fileName, String finalTxnmsg, String finalTxnTime) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(600, 800, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setTextSize(18);
        paint.setFakeBoldText(true);

        int startX = 50, startY = 100, rowHeight = 40, columnWidth = 250;

        // Draw table header
        canvas.drawText("Field", startX, startY, paint);
        canvas.drawText("Value", startX + columnWidth, startY, paint);
        paint.setStrokeWidth(2);
        canvas.drawLine(startX, startY + 10, startX + columnWidth * 2, startY + 10, paint);

        // Reset text style
        paint.setFakeBoldText(false);
        paint.setTextSize(16);

        // Table data
        String[][] tableData = {
                {"Client ID", fileName},
                {"Transaction Type", TxnMsg.getText().toString()},
                {"Transaction Time", TxnTime.getText().toString()},
                {"RRN", rrn.getText().toString()},
                {"Name", Name.getText().toString()},
                {"Father's Name", FName.getText().toString()},
                {"Email ID", EmailID.getText().toString()},
                {"Mobile", Mobile.getText().toString()},
                {"Received By", ReceivedBy.getText().toString()},
                {"Jalkal ID", JalkalId.getText().toString()},
                {"Zone Name", Zone_Name.getText().toString()},
                {"Ward Name", Ward_Name.getText().toString()},
                {"Mohlla Name", Mohlla_Name.getText().toString()},
                {"Amount Received", AmountRecived.getText().toString()},
                {"Payment Mode", Paymentmode.getText().toString()},
                {"Total Tax", TotalTax.getText().toString()}
        };

        // Draw table rows
        for (String[] row : tableData) {
            startY += rowHeight;
            canvas.drawText(row[0], startX, startY, paint);
            canvas.drawText(row[1], startX + columnWidth, startY, paint);
        }

        document.finishPage(page);
        savePdfToDownloads(document, fileName);
        document.close();
    }

    private void savePdfToDownloads(PdfDocument document, String fileName) {
        ContentResolver contentResolver = getContentResolver();
        Uri pdfUri = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10+ (API 29+)
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName + ".pdf");
            values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            pdfUri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
        } else { // Android 9 and below
            String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName + ".pdf";
            pdfUri = Uri.parse(filePath);
        }

        if (pdfUri != null) {
            try (OutputStream outputStream = contentResolver.openOutputStream(pdfUri)) {
                document.writeTo(outputStream);
                Toast.makeText(this, "PDF Saved in Downloads!", Toast.LENGTH_SHORT).show();
                openPdfFile(pdfUri);
            } catch (IOException e) {
                Log.e("PDF", "Error saving PDF", e);
                Toast.makeText(this, "Error saving PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openPdfFile(Uri pdfUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Open PDF"));
    }
}