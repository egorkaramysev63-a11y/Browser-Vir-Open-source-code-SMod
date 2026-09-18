package com.vir.brower;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class OfficeActivity extends Activity {

    private static final int CREATE_FILE_REQUEST = 301;
    private static final int OPEN_FILE_REQUEST = 302;

    private EditText etDocContent;
    private TextView tvCurrentFileStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView sv = new ScrollView(this);
        sv.setBackgroundColor(0xFFE5E5E5); // Серый Holo фон
        sv.setFillViewport(true);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);
        sv.addView(layout);
        setContentView(sv);

        TextView tvHeader = new TextView(this);
        tvHeader.setText("MS Office & Txt Редактор");
        tvHeader.setTextSize(18);
        tvHeader.setTextColor(0xFF333333);
        layout.addView(tvHeader);

        tvCurrentFileStatus = new TextView(this);
        tvCurrentFileStatus.setText("Файл не выбран");
        tvCurrentFileStatus.setTextColor(0xFF4285F4);
        tvCurrentFileStatus.setPadding(0, 10, 0, 20);
        layout.addView(tvCurrentFileStatus);

        etDocContent = new EditText(this);
        etDocContent.setHint("Введите текст или данные MS Office...");
        etDocContent.setGravity(android.view.Gravity.TOP);
        etDocContent.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        LinearLayout.LayoutParams contentLP = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 600);
        contentLP.setMargins(0, 10, 0, 30);
        etDocContent.setLayoutParams(contentLP);
        etDocContent.setBackgroundColor(Color.WHITE);
        etDocContent.setPadding(25, 25, 25, 25);
        etDocContent.setTextColor(Color.BLACK);
        layout.addView(etDocContent);

        LinearLayout actionsLayout = new LinearLayout(this);
        actionsLayout.setOrientation(LinearLayout.HORIZONTAL);
        actionsLayout.setWeightSum(2);

        Button btnOpen = new Button(this);
        btnOpen.setText("📁 Открыть");
        btnOpen.setBackgroundColor(0xFF2D2D2D);
        btnOpen.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams btnLP1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        btnLP1.setMargins(0, 0, 10, 0);
        btnOpen.setLayoutParams(btnLP1);
        btnOpen.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    startActivityForResult(intent, OPEN_FILE_REQUEST);
                }
            });
        actionsLayout.addView(btnOpen);

        Button btnSave = new Button(this);
        btnSave.setText("💾 Сохранить");
        btnSave.setBackgroundColor(0xFF4285F4);
        btnSave.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams btnLP2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        btnSave.setLayoutParams(btnLP2);
        btnSave.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TITLE, "Document.txt");
                    startActivityForResult(intent, CREATE_FILE_REQUEST);
                }
            });
        actionsLayout.addView(btnSave);
        layout.addView(actionsLayout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null || data.getData() == null) return;
        Uri fileUri = data.getData();

        if (requestCode == OPEN_FILE_REQUEST) {
            try {
                InputStream is = getContentResolver().openInputStream(fileUri);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder(); String line;
                while ((line = reader.readLine()) != null) sb.append(line).append("\n");
                reader.close(); is.close();
                etDocContent.setText(sb.toString());
                tvCurrentFileStatus.setText("📖 Файл: " + fileUri.getLastPathSegment());
            } catch (IOException e) {}
        } else if (requestCode == CREATE_FILE_REQUEST) {
            try {
                OutputStream os = getContentResolver().openOutputStream(fileUri);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
                writer.write(etDocContent.getText().toString());
                writer.flush(); writer.close(); os.close();
                tvCurrentFileStatus.setText("📝 Сохранено в: " + fileUri.getLastPathSegment());
            } catch (IOException e) {}
        }
    }
}

