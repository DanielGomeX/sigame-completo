package com.followme.library;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.Marker;

@SuppressWarnings("serial")
public class MarkerList extends ArrayList<Marker> {
	
	private List<Integer> ids;

	public MarkerList() {
		super();
		ids = new ArrayList<Integer>();
	}
	
	@Override
	public void add(int id, Marker marker){
		ids.add(id);
		int index = ids.indexOf(id);
		super.add(index,marker);
	}
	
	@Override
	public Marker get(int id){
		return super.get(ids.indexOf(id));
	}
	
	public boolean contains(int id){
		if(ids.contains(id)){
			return true;
		}
		return false;
	}

}
