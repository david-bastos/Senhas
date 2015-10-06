package com.estg.davidbastos.senhas;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends Activity {

    private static Handler handler;
    private ListView listView;
    private ArrayList<Senha> senhas = new ArrayList<Senha>();
    private ArrayList<Senha> senhasAux = new ArrayList<Senha>();
    private MyCustomAdapter adapter;
    private static final String TAG = "SenhasLog";
    private static final int STATIC_INTEGER_VALUE = 999;
    private EditText inputSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    senhas = (ArrayList<Senha>) msg.obj;
                    senhasAux.addAll(senhas);
                    setList();
                }
                if (msg.what == 1) {
                    Toast.makeText(getApplicationContext(), "Senha Guardada", Toast.LENGTH_SHORT).show();
                }
                if (msg.what == 2) {
                    new DownloadFileAsync(MainActivity.this, MainActivity.this, handler).execute();
                    Toast.makeText(getApplicationContext(), "Ocorreu um erro ao guardar a senha", Toast.LENGTH_SHORT).show();
                }
                if (msg.what == 2) {
                    Toast.makeText(getApplicationContext(), "Ocorreu um erro ao fazer o download da lista de senhas.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        //popup();
        if (isNetworkConnected()) {
            if (!isInternetAvailable()) {
                final AccountManager acm = AccountManager.get(getApplicationContext());
                Account[] accounts = acm.getAccountsByType("com.google");
                boolean check = false;
                for (Account account : accounts) {
                    if (account.name.equals("senhas.estg@gmail.com")) {
                        new DownloadFileAsync(this, MainActivity.this, handler).execute();
                        setContentView(R.layout.activity_main);
                        listView = (ListView) findViewById(R.id.list);
                        check = true;
                    }
                }
                if (!check) {
                    AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
                    helpBuilder.setTitle("Adicionar conta Gmail");
                    helpBuilder.setMessage("Precisa de adicionar a conta gmail senhas.estg@gmail.com ao dispostivo. A Password é senhasestg11");

                    helpBuilder.setPositiveButton("Sim",
                            new DialogInterface.OnClickListener() {
                                //@Override
                                public void onClick(DialogInterface dialog, int which) {
                                    acm.addAccount("com.google", null, null, null, MainActivity.this,
                                            null, null);
                                }
                            });

                    helpBuilder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
                            System.exit(0);
                        }
                    });

                    AlertDialog helpDialog = helpBuilder.create();
                    helpDialog.show();

                }
            } else
                Toast.makeText(getApplicationContext(), "Erro na ligação à Internet.", Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(getApplicationContext(), "Erro na ligação à rede.", Toast.LENGTH_LONG).show();

        inputSearch = (EditText) findViewById(R.id.search);
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                filter(cs.toString());
                setList();
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_configs) {
            Intent intent = new Intent(getApplicationContext(), ConfigsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void addSenha(View view) {
        Log.v(TAG, "addSenha");
        Intent intent = new Intent(getApplicationContext(), AddSenhaActivity.class);
        startActivityForResult(intent, STATIC_INTEGER_VALUE);
    }

    protected void setList() {
        adapter = new MyCustomAdapter(this, android.R.layout.simple_list_item_1, senhas);
        listView.setAdapter(adapter);
    }

    private void popup() {
        final AccountManager acm = AccountManager.get(getApplicationContext());
        Account[] accounts = acm.getAccountsByType("com.google");
        for (Account account : accounts) {
            if (account.name.equals("senhas.estg@gmail.com"))
                return;
        }
        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle("Adicionar conta Gmail");
        helpBuilder.setMessage("Precisa de adicionar a conta gmail senhas.estg@gmail.com. A Password é senhasestg11");

        helpBuilder.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    //@Override
                    public void onClick(DialogInterface dialog, int which) {

                        acm.addAccount("com.google", null, null, null, MainActivity.this,
                                null, null);
                    }
                });

        helpBuilder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.finish();
                System.exit(0);
            }
        });

        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }

    private void uploadFile() {
        String xml = "";

        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
        xml += "<senhas xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"Senhas.xsd\">\r\n";
        for (Senha senha : senhas) {
            xml += "\t<senha>\r\n";
            xml += "\t\t<periodo>" + senha.getPeriodo() + "</periodo>\r\n";
            if (senha.getDocente())
                xml += "\t\t<docente>Sim</docente>\r\n";
            else
                xml += "\t\t<docente>Não</docente>\r\n";
            xml += "\t\t<cantina>" + senha.getCantina() + "</cantina>\r\n";
            xml += "\t\t<preco>" + senha.getPreco() + "</preco>\r\n";
            xml += "\t\t<refeicao>" + senha.getRefeicao() + "</refeicao>\r\n";
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String datetime = dateFormat.format(senha.getData());
            xml += "\t\t<data>" + datetime + "</data>\r\n";
            xml += "\t\t<telemovel>" + senha.getTelemovel() + "</telemovel>\r\n";
            xml += "\t</senha>\r\n";
        }
        xml += "</senhas>\r\n";
        //getFilesDir().getAbsolutePath()
        java.io.File file = new java.io.File(getFilesDir().getAbsolutePath(), "temp.xml");
        try {
            file.createNewFile();
            OutputStream fo = new FileOutputStream(file);
            byte[] xmlInBytes = xml.getBytes();
            fo.write(xmlInBytes);
            fo.flush();
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new UploadFileAsync(this, MainActivity.this, file, handler).execute();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (STATIC_INTEGER_VALUE): {
                if (resultCode == Activity.RESULT_OK) {
                    Senha nova = new Senha();
                    nova.setData(new Date(data.getLongExtra("NovaSenhaData", -1)));
                    nova.setPeriodo(data.getStringExtra("NovaSenhaPeriodo"));
                    nova.setCantina(data.getStringExtra("NovaSenhaCantina"));
                    nova.setPreco(data.getStringExtra("NovaSenhaPreco"));
                    nova.setDocente(data.getBooleanExtra("NovaSenhaDocente", false));
                    nova.setRefeicao(data.getStringExtra("NovaSenhaRefeicao"));
                    nova.setTelemovel(data.getStringExtra("NovaSenhaTelemovel"));
                    senhas.add(nova);
                    uploadFile();
                    setList();
                }
                break;
            }
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");

            if (ipAddr.equals("")) {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        senhas.clear();
        if (charText.length() == 0) {
            senhas.addAll(senhasAux);
        } else {
            for (Senha node : senhasAux) {
                if (node.getRefeicao().toLowerCase(Locale.getDefault()).contains(charText)) {
                    senhas.add(node);
                } else if (node.getPeriodo().toLowerCase(Locale.getDefault()).contains(charText)) {
                    senhas.add(node);
                } else if (node.getCantina().toLowerCase(Locale.getDefault()).contains(charText)) {
                    senhas.add(node);
                } else if (node.getPreco().toLowerCase(Locale.getDefault()).contains(charText)) {
                    senhas.add(node);
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String datetime = dateFormat.format(node.getData());
                    if (datetime.toLowerCase(Locale.getDefault()).contains(charText)) {
                        senhas.add(node);
                    } else if (node.getTelemovel().toLowerCase(Locale.getDefault()).contains(charText)) {
                        senhas.add(node);
                    } else {
                        String docente;
                        if (node.getDocente())
                            docente = "Sim";
                        else
                            docente = "Não";
                        if (docente.toLowerCase(Locale.getDefault()).contains(charText)) {
                            senhas.add(node);
                        }
                    }
                }
            }
        }
    }

    private class MyCustomAdapter extends ArrayAdapter<Senha> implements Filterable {

        private Context context;
        ArrayList<Senha> senhas = new ArrayList<Senha>();
        TextView refeicao;
        TextView periodo;
        TextView docente;
        TextView cantina;
        TextView preco;
        TextView data;
        TextView telemovel;

        public MyCustomAdapter(Context context, int viewResourceId, ArrayList<Senha> senhas) {
            super(context, viewResourceId, senhas);
            this.context = context;
            this.senhas.addAll(senhas);
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.senha, null);

                refeicao = (TextView) convertView.findViewById(R.id.textViewRefeicao);
                periodo = (TextView) convertView.findViewById(R.id.textViewPeriodo);
                docente = (TextView) convertView.findViewById(R.id.textViewDocente);
                cantina = (TextView) convertView.findViewById(R.id.textViewCantina);
                preco = (TextView) convertView.findViewById(R.id.textViewPreco);
                data = (TextView) convertView.findViewById(R.id.textViewData);
                telemovel = (TextView) convertView.findViewById(R.id.textViewTelemovel);
            }

            refeicao.setText(senhas.get(position).getRefeicao());
            periodo.setText(senhas.get(position).getPeriodo());
            if (senhas.get(position).getDocente())
                docente.setText("Sim");
            else
                docente.setText("Não");
            cantina.setText(senhas.get(position).getCantina());
            preco.setText(senhas.get(position).getPreco());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String datetime = dateFormat.format(senhas.get(position).getData());
            data.setText(datetime);
            telemovel.setText(senhas.get(position).getTelemovel());

            return convertView;
        }

        @Override
        public int getCount() {
            return senhas.size();
        }

    }

}
