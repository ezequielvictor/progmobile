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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AlunosActivity extends AppCompatActivity {
    bd banco = new bd(this);
    bd.Curso novoCurso = new bd.Curso();
    bd.Aluno novoAluno = new bd.Aluno();

    EditText edtCodigo, edtNome, edtEmail, edtTelefone, edtCurso;

    Button btnRegistrarAluno;
    Button btnLimpar;
    Button btnExcluir;
    ListView listViewCursos;
    ListView listViewAlunos;

    ArrayAdapter<String> adapter, adapter1;
    ArrayList<String> arrayList, arrayList1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alunos);

        edtCodigo = (EditText) findViewById(R.id.edtCodigo);
        edtNome = (EditText) findViewById(R.id.edtNome);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtTelefone = (EditText) findViewById(R.id.edtTelefone);
        edtCurso = (EditText) findViewById(R.id.edtCurso);

        btnRegistrarAluno = (Button) findViewById(R.id.btnSalvar);
        btnExcluir = (Button) findViewById(R.id.btnExcluirAluno);
        btnLimpar = (Button) findViewById(R.id.btnLimparAluno);

        listViewCursos = (ListView) findViewById(R.id.listViewCursos);
        listViewAlunos = (ListView) findViewById(R.id.listViewAlunos);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.lv_header_alunos,listViewAlunos,false);
        listViewAlunos.addHeaderView(header);

        ListarCursos();
        ListarAlunos();

        listViewCursos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String conteudo = (String) listViewCursos.getItemAtPosition(position);

                String codigo = conteudo.substring(0, conteudo.indexOf("-"));

                bd.Curso curso = banco.selecionarCurso(Integer.parseInt(codigo));

                novoCurso = curso;


                edtCurso.setText(novoCurso.codigo +"- " + novoCurso.nomeCurso);

                //edtHoras.setText(String.valueOf(curso.qtdeHoras));
                //edtCodigo.setText(String.valueOf(curso.codigo));
            }
        });

        listViewAlunos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String alunoSelected = (String) listViewCursos.getItemAtPosition(position);

                String codigo = alunoSelected.substring(0, alunoSelected.indexOf("-"));



                bd.Aluno aluno = banco.selecionarAluno(Integer.parseInt(codigo));

                //Toast.makeText(AlunosActivity.this, "Aluno selecionado" + aluno.nomeAluno + aluno.curso.nomeCurso, Toast.LENGTH_SHORT).show();

                edtCodigo.setText(String.valueOf(aluno.codigo));
                edtEmail.setText(aluno.emailAluno);
                edtNome.setText(aluno.nomeAluno);
                edtTelefone.setText(String.valueOf(aluno.telefoneAluno));
                edtCurso.setText(aluno.curso.codigo + "- "+ aluno.curso.nomeCurso);
            }
        });

        btnLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpaCampos();
            }
        });

        btnRegistrarAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nomeAluno = edtNome.getText().toString();
                String emailAluno = edtEmail.getText().toString();
                String codigoAluno = edtCodigo.getText().toString();
                String telefoneAluno = edtTelefone.getText().toString();
                String nomeCurso = edtCurso.getText().toString();


                if (nomeAluno.isEmpty()) {
                    edtNome.setError("Este campo é obrigatório");
                } else if (emailAluno.isEmpty()) {
                    edtEmail.setError("Este campo é obrigatório");
                } else if (telefoneAluno.isEmpty()){
                    edtEmail.setError("Este campo é obrigatório");
                } else if (nomeCurso.isEmpty()){
                    edtEmail.setError("Este campo é obrigatório");
                }

                else if (codigoAluno.isEmpty() && !nomeCurso.isEmpty() && !telefoneAluno.isEmpty() && !emailAluno.isEmpty() && !nomeAluno.isEmpty()) {
                    novoAluno.nomeAluno = nomeAluno;
                    novoAluno.curso = novoCurso;
                    novoAluno.emailAluno = emailAluno;
                    novoAluno.telefoneAluno = telefoneAluno;

                    banco.addAluno(novoAluno);
                    Toast.makeText(AlunosActivity.this, "Aluno cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                    limpaCampos();
                    ListarAlunos();

                }else {

                    novoAluno.nomeAluno = nomeAluno;
                    novoAluno.curso = novoCurso;
                    novoAluno.emailAluno = emailAluno;
                    novoAluno.telefoneAluno = telefoneAluno;
                    novoAluno.codigo = Integer.parseInt(codigoAluno);


                    banco.AtualizaAluno(novoAluno);
                    Toast.makeText(AlunosActivity.this, "Aluno atualizado com sucesso!", Toast.LENGTH_SHORT).show();

                    ListarAlunos();
                    limpaCampos();
                }


            }
        });

    }

    void limpaCampos (){
        edtCodigo.setText("");
        edtCurso.setText("");
        edtTelefone.setText("");
        edtEmail.setText("");
        edtNome.setText("");

        edtNome.requestFocus();
    }


    public void ListarCursos(){
        List<bd.Curso> cursos = banco.listaTodosCursos();

        arrayList = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(AlunosActivity.this, android.R.layout.simple_list_item_1, arrayList);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.lv_header,listViewCursos,false);
        listViewCursos.addHeaderView(header);

        listViewCursos.setAdapter(adapter);

        for(bd.Curso c : cursos){
            arrayList.add(c.codigo + "- " + c.nomeCurso);
            adapter.notifyDataSetChanged();
        }

    }
    public void ListarAlunos(){
        List<bd.Aluno> alunos = banco.listaTodosAlunos();

        arrayList1 = new ArrayList<String>();

        adapter1 = new ArrayAdapter<String>(AlunosActivity.this, android.R.layout.simple_list_item_1, arrayList1);

        listViewAlunos.setAdapter(adapter1);

        for(bd.Aluno a : alunos){
            arrayList1.add(a.codigo + "- " + a.nomeAluno + " | Curso: " + a.curso.nomeCurso);
            adapter1.notifyDataSetChanged();
        }

    }

}
