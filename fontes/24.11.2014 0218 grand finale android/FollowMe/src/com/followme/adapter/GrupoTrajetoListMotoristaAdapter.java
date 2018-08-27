package com.followme.adapter;

import java.util.List;

import com.android.volley.toolbox.NetworkImageView;

import com.followme.R;
import com.followme.R.id;
import com.followme.R.layout;
import com.followme.library.VolleySingleton;
import com.followme.model.MotoristaModel;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class GrupoTrajetoListMotoristaAdapter extends ArrayAdapter<MotoristaModel> {
	
	static final int LAYOUT = R.layout.grupo_trajeto_list_motorista_activity;
	
	public GrupoTrajetoListMotoristaAdapter(Context context, 
		    List<MotoristaModel> objects) {

		    super(context, LAYOUT, objects);
		  }
	
	@Override
	  public View getView(int position, 
	    View convertView, ViewGroup parent) {

	    Context ctx = parent.getContext();
	    if (convertView == null){
	      convertView = LayoutInflater.from(ctx)
	        .inflate(R.layout.grupo_trajeto_list_motorista_activity, null);
	    }
	    NetworkImageView img = (NetworkImageView)
	      convertView.findViewById(R.id.ivGrupoTrajetoListMotoristaActivity);
	    TextView txtEmail = (TextView)
	      convertView.findViewById(R.id.lblNomeLiderGrupoTrajetoListMotoristaActivity);
	    
	 
	  
	    MotoristaModel motoristaModel = getItem(position);
	    txtEmail.setText(motoristaModel.getEmail().toString());
	    //txtStatusBloqueio.setText(motoristaModel.getAutorizado().toString());
	   /* if(motoristaModel.getAutorizado().equals("bloqueado"))
	    {
	    	convertView.setBackgroundColor(Color.RED);
	    }else
	    {
	    	convertView.setBackgroundColor(Color.GREEN);
	    }*/
	    
	    String url=motoristaModel.getUrl();
	    img.setImageUrl(
	      motoristaModel.getUrl(), 
	      VolleySingleton.getInstance(
	        getContext()).getImageLoader());
	  
	    return convertView;
	  }

}
