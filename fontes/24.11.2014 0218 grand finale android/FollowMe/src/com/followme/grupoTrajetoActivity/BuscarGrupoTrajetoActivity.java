package com.followme.grupoTrajetoActivity;

/**
 * @Activity para pesquisar um grupo de trajeto que irá mostrar uma lista de trajetos de 
 * acordo com os parametros de pesquisa.
 * Aqui tem duas alternativas:
 * 1º caso o lider do grupo de trajeto é o mesmo do logado, aí abre a 
 * MainActivity>BuscarGrupoTrajetoActivity>GrupoTrajetoListBuscaActivity>MotoristaAutorizacaoActivity
 * 
 * 2º caso o lider do grupo de trajeto é diferente do logado, aí abre a
 * MainActivity>BuscarGrupoTrajetoActivity>GrupoTrajetoListBuscaActivity>GrupoTrajetoListJuntarseActivity
 * 
 * 
 */

import com.followme.R;
import com.followme.R.id;
import com.followme.R.layout;
import com.followme.R.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class BuscarGrupoTrajetoActivity extends Activity {
	
	private EditText txtBuscar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.busca_grupo_trajeto_activity);
		
		inicializaComponentes();

	

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.menu_buscar_grupo_trajeto, menu);
	    return true;
	    
	    
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		case R.id.menu_buscar_grupo_trajeto_procurar:
			
			
			Intent it = new Intent(BuscarGrupoTrajetoActivity.this,GrupoTrajetoListBuscaActivity.class);
			it.putExtra("nomeGrupoTrajeto", txtBuscar.getText().toString());
			startActivity(it);
		
			Log.e("Script", txtBuscar.getText().toString());
			
			break;
        case R.id.menu_buscar_grupo_trajeto_voltar:
			
			
			
			finish();
			
			break;
						
		default:
			break;
		}
		return true;
		
	}
	
	public void inicializaComponentes()
	{
		txtBuscar=(EditText)findViewById(R.id.txtBuscarGrupoActBuscarGrupoTrajeto);
		
	}


}
