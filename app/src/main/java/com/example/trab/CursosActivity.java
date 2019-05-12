package com.example.trab;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CursosActivity extends AppCompatActivity {

    bd banco = new bd(this);
    bd.Curso novoCurso = new bd.Curso();

    EditText edtNomeCurso, edtHoras, edtCodigo;
    Button btnRegistrarCurso;
    Button btnLimpar;
    Button btnExcluir;
    ListView listViewCursos;

    ArrayAdapter<String> adapter;
    ArrayList<String> arrayList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cursos);

        edtNomeCurso = (EditText) findViewById(R.id.edtNomeCurso);
        edtHoras = (EditText) findViewById(R.id.edtHoras);
        edtCodigo = (EditText) findViewById(R.id.edtCodigo);
        btnRegistrarCurso = (Button) findViewById(R.id.btnRegistrarCurso);
        btnExcluir = (Button) findViewById(R.id.btnExcluirAluno);
        btnLimpar = (Button) findViewById(R.id.btnLimparAluno);
        listViewCursos = (ListView) findViewById(R.id.listViewCursos);


        ListarCursos();

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.lv_header,listViewCursos,false);
        listViewCursos.addHeaderView(header);




        listViewCursos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String conteudo = (String) listViewCursos.getItemAtPosition(position);

                String codigo = conteudo.substring(0, conteudo.indexOf("-"));

                bd.Curso curso = banco.selecionarCurso(Integer.parseInt(codigo));

                edtNomeCurso.setText(curso.nomeCurso);
                edtHoras.setText(String.valueOf(curso.qtdeHoras));
                edtCodigo.setText(String.valueOf(curso.codigo));
            }
        });

        btnRegistrarCurso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nomeCurso = edtNomeCurso.getText().toString();
                String qtdeHoras = edtHoras.getText().toString();
                String codigo = edtCodigo.getText().toString();


                if (nomeCurso.isEmpty()) {
                    edtNomeCurso.setError("Este campo é obrigatório");
                } else if (qtdeHoras.isEmpty()) {
                    edtHoras.setError("Este campo é obrigatório");
                } else if (codigo.isEmpty() && !nomeCurso.isEmpty() && !qtdeHoras.isEmpty()) {
                    novoCurso.nomeCurso = nomeCurso;
                    novoCurso.qtdeHoras = Integer.parseInt(qtdeHoras);

                    banco.addCurso(novoCurso);
                    Toast.makeText(CursosActivity.this, "Salvo com sucesso!", Toast.LENGTH_SHORT).show();

                    ListarCursos();
                    limpaCampos();

                }else {
                    novoCurso.nomeCurso = nomeCurso;
                    novoCurso.qtdeHoras = Integer.parseInt(qtdeHoras);
                    novoCurso.codigo = Integer.parseInt(codigo);
                    banco.AtualizaCurso(novoCurso);
                    Toast.makeText(CursosActivity.this, "Atualizado com sucesso!", Toast.LENGTH_SHORT).show();

                    ListarCursos();
                    limpaCampos();
                }


            }
        });

        btnLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpaCampos();
            }
        });


        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codigo = edtCodigo.getText().toString();
                String nomeCurso = edtNomeCurso.getText().toString();
                String qtdeHoras = edtHoras.getText().toString();

                if(codigo.isEmpty()){
                    Toast.makeText(CursosActivity.this, "Nenhum curso selecionado!", Toast.LENGTH_SHORT).show();
                }else{
                    bd.Curso curso = new bd.Curso();
                    curso.codigo = Integer.parseInt(codigo);
                    curso.nomeCurso = nomeCurso;
                    curso.qtdeHoras = Integer.parseInt(qtdeHoras);
                    banco.apagarCurso(curso);
                    Toast.makeText(CursosActivity.this, "Curso apagado com sucesso!", Toast.LENGTH_SHORT).show();

                    ListarCursos();
                    limpaCampos();

                }

            }
        });


    }

    void limpaCampos (){
        edtNomeCurso.setText("");
        edtHoras.setText("");
        edtCodigo.setText("");
        edtNomeCurso.requestFocus();
    }


  public void ListarCursos(){
        List<bd.Curso> cursos = banco.listaTodosCursos();

        arrayList = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(CursosActivity.this, android.R.layout.simple_list_item_1, arrayList);

        listViewCursos.setAdapter(adapter);


        for(bd.Curso c : cursos){
            Log.d("Lista", "\nID"+ c.codigo + "\nNOME:" + c.nomeCurso + "\nHoras" + c.qtdeHoras);
            arrayList.add(c.codigo + "- " + c.nomeCurso);
            adapter.notifyDataSetChanged();
        }

    }


}
