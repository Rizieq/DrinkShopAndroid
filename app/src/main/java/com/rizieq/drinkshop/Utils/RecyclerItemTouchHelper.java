package com.rizieq.drinkshop.Utils;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.rizieq.drinkshop.Adapter.CartAdapter;
import com.rizieq.drinkshop.Adapter.FavoriteAdapter;

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {


    RecyclerItemTouchHelperListener listener;
    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs,RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        if (listener != null)
            listener.onSwiped(viewHolder,direction,viewHolder.getAdapterPosition());
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

        if (viewHolder instanceof FavoriteAdapter.FavoriteViewHolder)
        {
            View foregroundView = ((FavoriteAdapter.FavoriteViewHolder)viewHolder).view_foreground;
            getDefaultUIUtil().clearView(foregroundView);
        } else   if (viewHolder instanceof CartAdapter.CartViewHolder)
        {
            View foregroundView = ((CartAdapter.CartViewHolder)viewHolder).view_foreground;
            getDefaultUIUtil().clearView(foregroundView);
        }

    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null)
        {
            if (viewHolder instanceof FavoriteAdapter.FavoriteViewHolder)
            {
                View foreground = ((FavoriteAdapter.FavoriteViewHolder)viewHolder).view_foreground;
                getDefaultUIUtil().clearView(foreground);
            } else if (viewHolder instanceof CartAdapter.CartViewHolder)
            {
                View foreground = ((CartAdapter.CartViewHolder)viewHolder).view_foreground;
                getDefaultUIUtil().clearView(foreground);
            }

        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (viewHolder instanceof FavoriteAdapter.FavoriteViewHolder)
        {
            View foreground = ((FavoriteAdapter.FavoriteViewHolder)viewHolder).view_foreground;
            getDefaultUIUtil().onDraw(c,recyclerView,foreground,dX,dY,actionState,isCurrentlyActive);
        } else if (viewHolder instanceof CartAdapter.CartViewHolder)
        {
            View foreground = ((CartAdapter.CartViewHolder)viewHolder).view_foreground;
            getDefaultUIUtil().onDraw(c,recyclerView,foreground,dX,dY,actionState,isCurrentlyActive);
        }
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (viewHolder instanceof FavoriteAdapter.FavoriteViewHolder)
        {
            View foreground = ((FavoriteAdapter.FavoriteViewHolder)viewHolder).view_foreground;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foreground, dX, dY, actionState, isCurrentlyActive);
        } else if (viewHolder instanceof CartAdapter.CartViewHolder)
        {
            View foreground = ((CartAdapter.CartViewHolder)viewHolder).view_foreground;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foreground, dX, dY, actionState, isCurrentlyActive);
        }

    }
}
