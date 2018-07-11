package masterluck.android.com.facturaapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.BaseAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends BaseAdapter {

    Context ctx;
    LayoutInflater lInflater;
    List<Product> mProducts;

    ProductAdapter(Context context, List<Product> products){
        ctx = context;
        mProducts = products;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mProducts.size();
    }

    @Override
    public Object getItem(int position) {
        return mProducts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(convertView == null)
            view = lInflater.inflate(R.layout.product, parent, false);
        Product p = mProducts.get(position);
        ((TextView) view.findViewById(R.id.tvpName)).setText(String.valueOf(position + 1) + "." + p.getName());
        ((TextView) view.findViewById(R.id.tvpQuantity)).setText(p.getQuantity() + p.getType());
        ((TextView) view.findViewById(R.id.tvpSum)).setText(String.valueOf(p.getSum()));
        return view;
    }
}
