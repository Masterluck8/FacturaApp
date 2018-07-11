package masterluck.android.com.facturaapp;

public class Product {
    private String mName;
    private String mType;
    private float mPrice;
    private int mQuantity;

    Product(String name, String type, int quantity, float price){
        mName = name;
        mQuantity = quantity;
        mPrice = price;
        mType = type;
    }

    @Override
    public String toString() {
        return mName + " " + mPrice + ": " + mQuantity + ", type = " + mType;
    }

    public float getSum(){
        return mPrice * mQuantity;
    }

    public String getName() {
        return mName;
    }

    public float getPrice() {
        return mPrice;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public String getType() {
        return mType;
    }

}
