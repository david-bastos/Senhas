package com.estg.davidbastos.senhas;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

public class ConfigsActivity extends Activity {

    private Config config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configs);
        config = new Config();
        java.io.File file = new java.io.File(getFilesDir().getAbsolutePath(), "configs");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            config.setNome(br.readLine());
            config.setTelemovel(br.readLine());
            config.setEmail(br.readLine());
            config.setPosicao(Integer.parseInt(br.readLine()));
            config.setPeriodo(Integer.parseInt(br.readLine()));
        } catch (FileNotFoundException e) {
            config.setNome("");
            config.setTelemovel("");
            config.setEmail("");
            config.setPosicao(0);
            config.setPeriodo(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setScreen();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_configs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setScreen() {

        EditText mEdit = (EditText) findViewById(R.id.CA1);
        mEdit.setText(config.getNome());

        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (tm.getLine1Number() != null) {
            mEdit = (EditText) findViewById(R.id.CA2);
            config.setTelemovel(tm.getLine1Number());
            mEdit.setText(tm.getLine1Number());
        } else {
            mEdit = (EditText) findViewById(R.id.CA2);
            mEdit.setText(config.getTelemovel());
        }

        mEdit = (EditText) findViewById(R.id.CA3);
        mEdit.setText(config.getEmail());

        Spinner spinner = (Spinner) findViewById(R.id.CA4);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.docente_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(config.getPosicao());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                config.setPosicao(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        spinner = (Spinner) findViewById(R.id.CA5);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.periodo_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(config.getPeriodo());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                config.setPeriodo(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
    }

    public void saveConfig(View view) {

        EditText mEdit = (EditText) findViewById(R.id.CA1);
        if (mEdit.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Tem que introduzir um nome", Toast.LENGTH_SHORT).show();
            return;
        } else {
            config.setNome(mEdit.getText().toString());
        }

        mEdit = (EditText) findViewById(R.id.CA2);
        if (mEdit.getText().toString().isEmpty() || !android.util.Patterns.PHONE.matcher(mEdit.getText()).matches()) {
            Toast.makeText(getApplicationContext(), "Tem que inserir um número de telemóvel", Toast.LENGTH_SHORT).show();
            return;
        } else {
            config.setTelemovel(mEdit.getText().toString());
        }

        mEdit = (EditText) findViewById(R.id.CA3);
        if (mEdit.getText().toString().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(mEdit.getText()).matches()) {
            Toast.makeText(getApplicationContext(), "Tem que inserir um email válido", Toast.LENGTH_SHORT).show();
            return;
        } else {
            config.setEmail(mEdit.getText().toString());
        }

        String filePrep = "";
        filePrep += config.getNome() + "\r\n";
        filePrep += config.getTelemovel() + "\r\n";
        filePrep += config.getEmail() + "\r\n";
        filePrep += config.getPosicao() + "\r\n";
        filePrep += config.getPeriodo() + "\r\n";

        java.io.File file = new java.io.File(getFilesDir().getAbsolutePath(), "configs");
        try {
            file.createNewFile();
            OutputStream fo = new FileOutputStream(file);
            byte[] fileBytes = filePrep.getBytes();
            fo.write(fileBytes);
            fo.flush();
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
