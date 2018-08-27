package com.followme.BD;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;

import com.followme.model.MotoristaModel;

public class MotoristaDA extends Bd{
	
	public MotoristaDA(Context ctx)
	{
		super(ctx);
	}
	
	// Getting
    public MotoristaModel getMotorista(int id) {
 	    SQLiteDatabase db = mDbHelper.getWritableDatabase();
 	    Cursor cursor = db.query(TABELA_MOTORISTA, null, ID_MOTORISTA + "=?", new String[] { String.valueOf(id) }, null, null, null);

	    try{
	 	    cursor.moveToFirst();
		    MotoristaModel motorista = new MotoristaModel(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4), cursor.getInt(5));
		    
		    db.close();
	        return motorista;
	        
	    }catch(CursorIndexOutOfBoundsException e){
	    	return null;
	    }
     }
    
    public MotoristaModel getMotorista() {
 	    SQLiteDatabase db = mDbHelper.getWritableDatabase();
 	    Cursor cursor = db.query(TABELA_MOTORISTA, null, LOGADO_MOTORISTA + "=?", new String[] { "1" }, null, null, null);

	    try{
	 	    cursor.moveToFirst();
		    MotoristaModel motorista = new MotoristaModel(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4), cursor.getInt(5));
		    
		    db.close();
	        return motorista;
	        
	    }catch(CursorIndexOutOfBoundsException e){
	    	return null;
	    }
     }
    
    public void gravaMotorista(MotoristaModel motorista) {
		// Verifica se descricao existe no cadastro
    	
 		MotoristaModel motoristaAux;
 		motoristaAux = getMotorista(motorista.getId()); 
		
		// processa dados
    	SQLiteDatabase db = mDbHelper.getWritableDatabase();
       	ContentValues values = new ContentValues();
       	
       	values.put(ID_MOTORISTA, motorista.getId());	
       	values.put(NOME_MOTORISTA, motorista.getNome());	
		values.put(NASCIMENTO_MOTORISTA, motorista.getNascimento());
		values.put(EMAIL_MOTORISTA, motorista.getEmail());
		values.put(SENHA_MOTORISTA, motorista.getSenha());
		values.put(LOGADO_MOTORISTA, motorista.getLogado());
		// Inserting Row
        if (motoristaAux == null)
        {
        	db.insert(TABELA_MOTORISTA, null, values);
        } else
        {
        	db.update(TABELA_MOTORISTA, values, ID_MOTORISTA + " = ?", new String[] { String.valueOf(motorista.getId()) });
        }
        motoristaAux = null;
    	db.close(); // Closing database connection
    }
    
    public void logoffMotorista(){
    	SQLiteDatabase db = mDbHelper.getWritableDatabase();
       	ContentValues values = new ContentValues();
       	
       	values.put(LOGADO_MOTORISTA, 0);
       	
       	db.update(TABELA_MOTORISTA, values, LOGADO_MOTORISTA + " = ?", new String[] { "1" });
    }

    // exclui motorista
    public void delMotorista(String id) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(TABELA_MOTORISTA, ID_MOTORISTA + " = ?",
                new String[] { id });
        db.close();
    }

}
