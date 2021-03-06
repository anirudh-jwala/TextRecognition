package com.example.android.textrecognition;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class MainActivity extends AppCompatActivity {

    Button openCamera;
    private static final int REQUEST_IMAGE_CAPTURE = 56;
    TextRecognizer recognizer;
    InputImage image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openCamera = findViewById(R.id.camera_button);

        openCamera.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null)
                startActivityIfNeeded(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");

            recognizeMyText(bitmap);
        }
    }

    private void recognizeMyText(Bitmap bitmap) {
        try {
            image = InputImage.fromBitmap(bitmap, 0);
            recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        recognizer.process(image)
                .addOnSuccessListener(text -> {
                    String resultText = text.getText();
                    if (resultText.isEmpty())
                        Toast.makeText(MainActivity.this, "NO TEXT RECOGNIZED", Toast.LENGTH_SHORT).show();
                    else {
                        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                        intent.putExtra(TextDetection.RESULT_TEXT, resultText);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());

    }
}