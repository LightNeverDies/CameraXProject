package com.example.joy;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Collage_Maker extends AppCompatActivity {

    Button image_loader, image_saver, information;
    TextView textInformation;
    ImageView newImage, imageView;
    RelativeLayout rl;
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private float oldDist = 1f;
    float[] lastEvent = null;
    ImageView[] imageViewАrr;
    float xDown = 0, yDown = 0;
    Bitmap bitmap;
    Canvas canvasResult;
    List<ImageView> imageViews = new ArrayList<>();

    RelativeLayout MainRelativeLayout;
    GestureDetector gestureDetector;
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;
    private float newRot = 0f;
    private float d = 0f;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_collage__maker);

        imageView = (ImageView) findViewById(R.id.Collage_Maker);
        Intent intent = getIntent();
        byte[] bytes = intent.getByteArrayExtra("Image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imageView.setImageBitmap(bitmap);

        rl = (RelativeLayout) findViewById(R.id.MainRelativeLayout);

        image_loader = (Button) findViewById(R.id.LoadGallery);
        image_saver = (Button) findViewById(R.id.SaveImage);
        information = (Button) findViewById(R.id.Information);

        MainRelativeLayout = (RelativeLayout) findViewById(R.id.MainRelativeLayout);

        textInformation = (TextView) findViewById(R.id.TextInformation);

        image_loader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageView.getVisibility() == v.GONE) {
                    imageView.setVisibility(v.VISIBLE);
                    textInformation.setVisibility(v.GONE);
                    LoadGallery();
                } else {
                    LoadGallery();
                }
            }
        });

        image_saver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveImageCollage();
            }
        });

        information.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                textInformation.setText(
                        "How to use Collage Option" + "\n" +
                                "Step 1: Click on \"Load Image\". \n" +
                                "Step 2: Choose one or multiple photos from your gallery.\n" +
                                "Step 3: Move your selected photos to their places in the collage.\n" +
                                "Step 4: Save your collage.");
                textInformation.setVisibility((textInformation.getVisibility() == v.GONE) ? v.VISIBLE : v.GONE);
                imageView.setVisibility((imageView.getVisibility() == v.VISIBLE) ? v.GONE : v.VISIBLE);
            }
        });

    }

    private void LoadGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {

            List<Bitmap> bitmaps = new ArrayList<>();
            imageViews = new ArrayList<>();

            ClipData clipData = data.getClipData();

            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();

                    try {

                        InputStream is = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        bitmaps.add(i, bitmap);

                        newImage = new ImageView(getApplicationContext());
                        Log.e("Images","How many of them" + imageViews.size());
                        newImage.setAdjustViewBounds(true);
                        newImage.setScaleType(ImageView.ScaleType.MATRIX);

                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT
                        );


                        newImage.setLayoutParams(lp);
                        newImage.setId(i + rl.getChildCount()); // => might be a problem BIG PROBLEM very soon

                        rl.addView(newImage);

                        imageViews.add(newImage);
                        newImage.setImageBitmap(bitmap);

                        newImage.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                // the user just put his finger down on the imageView
                                ImageView view = (ImageView) v;
                                view.setAdjustViewBounds(true);
                                view.setScaleType(ImageView.ScaleType.MATRIX);
                                int maskedAction = event.getActionMasked();
                                float scale;
                                switch (maskedAction) {

                                    case MotionEvent.ACTION_DOWN:
                                        matrix = new Matrix();
                                        savedMatrix.set(matrix);
                                        start.set(event.getX(), event.getY());
                                        mode = DRAG;
                                        break;

                                    // the user moved his finger
                                    case MotionEvent.ACTION_MOVE:
                                        if(mode == DRAG) {
                                            matrix.set(savedMatrix);
                                            matrix.postTranslate(event.getX() - start.x, event.getY()
                                                    - start.y);
                                        }
                                        else if(mode == ZOOM) {
                                                float newDist = spacing(event);
                                                if (event.getPointerCount() >= 2 & newDist > 5f) {
                                                    matrix.set(savedMatrix);
                                                    scale = newDist / oldDist;

                                                    matrix.postScale(scale, scale, mid.x, mid.y);
                                                }
                                            }
                                       if (event.getPointerCount() == 2 || event.getPointerCount() == 3) {
                                            newRot = rotation(event);
                                            float r = newRot - d;
                                            float[] values = new float[9];
                                            matrix.getValues(values);
                                            float tx = values[2];
                                            float ty = values[5];
                                            float sx = values[0];
                                            float xc = (view.getWidth() / 2) * sx;
                                            float yc = (view.getHeight() / 2) * sx;
                                            matrix.postRotate(r, tx + xc, ty + yc);
                                        }

                                        break;

                                    case MotionEvent.ACTION_POINTER_DOWN:
                                        oldDist = spacing(event);
                                        Log.e("SPACE","Spacing" + oldDist);
                                        if(event.getPointerCount() >= 2 & oldDist > 5f ) {
                                            savedMatrix.set(matrix);
                                            midPoint(mid, event);
                                            mode = ZOOM;
                                        }

                                        d= rotation(event);
                                        break;

                                    case MotionEvent.ACTION_UP:
                                    case MotionEvent.ACTION_POINTER_UP:
                                        mode = NONE;
                                        break;

                                }
                                newImage.invalidate();
                                view.setImageMatrix(matrix);
                                return true;
                            }
                        });

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Uri imageUri = data.getData();

                try {

                    InputStream is = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    bitmaps.add(bitmap);

                    newImage = new ImageView(getApplicationContext());
                    newImage.setAdjustViewBounds(true);
                    newImage.setScaleType(ImageView.ScaleType.MATRIX);

                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );

                    newImage.setLayoutParams(lp);

                    rl.addView(newImage);

                    newImage.setImageBitmap(bitmap);

                    newImage.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            // the user just put his finger down on the imageView
                            ImageView view = (ImageView) v;
                            int maskedAction = event.getActionMasked();
                            float scale;

                            switch (maskedAction) {
                                case MotionEvent.ACTION_DOWN:
                                    start.set(event.getX(), event.getY());
                                    matrix = new Matrix();
                                    savedMatrix.set(matrix);
                                    mode = DRAG;
                                    break;

                                // the user moved his finger
                                case MotionEvent.ACTION_MOVE:
                                    if(mode == DRAG) {
                                        matrix.set(savedMatrix);
                                        matrix.postTranslate(event.getX() - start.x, event.getY()
                                                - start.y);
                                    }
                                    else if(mode == ZOOM) {
                                        float newDist = spacing(event);
                                        if (event.getPointerCount() >= 2 & newDist > 5f) {
                                            matrix.set(savedMatrix);
                                            scale = newDist / oldDist;

                                            matrix.postScale(scale, scale, mid.x, mid.y);
                                        }
                                    }
                                    if (event.getPointerCount() == 2 || event.getPointerCount() == 3) {
                                        newRot = rotation(event);
                                        float r = newRot - d;
                                        float[] values = new float[9];
                                        matrix.getValues(values);
                                        float tx = values[2];
                                        float ty = values[5];
                                        float sx = values[0];
                                        float xc = (view.getWidth() / 2) * sx;
                                        float yc = (view.getHeight() / 2) * sx;
                                        matrix.postRotate(r, tx + xc, ty + yc);
                                    }

                                    break;

                                case MotionEvent.ACTION_POINTER_DOWN:
                                    oldDist = spacing(event);
                                    Log.e("SPACE","Spacing" + oldDist);
                                    if(event.getPointerCount() >= 2 & oldDist > 5f ) {
                                        savedMatrix.set(matrix);
                                        midPoint(mid, event);
                                        mode = ZOOM;
                                    }

                                    d= rotation(event);
                                    break;

                                case MotionEvent.ACTION_UP:
                                case MotionEvent.ACTION_POINTER_UP:
                                    mode = NONE;
                                    break;
                                case MotionEvent.ACTION_CANCEL:
                                    break;

                            }
                            view.setImageMatrix(matrix);
                            return true;
                        }
                    });

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);

        return (float) Math.toDegrees(radians);
    }
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }


    private void SaveImageCollage(){
        if(imageView.getDrawable() != null && newImage.getDrawable() !=null) {
            bitmap = Bitmap.createBitmap(MainRelativeLayout.getWidth(), MainRelativeLayout.getHeight(), Bitmap.Config.ARGB_8888);
            canvasResult = new Canvas(bitmap);

            if(imageViews.size() > 1) {
                Log.e("SS","S" + imageViews.size());
                for(int i = 0; i <imageViews.size(); i++) {
                    bitmap = Bitmap.createBitmap(imageViews.get(i).getWidth(),imageViews.get(i).getHeight(), Bitmap.Config.ARGB_8888);
                    canvasResult = new Canvas(bitmap);
                    newImage.draw(canvasResult);
                }
            }
            else{
                imageView.draw(canvasResult);
                newImage.draw(canvasResult);
            }

            File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Joy" + File.separator + "Collage");
            if (!imagesFolder.exists()) {
                imagesFolder.mkdirs();
            }
            File file = new File(imagesFolder.toString() + File.separator + System.currentTimeMillis() + ".png");
            String msg = "Pic captured at " + file.getAbsolutePath();
            Toast.makeText(Collage_Maker.this, msg, Toast.LENGTH_SHORT).show();

            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
            }
        }
    }

    private class DoubleTapGesture extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            newImage.setOnLongClickListener(new View.OnLongClickListener() {
                            int count = 0;
                            public boolean onLongClick(View v) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Collage_Maker.this, "Double tap the image and wait for 5 second to remove it.", Toast.LENGTH_SHORT).show();
                                        newImage.setImageDrawable(null);

                                    }
                                }, 5000);

                                return true;
                                //return false;
                            }

                        });
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }

    }



//
//                        newImage.setOnLongClickListener(new View.OnLongClickListener() {
//        @Override
//        public boolean onLongClick(View v) {
//            Toast.makeText(Collage_Maker.this, "Hold for 2 seconds to remove the image!", Toast.LENGTH_SHORT).show();
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Log.e("ImageID:", "ImageView ID:" + newImage.getId());
//                    newImage.setImageDrawable(null);
//                }
//            }, 2000);
//            return false;
//        }
//    });
}

