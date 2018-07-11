package masterluck.android.com.facturaapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Calendar;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.ScrollView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.LinearLayout;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AutoCompleteTextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.util.ArrayList;
import java.util.List;

public class TicketActivity extends Activity {

    private final String TAG = "myLog";
    private final int CM_DELETE_ID = 1;

    private static String FILE = "mnt/sdcard/invoice.pdf";

    private LinearLayout linearLayout;
    private Bitmap myBitmap;

    TextView tvDate, tvSum;
    EditText etPrice, etQuantity;
    AutoCompleteTextView acPayer, acName;
    ListView lvProducts;
    Spinner spType;

    ArrayAdapter<String>  spAdapter;
    ProductAdapter productAdapter;

    int DIALOG_DATE = 1;
    int mYear;
    int mMonth;
    int mDay;

    List<Product> mProducts;
    List<String> mTypes;
    List<String> mPayers;
    List<String> mNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        tvDate = findViewById(R.id.tvDate);
        tvSum = findViewById(R.id.tvSum);
        acName = findViewById(R.id.acName);
        etPrice = findViewById(R.id.etPrice);
        etQuantity = findViewById(R.id.etQuantity);
        acPayer = findViewById(R.id.acPayer);
        lvProducts = findViewById(R.id.lvProducts);
        spType = findViewById(R.id.spType);

        init();

        ArrayAdapter payerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mPayers);
        acPayer.setAdapter(payerAdapter);
        acPayer.setInputType(0);
        acPayer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    acPayer.showDropDown();
            }
        });

        productAdapter = new ProductAdapter(this, mProducts);
        lvProducts.setAdapter(productAdapter);
        registerForContextMenu(lvProducts);

        spAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mTypes);
        spType.setAdapter(spAdapter);

        //------------------------------------------------------------------------
    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, "Видалити");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getItemId() == CM_DELETE_ID){
            AdapterContextMenuInfo acmi = (AdapterContextMenuInfo)item.getMenuInfo();
            mProducts.remove(acmi.position);
            productAdapter.notifyDataSetChanged();
            setSum();
        }
        return super.onContextItemSelected(item);
    }

//Initialize
    private void init(){
        mProducts = new ArrayList<>();
        mPayers = new ArrayList<>();
        mPayers.add("ol");
        mPayers.add("aat");
        mPayers.add("ag");
        mPayers.add("aaty");
        mNames = new ArrayList<>();
        mTypes = new ArrayList<>();
        mTypes.add("шт.");
        mTypes.add("уп.");

        Calendar c = Calendar.getInstance();
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        tvDate.setText("" + mDay + "/" + (mMonth + 1) + "/" + mYear);
    }


//Set Sum price for all products on tvSum
    private void setSum(){
        int sum = 0;
        for (Product p : mProducts) {
            sum += p.getSum();
        }
        tvSum.setText(String.valueOf(sum));
    }

    public void onClickSettings(View v){
        startActivity(new Intent(TicketActivity.this, PrefActivity.class));
    }

//Add Product
    public void onAddClick(View v){
        String name = acName.getText().toString();
        String price = etPrice.getText().toString();
        String quantity = etQuantity.getText().toString();
        String type = mTypes.get(spType.getSelectedItemPosition());
        if (name.isEmpty()) {
            acName.setError("Введіть назву товару");
            return;
        }if (quantity.isEmpty()) {
            etQuantity.setError("Введіть кількість");
            return;
        }if (price.isEmpty()) {
            etPrice.setError("Введіть ціну");
            return;
        }
        Product p = new Product(name, type, Integer.parseInt(quantity), Float.parseFloat(price));
        mProducts.add(p);
        Log.d(TAG, "added " + p);

        productAdapter.notifyDataSetChanged();
        setSum();
        etQuantity.setText(""); acName.setText(""); etPrice.setText("");
    }

    public void form(View v){
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        ScrollView root = (ScrollView) inflater.inflate(R.layout.activity_ticket, null); //RelativeLayout is root view of my UI(xml) file.
        root.setDrawingCacheEnabled(true);
        Bitmap screen = getBitmapFromView(this.getWindow().findViewById(R.id.LLMain));

        Intent intent = new Intent(this, ViewActivity.class);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        screen.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        intent.putExtra("Bitmap", byteArray);
        startActivity(intent);
    }

    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

//Date picker
    public void onDateClick(View v) {
        showDialog(DIALOG_DATE);
    }

    protected Dialog onCreateDialog(int id){
        if (id == DIALOG_DATE) {
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, mYear, mMonth, mDay);
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    OnDateSetListener myCallBack = new OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            tvDate.setText("" + mDay + "/" + (mMonth + 1) + "/" + mYear);
        }
    };


}
