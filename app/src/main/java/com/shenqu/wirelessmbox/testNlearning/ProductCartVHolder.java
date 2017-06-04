package com.shenqu.wirelessmbox.testNlearning;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shenqu.wirelessmbox.R;

/**
 * Created by JongLim on 2017/6/4.
 */

public class ProductCartVHolder extends RecyclerView.ViewHolder {

    CheckBox proCbox;

    ImageView proPicture;
    TextView proTitle;
    TextView proBrand;
    TextView proPrice;

    static final int EditStyle = 0;
    static final int ListStyle = 1;

    TextView proRemarks;

    LinearLayout llProQuantity;
    TextView proMinus;
    EditText proQuantity;
    TextView proPlus;

    public ProductCartVHolder(View itemView) {
        super(itemView);

        proCbox = (CheckBox) itemView.findViewById(R.id.proCbox);

        proPicture = (ImageView) itemView.findViewById(R.id.proPicture);
        proTitle = (TextView) itemView.findViewById(R.id.proTitle);
        proBrand = (TextView) itemView.findViewById(R.id.proBrand);
        proPrice = (TextView) itemView.findViewById(R.id.proPrice);

        proRemarks = (TextView) itemView.findViewById(R.id.proRemarks);
        llProQuantity = (LinearLayout) itemView.findViewById(R.id.llProQuantity);
        proMinus = (TextView) itemView.findViewById(R.id.proMinus);
        proQuantity = (EditText) itemView.findViewById(R.id.proQuantity);
        proPlus = (TextView) itemView.findViewById(R.id.proPlus);
    }

}
