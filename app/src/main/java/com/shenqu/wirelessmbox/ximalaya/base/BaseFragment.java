/**
 * BaseFragment.java
 * com.ximalaya.ting.android.opensdk.test
 * <p/>
 * <p/>
 * ver     date      		author
 * ---------------------------------------
 * 2015-6-4 		chadwii
 * <p/>
 * Copyright (c) 2015, chadwii All Rights Reserved.
 */

package com.shenqu.wirelessmbox.ximalaya.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

public class BaseFragment extends Fragment {
    protected Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    public void refresh() {
    }

    public View findViewById(int id) {
        return getView().findViewById(id);
    }
}

