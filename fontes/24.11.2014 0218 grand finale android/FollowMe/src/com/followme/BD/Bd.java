package com.followme.BD;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class Bd {
	
	// nome da tabela
	public static final String TABELA_MOTORISTA = "motorista";
	// campos da tabela
	public static final String ID_MOTORISTA = "id";
	public static final String NOME_MOTORISTA = "nome";   
	public static final String NASCIMENTO_MOTORISTA = "nascimento";  	
	public static final String EMAIL_MOTORISTA = "email";
	public static final String SENHA_MOTORISTA = "senha";
	public static final String LOGADO_MOTORISTA = "logado"; 
	
	private static final String MOTORISTA_CREATE_TABLE = "CREATE TABLE "
			+ TABELA_MOTORISTA + "  (" +
										ID_MOTORISTA + " INTEGER NOT NULL PRIMARY KEY," +
										NOME_MOTORISTA + " TEXT NOT NULL, " +
										NASCIMENTO_MOTORISTA + " TEXT NOT NULL,"+
										EMAIL_MOTORISTA + " TEXT NOT NULL,"+
										SENHA_MOTORISTA + " TEXT NOT NULL,"+
										LOGADO_MOTORISTA + " BOOLEAN NOT NULL"+
								  "  );";
	
	private static final String TAG = "Db";
	protected DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	 
	private static final String DB_NAME = "sigame";
	private static final int DATABASE_VERSION = 1;
	 
	private final Context mCtx;

	public Bd(Context ctx) {
		this.mCtx = ctx;
	}
 
	public Bd open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
 
	public void close() {
		mDbHelper.close();
                mDb.close();
	}	
	
	protected static class DatabaseHelper extends SQLiteOpenHelper {
		 
		
		@Override
		  public void onOpen(SQLiteDatabase db)
		  {
		    super.onOpen(db);
		    if (!db.isReadOnly())
		    {
		      db.execSQL("PRAGMA foreign_keys=ON;");
		    }
		  }
	 
	 
		DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DATABASE_VERSION);
		}
	 
		@Override
		public void onCreate(SQLiteDatabase db) {
	 
			db.execSQL(MOTORISTA_CREATE_TABLE);
			Log.w("DbAdapter","DB criado com sucesso!");
		}
	 
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Atualizando o banco de dados da versão " + oldVersion + " para " + newVersion);
			// Renomeia tabelas
			int validLast = newVersion - 1;
			if (oldVersion == validLast && validLast > 0)
			{
				db.execSQL("ALTER TABLE " + TABELA_MOTORISTA   + " RENAME TO " + TABELA_MOTORISTA   + "BK");
			}
			// elimina tabelas
			db.execSQL("DROP TABLE IF EXISTS " + TABELA_MOTORISTA);
			// cria novas tabelas
			onCreate(db);
			// Copia dados anteriores para tabelas novas SEMPRE ALTERAR QUANDO MUDAR VERSï¿½O
			if (oldVersion == validLast && validLast > 0)
			{
				db.execSQL("INSERT INTO "+ TABELA_MOTORISTA + " SELECT " +	ID_MOTORISTA + "," + 
																			NOME_MOTORISTA + "," +
																			NASCIMENTO_MOTORISTA + "," +
																			EMAIL_MOTORISTA + "," +
																			SENHA_MOTORISTA + "," +
																			LOGADO_MOTORISTA +
															" FROM " + TABELA_MOTORISTA +"BK");

			}			
			// Elimina tabelas provisï¿½rias utilizadas para manter os dados
			db.execSQL("DROP TABLE IF EXISTS " + TABELA_MOTORISTA    + "_BK");
		}
	}
    
}