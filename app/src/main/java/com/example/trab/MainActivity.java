package com.example.trab;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void telaCursos (View view){
        Intent intent = new Intent(this, CursosActivity.class);
        startActivity(intent);
    }

    public void telaAlunos (View view){
        Intent intent = new Intent(this, AlunosActivity.class);
        startActivity(intent);
    }
}
