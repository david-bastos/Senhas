package com.estg.davidbastos.senhas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddSenhaActivity extends Activity {

    protected Senha nova;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_senha);
        setSpinners();
        nova = new Senha();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_senha, menu);
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

    private void setSpinners() {
        Spinner spinner = (Spinner) findViewById(R.id.periodo_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.periodo_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        nova.setPeriodo(getResources().getStringArray(R.array.periodo_array)[position]);
                        break;
                    case 1:
                        nova.setPeriodo(getResources().getStringArray(R.array.periodo_array)[position]);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        spinner = (Spinner) findViewById(R.id.refeicao_spinner);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.refeicao_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        nova.setRefeicao(getResources().getStringArray(R.array.refeicao_array)[position]);
                        break;
                    case 1:
                        nova.setRefeicao(getResources().getStringArray(R.array.refeicao_array)[position]);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        spinner = (Spinner) findViewById(R.id.docente_spinner);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.docente_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        nova.setDocente(true);
                        break;
                    case 1:
                        nova.setDocente(false);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        spinner = (Spinner) findViewById(R.id.cantina_spinner);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.cantina_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        nova.setCantina(getResources().getStringArray(R.array.cantina_array)[position]);
                        break;
                    case 1:
                        nova.setCantina(getResources().getStringArray(R.array.cantina_array)[position]);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        spinner = (Spinner) findViewById(R.id.preco_spinner);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.preco_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        nova.setPreco(getResources().getStringArray(R.array.preco_array)[position]);
                        break;
                    case 1:
                        nova.setPreco(getResources().getStringArray(R.array.preco_array)[position]);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
    }

    public void setDate(View view) {

        EditText mEdit   = (EditText)findViewById(R.id.cell);
        if(mEdit.getText().toString().isEmpty() || mEdit.getText().toString().length() < 9) {
            Toast.makeText(getApplicationContext(), "Tem que inserir um número de telemóvel", Toast.LENGTH_SHORT).show();
            return;
        }else {
            nova.setTelemovel(mEdit.getText().toString());
        }

        DatePicker datePicker = (DatePicker) findViewById(R.id.data);

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();

        String dateAux = day +"-" + month + "-"+year;

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        Date formatedDate = null;
        try {
            formatedDate = sdf.parse(dateAux);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        nova.setData(formatedDate);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("NovaSenhaRefeicao", nova.getRefeicao());
        resultIntent.putExtra("NovaSenhaCantina", nova.getCantina());
        resultIntent.putExtra("NovaSenhaData", nova.getData().getTime());
        resultIntent.putExtra("NovaSenhaDocente", nova.getDocente());
        resultIntent.putExtra("NovaSenhaPeriodo", nova.getPeriodo());
        resultIntent.putExtra("NovaSenhaPreco", nova.getPreco());
        resultIntent.putExtra("NovaSenhaTelemovel", nova.getTelemovel());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
