package com.followme.adapter;

import java.util.List;

import com.android.volley.toolbox.NetworkImageView;
import com.followme.R;
import com.followme.R.id;
import com.followme.R.layout;
import com.followme.model.GrupoTrajetoModel;
import com.followme.model.MotoristaModel;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;





public class GrupoTrajetoAdapter extends ArrayAdapter<GrupoTrajetoModel> {
	
	static final int LAYOUT = R.layout.grupo_trajeto_list_busca_activity;
	
	public GrupoTrajetoAdapter(Context context, 
		    List<GrupoTrajetoModel> objects) {

		    super(context, LAYOUT, objects);
		    Log.e("Script", "ok");
		  }
	
	@Override
	  public View getView(int position, 
	    View convertView, ViewGroup parent) {

	    Context ctx = parent.getContext();
	    if (convertView == null){
	      convertView = LayoutInflater.from(ctx)
	        .inflate(R.layout.grupo_trajeto_list_busca_activity, null);
	    }

	    TextView lblNomeGrupo= (TextView)
	      convertView.findViewById(R.id.lblNomeGrupoActBuscaGrupoRetornado);
	    
	   
	    GrupoTrajetoModel grupoTrajetoModel=getItem(position);
	    lblNomeGrupo.setText(grupoTrajetoModel.getNomeGrupoTrajeto());
	    
	    //TextView txtStatusBloqueio = (TextView)
	  	      //convertView.findViewById(R.id.txtStatusBloqueioActMotoristaAutorizacao);
	    
	  
	    //MotoristaModel motoristaModel = getItem(position);
	    //txtEmail.setText(motoristaModel.getEmail()+'\n'+(motoristaModel.getAutorizado().toString()));
	    //txtStatusBloqueio.setText(motoristaModel.getAutorizado().toString());
	   /* if(motoristaModel.getAutorizado().equals("bloqueado"))
	    {
	    	convertView.setBackgroundColor(Color.RED);
	    }else
	    {
	    	convertView.setBackgroundColor(Color.GREEN);
	    }*/
	    
	    
	    //img.setImageUrl(
	      //motoristaModel.getUrl(), 
	      //VolleySingleton.getInstance(
	        //getContext()).getImageLoader());
	  
	    return convertView;
	  }

	

}
