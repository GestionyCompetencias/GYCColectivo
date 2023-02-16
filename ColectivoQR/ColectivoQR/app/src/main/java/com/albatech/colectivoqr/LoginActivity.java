package com.albatech.colectivoqr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    EditText txtUser, txtPass;
    RequestQueue request;

    private SweetAlertDialog dialogCargando;
    private static final int PERMISO_CAMARA = 0;
    private static final int PERMISO_INTERNET = 0;
    private static final int PERMISO_LOCALIZAION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ComprobarPermisos();

        request= Volley.newRequestQueue(this);

        txtUser=findViewById(R.id.txtUser);
        txtPass=findViewById(R.id.txtPass);
        Button btnLogin = findViewById(R.id.btnLogin);
        //Define listener.
        btnLogin.setOnClickListener(v -> logearUsuario());

        ConexionSQLite conn = new ConexionSQLite(this,"dbSycSol",null,1);



    }

    private void ComprobarPermisos() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

        } else {
            // No se tiene el permiso, es necesario pedirlo al usuario

            PedirPermisoCamara();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED) {

        } else {
            // No se tiene el permiso, es necesario pedirlo al usuario

            PedirPermisoInternet();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

        } else {
            // No se tiene el permiso, es necesario pedirlo al usuario

            PedirPermisoLocalizacion();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISO_CAMARA) {
            /* Resultado de la solicitud para permiso de cámara
             Si la solicitud es cancelada por el usuario, el método .lenght sobre el array
             'grantResults' devolverá null.*/

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Permiso concedido, podemos iniciar camara


            } else {
                /* Permiso no concedido
                 Aquí habría que explicar al usuario el por qué de este permiso
                 y volver a solicitarlo .*/
//
            }
        }

    }


    private void PedirPermisoCamara() {
        //Comprobación 'Racional'
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {

            AlertDialog AD;
            AlertDialog.Builder ADBuilder = new AlertDialog.Builder(LoginActivity.this);
            ADBuilder.setMessage("Para poder funcionar, esta aplicacion requiere acceder a su camara para poder escanear los codigos QR. Permite que 'GycSol' pueda acceder al internet.");
            ADBuilder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Solicitamos permisos

                    ActivityCompat.requestPermissions(
                            LoginActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISO_CAMARA);
                }
            });

            AD = ADBuilder.create();
            AD.show();

        } else {
            ActivityCompat.requestPermissions(
                    LoginActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISO_CAMARA);
        }

    }

    private void PedirPermisoInternet() {
        //Comprobación 'Racional'
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.INTERNET)) {

            AlertDialog AD;
            AlertDialog.Builder ADBuilder = new AlertDialog.Builder(LoginActivity.this);
            ADBuilder.setMessage("Para iniciar sesion es necesaria la conexión a internet. Permite que 'GycSol' pueda acceder al internet.");
            ADBuilder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Solicitamos permisos

                    ActivityCompat.requestPermissions(
                            LoginActivity.this,
                            new String[]{Manifest.permission.INTERNET},
                            PERMISO_INTERNET);
                }
            });

            AD = ADBuilder.create();
            AD.show();

        } else {
            ActivityCompat.requestPermissions(
                    LoginActivity.this,
                    new String[]{Manifest.permission.INTERNET},
                    PERMISO_INTERNET);
        }

    }


    private void PedirPermisoLocalizacion() {
        //Comprobación 'Racional'
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {

            AlertDialog AD;
            AlertDialog.Builder ADBuilder = new AlertDialog.Builder(LoginActivity.this);
            ADBuilder.setMessage("Para poder funcionar, esta aplicacion requiere acceder a su GPS para poder enviar las coordenadas de marcacion. Permite que 'GycSol' pueda acceder al internet.");
            ADBuilder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Solicitamos permisos

                    ActivityCompat.requestPermissions(
                            LoginActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISO_LOCALIZAION);
                }
            });

            AD = ADBuilder.create();
            AD.show();

        } else {
            ActivityCompat.requestPermissions(
                    LoginActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISO_LOCALIZAION);
        }

    }




    private void logearUsuario(){

        dialogCargando = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        dialogCargando.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialogCargando.setTitleText("Loading ...");
        dialogCargando.setCancelable(true);
        dialogCargando.show();

        String url="https://apicolectivo.gycsol.cl/Access/Login";

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("Usuario",txtUser.getText().toString());
        hashMap.put("Contrasena",txtPass.getText().toString());
        hashMap.put("ID_DISPOSITIVO","1234");

        JsonObjectRequest peticion = new JsonObjectRequest(
                Request.Method.POST,
                url,
                new JSONObject(hashMap),
                response -> {
                    try {
                        JSONObject jsonObject = response.getJSONObject("oR");
                        int resultado = jsonObject.getInt("result");
                        String modo = jsonObject.getString("modo");
                        String mensj = jsonObject.getString("message");
                        String rut_empresa = jsonObject.getString("rut_empresa");
                        String nombre_empresa = jsonObject.getString("nombre_empresa");
                        String direccion_empresa = jsonObject.getString("direccion_empresa");
                        String logo_empresa = jsonObject.getString("logo_empresa");
                        String token = jsonObject.getString("token");
                        if(resultado == 1 && modo.contains("COLECTIVO")){

                            Bundle myBundle = new Bundle();
                            myBundle.putString("token","");
                            myBundle.putString("rut_empresa",rut_empresa);
                            myBundle.putString("nombre_empresa",nombre_empresa);
                            myBundle.putString("direccion_empresa",direccion_empresa);
                            myBundle.putString("logo_empresa",logo_empresa);
                            myBundle.putString("token",token);

                            Intent myIntent = new Intent(LoginActivity.this,MainActivity.class);
                            myIntent.putExtras(myBundle);
                            startActivity(myIntent);

                        }else{
                            mostrarDialogo(mensj);
                        }

                    } catch (JSONException e) {

                        mostrarDialogo("Se generó un problema en la conexión con el servidor por favor verifique su conexión a internet e intente nuevamente");
                    }
                },
                error -> {
                    String demos = "Errores ";

                    mostrarDialogo(error.getMessage());

                });

        request.add(peticion);
    }

    private void mostrarDialogo(String dato) {

        new AlertDialog.Builder(this)
                .setTitle("GycSol")
                .setMessage(dato)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();

    }

    @Override
    protected void onStop() {
        super.onStop();

        dialogCargando.dismiss();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();



    }
}