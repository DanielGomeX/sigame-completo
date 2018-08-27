package com.followme.motoristaActivity;

import com.followme.R;
import com.followme.BD.MotoristaDA;
import com.followme.R.id;
import com.followme.R.layout;
import com.followme.model.MotoristaModel;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MotoristaActivity extends Activity{
	
	TextView id, nome, nascimento, email;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motorista);
        
        MotoristaDA bd = new MotoristaDA(getApplicationContext());
        bd.open();
        MotoristaModel motorista = bd.getMotorista();
        
        id = (TextView) findViewById(R.id.textViewMotId);
        id.setText(String.valueOf(motorista.getId()));
        
        nome = (TextView) findViewById(R.id.textViewMotNome);
        nome.setText(motorista.getNome());
        
        nascimento = (TextView) findViewById(R.id.textViewMotNasc);
        nascimento.setText(motorista.getNascimento());
        
        email = (TextView) findViewById(R.id.textViewMotEmail);
        email.setText(motorista.getEmail());
	}

}
