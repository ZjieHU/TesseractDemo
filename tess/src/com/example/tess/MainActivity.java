package com.example.tess;


import java.io.File;
import java.util.regex.Pattern;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private TextView textView;
	private ImageView picture;
	TessBaseAPI baseApi;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        
		Button btn = new Button(this);
        textView = new TextView(this) {
        	@Override
        	public void onDraw(Canvas canvas) {
        		Paint paint = new Paint();
        		paint.setStrokeWidth(5);
        		paint.setStyle(Style.STROKE);
        		paint.setColor(Color.BLUE);
        		canvas.drawRoundRect(new RectF(0,0,getWidth(),getHeight()), 2, 2, paint);
        		super.onDraw(canvas);
        	}
        };
        picture = new ImageView(this);
        
        textView.setLayoutParams(params);
        picture.setLayoutParams(params);
        
        File picturePath = new File(Environment.getExternalStorageDirectory() + "/image.jpg");
        if(picturePath.exists()) {
        	Toast.makeText(this, "照片成功载入...", Toast.LENGTH_LONG).show();
        	Bitmap bitmap = BitmapFactory.decodeFile(
        			Environment.getExternalStorageDirectory() + "/image.jpg"
        	);
        	picture.setImageBitmap(bitmap);
        }
        
        btn.setText("打开摄像头");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri imageUri = Uri.fromFile(new File(
                        Environment.getExternalStorageDirectory(),"image.jpg"
                ));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 2); //requestCode = 2
            }
        });

        linearLayout.addView(btn);
        linearLayout.addView(textView);
        linearLayout.addView(picture);
        setContentView(linearLayout);
	}

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 2) {

            final TessBaseAPI tessBaseAPI;
            String result = "此处显示结果...";
            try {
                tessBaseAPI = new TessBaseAPI();
                tessBaseAPI.init(Environment.getExternalStorageDirectory().toString(),
                        "eng");
                tessBaseAPI.setPageSegMode(TessBaseAPI.PSM_AUTO);
                tessBaseAPI.setImage(new File(Environment.getExternalStorageDirectory() + "/image.jpg"));
                result = tessBaseAPI.getUTF8Text();
               // Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                tessBaseAPI.end();
            }catch (Exception e) {
                Log.v("errorException",e.toString());
                Toast.makeText(this, "Faile", Toast.LENGTH_LONG).show();
            }
            String str = "";
            for(String s : result.split("")) {
            	if(s.matches("[A-Za-z0-9]+")) {
            		str += s;
            	}
            }
            	
            textView.setText(str);
            
            File picturePath = new File(Environment.getExternalStorageDirectory() + "/image.jpg");
            if(picturePath.exists()) {
            	Toast.makeText(this, "照片成功载入...", Toast.LENGTH_LONG).show();
            	Bitmap bitmap = BitmapFactory.decodeFile(
            			Environment.getExternalStorageDirectory() + "/image.jpg"
            	);
            	picture.setImageBitmap(bitmap);
            }
        }
    }
}
