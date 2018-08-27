package com.followme.utilidades;

import java.util.ArrayList;
import java.util.List;

import com.followme.model.GrupoTrajetoModel;





import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BD extends SQLiteOpenHelper {
	
	


	private SQLiteDatabase bd;
	private static final String NOME_BD="GrupoTrajetoBD";
	private static final int Version_BD=3;
	private static final String TABLE_GRUPO_TRAJETO = "grupo_trajeto";
	
	public BD (Context ctx)
	{
		super(ctx,NOME_BD,null,Version_BD);
		
	}
	
	/*public BD(Context context){
		BDCore auxBd = new BDCore(context);
		bd = auxBd.getWritableDatabase();
	}*/
	
	
	public void inserir(GrupoTrajetoModel grupoTrajetoModel){
		ContentValues valores = new ContentValues();
		valores.put("id_lider", grupoTrajetoModel.getIdLider());
		valores.put("nome_grupo_trajeto", grupoTrajetoModel.getNomeGrupoTrajeto());
		valores.put("local_encontro", grupoTrajetoModel.getLocalEncontro());
		valores.put("local_destino", grupoTrajetoModel.getLocalDestino());
		valores.put("data_saida", grupoTrajetoModel.getDataSaidaAndroid());
		valores.put("hora_saida",grupoTrajetoModel.getHoraSaida());
		
		
		//valores.put("data_saida",grupoTrajetoModel.getDataSaidaInt());
		
		long id=getWritableDatabase().insert(TABLE_GRUPO_TRAJETO, null, valores);
		
		
	}
	public List<GrupoTrajetoModel> buscar(){
		List<GrupoTrajetoModel> list = new ArrayList<GrupoTrajetoModel>();
		String[] colunas = new String[]{"_id", "id_lider", "nome_grupo_trajeto", "data_saida"};
		
		//Cursor cursor = bd.query("grupo_trajeto", colunas, null, null, null, null, "_id ASC");
		Cursor cursor = getWritableDatabase().query("grupo_trajeto", colunas, null, null, null, null, "_id ASC");
		
		if(cursor.getCount() > 0){
			cursor.moveToFirst();
			
			do{
				
				GrupoTrajetoModel grupoTrajetoModel = new GrupoTrajetoModel();
				grupoTrajetoModel.setId(cursor.getInt(0));
				grupoTrajetoModel.setIdLider(cursor.getInt(1));
				grupoTrajetoModel.setNomeGrupoTrajeto(cursor.getString(2));
				grupoTrajetoModel.setDataSaidaAndroid(cursor.getString(3));
				
				
				
				list.add(grupoTrajetoModel);
				
			}while(cursor.moveToNext());
		}
		
		return(list);
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table grupo_trajeto(_id integer primary key autoincrement, id_lider integer, nome_grupo_trajeto text, local_encontro text, local_destino text, data_saida text, hora_saida text);");
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	

	
	
	

	
	
	
}
