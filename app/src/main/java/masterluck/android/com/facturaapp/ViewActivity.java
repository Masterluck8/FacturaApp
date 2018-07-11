package masterluck.android.com.facturaapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;


public class ViewActivity extends AppCompatActivity {


    ImageView ivB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        ivB = findViewById(R.id.ivBitmap);

        Intent intent = getIntent();
        byte[] byteArray = getIntent().getByteArrayExtra("Bitmap");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        ivB.setImageBitmap(bmp);
    }
}
