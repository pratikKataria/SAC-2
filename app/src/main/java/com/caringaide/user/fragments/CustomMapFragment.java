package com.caringaide.user.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.MapFragment;


public class CustomMapFragment extends MapFragment {

	private View mapView;
	private MapWrapperLayout mapWrapperLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mapView = super.onCreateView(inflater, container, savedInstanceState);
		mapWrapperLayout = new MapWrapperLayout(getActivity());
		mapWrapperLayout.addView(mapView);

		return mapWrapperLayout;
	}

	@Override
	public View getView() {
		return mapView;
	}

	public void setOnDragListener(MapWrapperLayout.OnDragListener onDragListener) {
	   mapWrapperLayout.setOnDragListener(onDragListener);
    }

	public void onDestroyView() {
		super.onDestroyView();
	}


}
