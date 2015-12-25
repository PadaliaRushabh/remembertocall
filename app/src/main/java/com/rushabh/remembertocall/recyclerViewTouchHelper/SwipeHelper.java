package com.rushabh.remembertocall.recyclerViewTouchHelper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.rushabh.remembertocall.adapter.ContactAdapter;

/**
 * Created by rushabh on 24/12/15.
 */
public class SwipeHelper extends ItemTouchHelper.SimpleCallback {

    private ContactAdapter adapter;

    public SwipeHelper(ContactAdapter adapter) {

        //super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        super(0, ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.removeContact(viewHolder.getAdapterPosition());
        //TODO:Show no contact if no contacts are left

    }
}
