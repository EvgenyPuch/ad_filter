package ru.j2kv.filter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.IOException;

import static ru.j2kv.filter.Util.DELTA_INDEX;
import static ru.j2kv.filter.Util.cleanValue;

public class EditActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_MEDIA = 99;
    ImageView ivPhoto;
    Uri photoUri;
    Bitmap originalPhoto;
    Bitmap filterPhoto;
    SeekBar sbValue;
    String currentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_MEDIA);
        } else {
        }


        ivPhoto = findViewById(R.id.iv_photo);
        sbValue = findViewById(R.id.sbValue);

        photoUri = getIntent().getParcelableExtra("photoUri");
        try {
            originalPhoto = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            filterPhoto = originalPhoto;
        } catch (IOException e) {
            e.printStackTrace();
        }
        final Button btnOriginal = findViewById(R.id.btn_original);
        final Button btnSepia = findViewById(R.id.btn_sepia);
        final Button btnBrightness = findViewById(R.id.btn_br);
        final Button btnContrast = findViewById(R.id.btn_contrast);
        final Button btnHue = findViewById(R.id.btn_hue);
        final Button btnSaturation = findViewById(R.id.btn_saturation);
        final FloatingActionButton fabShare = findViewById(R.id.fab_share);
        fabShare.setOnClickListener(v -> {
            String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), filterPhoto,"share_photo", null);
            Uri bitmapUri = Uri.parse(bitmapPath);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
            shareIntent.setType("image/jpeg");
            startActivity(Intent.createChooser(shareIntent, "Share"));
        });
        sbValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    setFilterValue(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        btnOriginal.setOnClickListener(v -> {
            filterPhoto = originalPhoto;
            ivPhoto.setImageBitmap(filterPhoto);
            sbValue.setVisibility(View.GONE);
        });
        btnSepia.setOnClickListener(v -> {
            currentFilter = "SEPIA";
            sbValue.setVisibility(View.GONE);
            setFilterValue(0);
        });
        btnBrightness.setOnClickListener(v -> {
            currentFilter = "BRIGHTNESS";
            sbValue.setProgress(0);
            sbValue.setVisibility(View.VISIBLE);
            setFilterValue(0);
        });
        btnSaturation.setOnClickListener(v -> {
            currentFilter = "SATURATION";
            sbValue.setProgress(0);
            sbValue.setVisibility(View.VISIBLE);
            setFilterValue(0);
        });
        btnContrast.setOnClickListener(v -> {
            currentFilter = "CONTRAST";
            sbValue.setProgress(0);
            sbValue.setVisibility(View.VISIBLE);
            setFilterValue(0);
        });
        btnHue.setOnClickListener(v -> {
            currentFilter = "HUE";
            sbValue.setProgress(0);
            sbValue.setVisibility(View.VISIBLE);
            setFilterValue(0);

        });
        ivPhoto.setImageBitmap(filterPhoto);
    }

    private void setFilterValue(int value) {
        Bitmap bitmap = Bitmap.createBitmap(filterPhoto.getWidth(), filterPhoto.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        switch (currentFilter){
            case "SEPIA": paint.setColorFilter(new ColorMatrixColorFilter(getSepia()));
                break;
            case "CONTRAST": paint.setColorFilter(new ColorMatrixColorFilter(setContrast(value)));
                break;
            case "SATURATION": paint.setColorFilter(new ColorMatrixColorFilter(setSaturation(value)));
                break;
            case "HUE": paint.setColorFilter(new ColorMatrixColorFilter(setHue(value)));
                break;
            case "BRIGHTNESS": paint.setColorFilter(new ColorMatrixColorFilter(setBrightness(value)));
                break;
        }
        canvas.drawBitmap(filterPhoto, 0, 0, paint);
        ivPhoto.setImageBitmap(filterPhoto);
    }

    private ColorMatrix getSepia(){
        ColorMatrix sepia = new ColorMatrix();
        sepia.setSaturation(0);
        ColorMatrix colorScale = new ColorMatrix();
        colorScale.setScale(1, 1, 0.8f, 1);
        sepia.postConcat(colorScale);
        return sepia;
    }
    public static ColorMatrix setHue(float value) {
        ColorMatrix cm = new ColorMatrix();
        value = cleanValue(value, 180f) / 180f * (float) Math.PI;
        if (value == 0) {
            return cm;
        }
        float cosVal = (float) Math.cos(value);
        float sinVal = (float) Math.sin(value);
        float lumR = 0.213f;
        float lumG = 0.715f;
        float lumB = 0.072f;
        float[] mat = new float[]
                {
                        lumR + cosVal * (1 - lumR) + sinVal * (-lumR), lumG + cosVal * (-lumG) + sinVal * (-lumG), lumB + cosVal * (-lumB) + sinVal * (1 - lumB), 0, 0,
                        lumR + cosVal * (-lumR) + sinVal * (0.143f), lumG + cosVal * (1 - lumG) + sinVal * (0.140f), lumB + cosVal * (-lumB) + sinVal * (-0.283f), 0, 0,
                        lumR + cosVal * (-lumR) + sinVal * (-(1 - lumR)), lumG + cosVal * (-lumG) + sinVal * (lumG), lumB + cosVal * (1 - lumB) + sinVal * (lumB), 0, 0,
                        0f, 0f, 0f, 1f, 0f,
                        0f, 0f, 0f, 0f, 1f};
        cm.postConcat(new ColorMatrix(mat));
        return cm;
    }

    public static ColorMatrix setSaturation( float value) {
        ColorMatrix cm = new ColorMatrix();
        value = cleanValue(value, 100);
        if (value == 0) {
            return cm;
        }
        float x = 1 + ((value > 0) ? 3 * value / 100 : value / 100);
        float lumR = 0.3086f;
        float lumG = 0.6094f;
        float lumB = 0.0820f;

        float[] mat = new float[]
                {
                        lumR * (1 - x) + x, lumG * (1 - x), lumB * (1 - x), 0, 0,
                        lumR * (1 - x), lumG * (1 - x) + x, lumB * (1 - x), 0, 0,
                        lumR * (1 - x), lumG * (1 - x), lumB * (1 - x) + x, 0, 0,
                        0, 0, 0, 1, 0,
                        0, 0, 0, 0, 1
                };
        cm.postConcat(new ColorMatrix(mat));
        return cm;
    }

    public ColorMatrix setContrast(int value) {
        ColorMatrix cm = new ColorMatrix();
        value = (int) cleanValue(value, 100);
        if (value == 0) {
            return cm;
        }
        float x;
        if (value < 0) {
            x = 127 + (float) value / 100 * 127;
        } else {
            x = value % 1;
            if (x == 0) {
                x = (float) DELTA_INDEX[value];
            } else {
                //x = DELTA_INDEX[(p_val<<0)]; // this is how the IDE does it.
                x = (float) DELTA_INDEX[(value << 0)] * (1 - x) + (float) DELTA_INDEX[(value << 0) + 1] * x; // use linear interpolation for more granularity.
            }
            x = x * 127 + 127;
        }

        float[] mat = new float[]
                {
                        x / 127, 0, 0, 0, 0.5f * (127 - x),
                        0, x / 127, 0, 0, 0.5f * (127 - x),
                        0, 0, x / 127, 0, 0.5f * (127 - x),
                        0, 0, 0, 1, 0,
                        0, 0, 0, 0, 1
                };
        cm.postConcat(new ColorMatrix(mat));
        return cm;
    }

    public ColorMatrix setBrightness(float value) {
        ColorMatrix cm = new ColorMatrix();
        value = cleanValue(value, 100);
        if (value == 0) {
            return cm;
        }

        float[] mat = new float[]
                {
                        1, 0, 0, 0, value,
                        0, 1, 0, 0, value,
                        0, 0, 1, 0, value,
                        0, 0, 0, 1, 0,
                        0, 0, 0, 0, 1
                };
        cm.postConcat(new ColorMatrix(mat));
        return cm;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_MEDIA:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                }
                break;

            default:
                break;
        }
    }
}
