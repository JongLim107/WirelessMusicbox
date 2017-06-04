package com.shenqu.wirelessmbox.testNlearning;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shenqu.wirelessmbox.R;
import java.util.List;

/**
 * Created by JongLim on 2017/6/4.
 */

public class ProductCartAdapter extends RecyclerView.Adapter<ProductCartVHolder> {

    List<String> mStrings;
    int viewType = 0;

    public ProductCartAdapter(List<String> strings) {
        mStrings = strings;
    }

    @Override
    public ProductCartVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tl_item_mycart, parent, false);
        ProductCartVHolder holder = new ProductCartVHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ProductCartVHolder holder, int position) {
        if (viewType == holder.ListStyle){
            holder.proCbox.setVisibility(View.GONE);
            holder.proRemarks.setVisibility(View.VISIBLE);
            holder.llProQuantity.setVisibility(View.VISIBLE);
        }else {
            holder.proCbox.setVisibility(View.VISIBLE);
            holder.proRemarks.setVisibility(View.GONE);
            holder.llProQuantity.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mStrings.size();
    }

    public void toggleViewType() {
        viewType = ++viewType % 2;
    }
}
