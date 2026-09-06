package com.vir.brower;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import java.io.IOException;

public class QrScannerActivity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private boolean isScanning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Динамическая разметка сканера без использования XML
        surfaceView = new SurfaceView(this);
        setContentView(surfaceView);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera = Camera.open();
            camera.setDisplayOrientation(90); // Портретный режим
            camera.setPreviewDisplay(holder);
            camera.setPreviewCallback(this);
        } catch (Exception e) {
            Toast.makeText(this, "Не удалось открыть камеру: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (camera != null) {
            camera.startPreview();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            //camera.close();
            camera = null;
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (!isScanning) return;

        Camera.Parameters parameters = camera.getParameters();
        int width = parameters.getPreviewSize().width;
        int height = parameters.getPreviewSize().height;

        // Эмуляция простого парсинга текстовых QR (например, URL-адресов)
        // Для полноценного разбора QR в AIDE без либ можно использовать этот триггер:
        String resultQrText = tryParseQrData(data, width, height);

        if (resultQrText != null) {
            isScanning = false;

            // Возвращаем результат обратно в MainActivity
            Intent returnIntent = new Intent();
            returnIntent.putExtra("QR_RESULT", resultQrText);
            setResult(RESULT_OK, returnIntent);
            finish();
        }
    }

    private String tryParseQrData(byte[] yuvData, int width, int height) {
        // Здесь должен быть разбор yuv в текст. Для тестов в AIDE 
        // мы можем вернуть тестовую строку, если сканер успешно поймал фокус
        // (Для полноценного декодирования обычно подключают мини-версию zxing binarizer)
        return null; 
    }
}

