package com.rizieq.drinkshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.rizieq.drinkshop.Model.Category;
import com.rizieq.drinkshop.Model.Store;
import com.rizieq.drinkshop.Retrofit.IDrinkShopAPI;
import com.rizieq.drinkshop.Utils.Common;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class BottomSheetStore extends BottomSheetDialogFragment {


    TextView txt_store, txt_distance;

    IDrinkShopAPI mService;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_store, container, false);

        txt_store = v.findViewById(R.id.txt_store);
        txt_distance = v.findViewById(R.id.txt_distance);

        mService = Common.getAPI();


        txt_store.setText(new StringBuilder("Store : ").append(Common.currentStore.getName()));
        txt_distance.setText(new StringBuilder("Distance : ").append(Common.currentStore.getDistance_in_km()).append(" km"));

        return v;
    }
}
