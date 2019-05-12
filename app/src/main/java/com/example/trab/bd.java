package com.example.trab;

import android.app.admin.DeviceAdminInfo;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.nfc.Tag;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.database.Cursor;
import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

public class bd extends SQLiteOpenHelper{
    // Database Info
    private static final String DATABASE_NAME = "cursos.db";
    private static final int DATABASE_VERSION = 2;

    // Table Names
    private static final String TABLE_CURSOS = "cursos";
    private static final String TABLE_ALUNOS = "alunos";

    // Alunos Table Columns
    private static final String KEY_ALUNOS_ID = "alunoId";
    private static final String KEY_ALUNOS_CURSOS_ID_FK = "cursoId";
    private static final String KEY_ALUNOS_NOME = "nomeAluno";
    private static final String KEY_ALUNOS_EMAIL = "emailAluno";
    private static final String KEY_ALUNOS_TELEFONE = "telefoneAluno";

    // Curso Table Columns
    private static final String KEY_CURSOS_ID = "cursoId";
    private static final String KEY_CURSOS_NOME = "nomeCurso";
    private static final String KEY_CURSOS_HORAS = "qtdeHoras";

 public bd (Context context){
    // super(context, DATABASE_NAME, null, DATABASE_VERSION);
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
 }


    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            db.setForeignKeyConstraintsEnabled(true);
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABELA_CURSOS = "CREATE TABLE " + TABLE_CURSOS +
                "(" +
                KEY_CURSOS_ID + " INTEGER PRIMARY KEY," +
                KEY_CURSOS_NOME + " TEXT," +
                KEY_CURSOS_HORAS + " INTEGER" +
                ")";

        String CREATE_TABELA_ALUNOS = "CREATE TABLE " + TABLE_ALUNOS +
                "(" +
                KEY_ALUNOS_ID + " INTEGER PRIMARY KEY," +
                KEY_ALUNOS_CURSOS_ID_FK + " INTEGER REFERENCES " + TABLE_CURSOS + ", "+
                KEY_ALUNOS_NOME  + " TEXT," +
                KEY_ALUNOS_EMAIL + " TEXT," +
                KEY_ALUNOS_TELEFONE + " INTEGER" +
                ")";

        db.execSQL(CREATE_TABELA_CURSOS);
        db.execSQL(CREATE_TABELA_ALUNOS);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURSOS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALUNOS);
            onCreate(db);
        }
    }

    public static class Aluno{
        public Curso curso;
        public int codigo;
        public String nomeAluno;
        public String emailAluno;
        public String telefoneAluno;

    }

    public static class Curso{
        public int codigo;
        public String nomeCurso;
        public int qtdeHoras;
    }



    //Adicionando Curso
    public long addCurso (Curso curso){
        SQLiteDatabase db = getWritableDatabase();
        long cursoId = -1;

        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_CURSOS_NOME, curso.nomeCurso);
            values.put(KEY_CURSOS_HORAS, curso.qtdeHoras);

            int rows = db.update(TABLE_CURSOS, values, KEY_CURSOS_NOME + "= ?", new String[]{curso.nomeCurso});

            if (rows == 1){
                String usersSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                        KEY_CURSOS_ID, TABLE_CURSOS, KEY_CURSOS_NOME);

                Cursor cursor = db.rawQuery(usersSelectQuery, new String[]{String.valueOf(curso.nomeCurso)});

                try {
                    if (cursor.moveToFirst()) {
                        cursoId = cursor.getInt(0);
                        db.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            }else {
                cursoId = db.insertOrThrow(TABLE_CURSOS, null, values);
                db.setTransactionSuccessful();
            }

        }catch (Exception e) {
            Log.d("TAG", "Erro ao inserir o curso");
        } finally {
            db.endTransaction();
        }

        return cursoId;
    }

    public void addAluno (Aluno aluno) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {
            long cursoId = addCurso(aluno.curso);

            ContentValues values = new ContentValues();

            values.put(KEY_ALUNOS_CURSOS_ID_FK, cursoId);
            values.put(KEY_ALUNOS_NOME, aluno.nomeAluno);
            values.put(KEY_ALUNOS_EMAIL, aluno.emailAluno);
            values.put(KEY_ALUNOS_TELEFONE, aluno.telefoneAluno);

            db.insertOrThrow(TABLE_ALUNOS, null, values);
            db.setTransactionSuccessful();
        }catch (Exception e){
            Log.d("TAG", "Erro ao tentar adicionar um aluno no banco");
        }finally {
            db.endTransaction();
        }
    }

    public void apagarCurso (Curso curso){
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try{
            db.delete(TABLE_CURSOS, KEY_CURSOS_ID + " = ?", new String [] {String.valueOf(curso.codigo)});

        }catch (Exception e ){
            Log.d("TAG", "Erro ao deletar um curso");
        }finally {
            db.endTransaction();
        }
    }

    public Curso selecionarCurso(int codigo){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_CURSOS, new String[] {KEY_CURSOS_ID, KEY_CURSOS_NOME, KEY_CURSOS_HORAS}, KEY_CURSOS_ID + " = ?",
                                new String[] {String.valueOf(codigo)}, null, null, null);


        if(cursor != null){
            cursor.moveToFirst();
        }

        Curso curso = new Curso();
        curso.codigo = Integer.parseInt(cursor.getString(0));
        curso.nomeCurso = cursor.getString(1);
        curso.qtdeHoras = Integer.parseInt(cursor.getString(2));

        return curso;
    }

    public void AtualizaCurso(Curso curso){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues  values = new ContentValues();

        values.put(KEY_CURSOS_ID, curso.codigo);
        values.put(KEY_CURSOS_NOME, curso.nomeCurso);
        values.put(KEY_CURSOS_HORAS, curso.qtdeHoras);

        db.update(TABLE_CURSOS, values, KEY_CURSOS_ID + " = ?",
                new String[] {String.valueOf(curso.codigo)});

    }

    public List<Curso> listaTodosCursos() {
        List<Curso> listaCursos = new ArrayList<Curso>();

        String query = "SELECT * FROM " + TABLE_CURSOS;

        SQLiteDatabase db = getWritableDatabase();

        Cursor c = db.rawQuery(query, null);

        if(c.moveToFirst()){
            do{
                Curso curso = new Curso();
                curso.codigo = Integer.parseInt(c.getString(0));
                curso.nomeCurso = c.getString(1);
                curso.qtdeHoras = Integer.parseInt(c.getString(2));

                listaCursos.add(curso);

            }while(c.moveToNext());
        }

        return listaCursos;
    }

    public List<Aluno> listaTodosAlunos() {
        List<Aluno> listaAlunos = new ArrayList<Aluno>();


        //String ALUNOS_QUERY = "SELECT * FROM " + TABLE_ALUNOS;
        String ALUNOS_QUERY = String.format("SELECT * FROM %s LEFT OUTER JOIN %s ON %s.%s = %s.%s", TABLE_ALUNOS,
                TABLE_CURSOS,
                TABLE_ALUNOS,
                KEY_ALUNOS_CURSOS_ID_FK,
                TABLE_CURSOS,
                KEY_CURSOS_ID);



        SQLiteDatabase db = getWritableDatabase();

        Cursor c = db.rawQuery(ALUNOS_QUERY, null);


            if(c.moveToFirst()){
                do{
                    Curso curso = new Curso();
                    curso.codigo = Integer.parseInt(c.getString(c.getColumnIndex(KEY_CURSOS_ID)));
                    curso.nomeCurso = c.getString(c.getColumnIndex(KEY_CURSOS_NOME));
                    curso.qtdeHoras = Integer.parseInt(c.getString(c.getColumnIndex(KEY_CURSOS_HORAS)));

                    Aluno aluno = new Aluno();
                    aluno.curso = curso;
                    aluno.nomeAluno = c.getString(c.getColumnIndex(KEY_ALUNOS_NOME));
                    aluno.emailAluno = c.getString(c.getColumnIndex(KEY_ALUNOS_EMAIL));
                    aluno.telefoneAluno = c.getString(c.getColumnIndex(KEY_ALUNOS_TELEFONE));
                    aluno.codigo = Integer.parseInt(c.getString(c.getColumnIndex(KEY_ALUNOS_ID)));

                    listaAlunos.add(aluno);

                }while(c.moveToNext());
            }




        return listaAlunos;
    }

    public Aluno selecionarAluno(int codigo){
        SQLiteDatabase db = getReadableDatabase();

        String ALUNOS_QUERY = String.format("SELECT * FROM %s LEFT OUTER JOIN %s ON %s.%s = %s.%s WHERE %s = %s", TABLE_ALUNOS,
                TABLE_CURSOS,
                TABLE_ALUNOS,
                KEY_ALUNOS_CURSOS_ID_FK,
                TABLE_CURSOS,
                KEY_CURSOS_ID,
                KEY_ALUNOS_ID,
                String.valueOf(codigo));



        Cursor c = db.rawQuery(ALUNOS_QUERY, null);


        if(c != null){
            c.moveToFirst();
        }

        Curso curso = new Curso();
        curso.codigo = Integer.parseInt(c.getString(c.getColumnIndex(KEY_CURSOS_ID)));
        curso.nomeCurso = c.getString(c.getColumnIndex(KEY_CURSOS_NOME));
        curso.qtdeHoras = Integer.parseInt(c.getString(c.getColumnIndex(KEY_CURSOS_HORAS)));

        Aluno aluno = new Aluno();
        aluno.curso = curso;
        aluno.nomeAluno = c.getString(c.getColumnIndex(KEY_ALUNOS_NOME));
        aluno.emailAluno = c.getString(c.getColumnIndex(KEY_ALUNOS_EMAIL));
        aluno.telefoneAluno = c.getString(c.getColumnIndex(KEY_ALUNOS_TELEFONE));
        aluno.codigo = Integer.parseInt(c.getString(c.getColumnIndex(KEY_ALUNOS_ID)));

        return aluno;
    }

    public void AtualizaAluno(Aluno aluno){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues  values = new ContentValues();

        values.put(KEY_ALUNOS_ID, aluno.codigo);
        values.put(KEY_ALUNOS_EMAIL, aluno.emailAluno);
        values.put(KEY_ALUNOS_NOME, aluno.nomeAluno);
        values.put(KEY_ALUNOS_TELEFONE, aluno.telefoneAluno);
        values.put(KEY_ALUNOS_CURSOS_ID_FK, aluno.curso.codigo);

        db.update(TABLE_ALUNOS, values, KEY_ALUNOS_ID + " = ?",
                new String[] {String.valueOf(aluno.codigo)});

    }



}
