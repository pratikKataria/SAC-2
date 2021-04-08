package com.caringaide.user.fragments;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * wrapper class for map fragment
 */
public class MapWrapperLayout extends FrameLayout{

	private OnDragListener onDragListener;
	public interface OnDragListener{
		 void onDrag(MotionEvent motionEvent);
	}

	public MapWrapperLayout(Context context) {
		super(context);
	}
	
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(onDragListener != null){
			onDragListener.onDrag(ev);
		}
		return super.dispatchTouchEvent(ev);
	}

	
	 public void setOnDragListener(OnDragListener mOnDragListener) {
	        this.onDragListener = mOnDragListener;
	 }
	
}
