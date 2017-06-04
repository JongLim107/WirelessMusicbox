package com.shenqu.wirelessmbox.testNlearning;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.shenqu.wirelessmbox.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JongLim on 2017/6/4.
 */

public class ProductCartActivity extends Activity {
    List<String> mItems;
    RecyclerView mRecyclerView;
    ProductCartAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tl_pro_cart_edit);

        mItems = new ArrayList<>();
        for (int i = 0; i <10 ; i++){
            mItems.add("this it the " + i + " item.");
        }

        initWidget();
    }

    private void initWidget() {
        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);

        adapter = new ProductCartAdapter(mItems);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void onCHangeStyle(View view) {
        adapter.toggleViewType();
        adapter.notifyDataSetChanged();
    }
}
