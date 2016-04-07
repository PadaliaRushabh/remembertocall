package com.rushabh.remembertocall.recyclerViewTouchHelper;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.rushabh.remembertocall.adapter.ContactAdapter;

/**
 * Created by rushabh on 24/12/15.
 */
public class SwipeHelper extends ItemTouchHelper.SimpleCallback {

    private ContactAdapter adapter;
    Paint delete = new Paint();


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

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            View itemView = viewHolder.itemView;
            if(dX > 0){
                delete.setARGB(255,239, 83, 80);
                c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                        (float) itemView.getBottom(), delete);
            }/*else{

                delete.setARGB(255, 0, 255, 0);
                c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                        (float) itemView.getRight(),  (float) itemView.getBottom() , delete);

            }*/
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }


    }
}
