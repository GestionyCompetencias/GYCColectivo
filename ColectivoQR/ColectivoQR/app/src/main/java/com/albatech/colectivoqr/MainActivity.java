package com.albatech.colectivoqr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.albatech.colectivoqr.utilidades.Utilidades;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class MainActivity extends AppCompatActivity {

    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;
    private String EscribirEn;

    EditText txtUser;
    EditText txtPass;
    String rutPerson;
    Button btnSalida;

    String token;
    String MARCACION;
    String LATITUD;
    String LONGITUD;
    String HASH;
    String TIPO_VERIFICACION;
    String rutEmpresa;
    String respuestaMarcacionServer;
    String mensajeRespuestaServer;
    Boolean elTrabajadorEsDeLaEmpresa;

    RequestQueue request;
    private FusedLocationProviderClient fusedLocationClient;
    private SweetAlertDialog dialogCargando;

    //Cadena de coxion a la BD
    ConexionSQLite conn = new ConexionSQLite(this,"dbSycSol",null,1);

    private final BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                return;
            }

            lastText = result.getText();
            barcodeView.setStatusText("");

            beepManager.playBeepSoundAndVibrate();

            //Added preview of scanned barcode
            ImageView imageView = findViewById(R.id.barcodePreview);
            imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));


            if(lastText.length()>80){
                //extraemos el RUT de la cadena del QR
                rutPerson = lastText.substring(52, 62).replace("&","").replace("t","");
                rutPerson = rutPerson.replace("-","");
                TIPO_VERIFICACION="COLECTIVO";
                //Obtenemos la fecha y la hora del dispositivo
                momentoEvento();

                //Motramos un cuadro de dialogo para seleccionar ENTRADA o SALIDA
                //mostrarDialogo();
                //mostrarDialogoSweet();

                mostrarDialogoEntradaSalida();

            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "Codigo Invalido", Toast.LENGTH_LONG);
                toast.show();
            }

        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    private void mostrarDialogoSweetSuccess(){

        // 1. Success message
        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Marcación Exitosa")
                .setContentText("Bienvenido!")
                .setConfirmText("OK")
                .show();

        HASH="";

    }

    private void mostrarDialogoSweetError(String motivo){

        // 1. Success message
        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Marcación Fallida")
                .setContentText(motivo)
                .setConfirmText("OK")
                .show();

        HASH="";

    }

    private void mostrarDialogoCarga(){
        dialogCargando = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        dialogCargando.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialogCargando.setTitleText("Loading ...");
        dialogCargando.setCancelable(true);
        dialogCargando.show();
    }

    private Dialog alertaEntradaSalida;
    private void mostrarDialogoEntradaSalida() {

        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.dialog_marca,
                        null);
        final AlertDialog dialogoEntSal = new AlertDialog.Builder(this)
                .setView(customLayout)
                .show();

        alertaEntradaSalida = dialogoEntSal;

    }

    private Dialog confirmaSalida;
    private void mostrarDialogoExit() {

        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.dialog_salir,
                        null);
        final AlertDialog dialogoEntSal = new AlertDialog.Builder(this)
                .setView(customLayout)
                .show();

        confirmaSalida = dialogoEntSal;

    }

    private  void confirmarSalir(){

        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Cerrar Aplicación?")
                .setContentText("Está seguro que desea salir de la apicación!")
                .setConfirmText("Salir!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        logearUsuario();
                        sDialog.dismissWithAnimation();
                    }
                })
                .setCancelButton("Cancelar", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();

    }

    private void logearUsuario(){

        dialogCargando = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
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

                            dialogCargando.dismiss();
                            finish();

                        }else{
                            txtUser.setText("");
                            txtPass.setText("");
                            Toast toast = Toast.makeText(getApplicationContext(), "No autorizado para cerrar la APP", Toast.LENGTH_LONG);
                            toast.show();
                            dialogCargando.dismiss();
                        }

                    } catch (JSONException e) {
                        txtUser.setText("");
                        txtPass.setText("");
                        Toast toast = Toast.makeText(getApplicationContext(), "Se generó un problema en la conexión con el servidor por favor verifique su conexión a internet e intente nuevamente", Toast.LENGTH_LONG);
                        toast.show();
                        dialogCargando.dismiss();
                    }
                },
                error -> {
                    txtUser.setText("");
                    txtPass.setText("");
                    Toast toast = Toast.makeText(getApplicationContext(), "Se generó un problema en la conexión con el servidor", Toast.LENGTH_LONG);
                    toast.show();
                    dialogCargando.dismiss();
                });

        request.add(peticion);
    }





    private void enviarMarcaServidor(String marca, String tipoMarca, String lat, String lon, String nDoc, String hash) {

        alertaEntradaSalida.dismiss();


        String trab = String.valueOf(idTrab(nDoc));

        String url="https://apicolectivo.gycsol.cl/Trabajadores/RegistraMarcacion";

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("token", token);
        hashMap.put("ID_DISPOSITIVO","1234");
        hashMap.put("MARCACION",marca);
        hashMap.put("TIPO_MARCA",tipoMarca);
        hashMap.put("LATITUD",lat);
        hashMap.put("LONGITUD",lon);
        hashMap.put("TIPO_VERIFICACION",TIPO_VERIFICACION);
        hashMap.put("TRABAJADOR",trab);
        hashMap.put("N_DOCUMENTO",nDoc);
        hashMap.put("HASH",hash);

        JsonObjectRequest envioMarca = new JsonObjectRequest(
                Request.Method.POST,
                url,
                new JSONObject(hashMap),response -> {

            try {

                JSONObject jsonObject = response.getJSONObject("respuesta");
                int resultado = jsonObject.getInt("result");

                if( resultado == 1 ){
                    String message=jsonObject.getString("data2");
                    mensajeRespuestaServer=message;
                    respuestaMarcacionServer = String.valueOf(resultado);
                    mostrarDialogoSweetSuccess();
                }else{
                    String message=jsonObject.getString("message");
                    mensajeRespuestaServer=message;
                    respuestaMarcacionServer = String.valueOf(resultado);
                    mostrarDialogoSweetError(message);
                }

            } catch (JSONException e) {
                respuestaMarcacionServer="911";
                mensajeRespuestaServer="Se generó un problema en la conexión con el servidor por favor verifique su conexión a internet e intente nuevamente";

                String message="Se generó un problema en la conexión con el servidor por favor verifique su conexión a internet e intente nuevamente";
                mensajeRespuestaServer=message;
                respuestaMarcacionServer = String.valueOf(message);
                mostrarDialogoSweetError(message);

            }
        },
                error -> {
                    respuestaMarcacionServer="912";
                    String demos = error.getLocalizedMessage();
                    mostrarDialogoSweetError("912"+demos);
                }

        );

        request.add(envioMarca);
        dialogCargando.dismiss();

    }





    protected void momentoEvento(){

        TimeZone myTimeZone = TimeZone.getTimeZone("America/Santiago");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(myTimeZone);
        String momentoDate = simpleDateFormat.format(new Date());
        MARCACION = momentoDate;

    }

    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    public void pause(View view) {
        barcodeView.pause();
    }

    public void resume(View view) {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtUser= findViewById(R.id.txtUser);
        txtPass= findViewById(R.id.txtPass);
        txtUser.setText("");
        txtPass.setText("");


        FloatingActionButton fab = findViewById(R.id.BtnSalida);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmarSalir();
            }
        });


        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();

        request= Volley.newRequestQueue(this);

        Bundle myBundle=this.getIntent().getExtras();
        if (myBundle!=null){
            token=myBundle.getString("token");
            rutEmpresa=myBundle.getString("rut_empresa");

            getTrabajadores(token,rutEmpresa);

        //Obtener coordenadas GPS

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                LATITUD = String.valueOf(location.getLatitude());
                                LONGITUD = String.valueOf(location.getLongitude());

                            }
                        }
                    });

        }


        barcodeView = findViewById(R.id.barcode_scanner);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(getIntent());
        barcodeView.decodeContinuous(callback);

        beepManager = new BeepManager(this);

        txtUser.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    txtUser.setInputType(InputType.TYPE_NULL);
                    txtUser.setText("");
                    EscribirEn="1";
                }
            }
        });
        txtPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    txtPass.setInputType(InputType.TYPE_NULL);
                    txtPass.setText("");
                    EscribirEn="2";
                }
            }
        });
    }


    private  void getTrabajadores( String token, String rutEmpresa){

        String url="https://apicolectivo.gycsol.cl/Trabajadores/GetTrabajadores";

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("token", token);
        hashMap.put("empresa", rutEmpresa);
        hashMap.put("ID_DISPOSITIVO","1234");

        JsonObjectRequest peticion = new JsonObjectRequest(
            Request.Method.POST,
            url,
            new JSONObject(hashMap),response -> {
            try {

                JSONObject jsonObject = response.getJSONObject("oR");
                int resultado = jsonObject.getInt("result");
                String message = jsonObject.getString("message");
                JSONArray data = jsonObject.getJSONArray("data");

                if( resultado == 1 ){

                    Integer UserId = 0;
                    String RutEmpresaTrabajador="";
                    String trabajadores="";
                    String RutTrab;
                    if(data.length()>0){

                        //Abrimos la BD para poder escribir en ella
                        SQLiteDatabase db = conn.getWritableDatabase();

                        db.execSQL("DELETE FROM trabajadores");


                        for(int i=0; i< data.length();i++){

                            JSONObject trabajador = data.getJSONObject(i);
                            UserId = trabajador.getInt("USER_ID");
                            RutEmpresaTrabajador= trabajador.getString("RutEmpresa");
                            RutTrab = trabajador.getString("RUT");

                            if(RutEmpresaTrabajador.contains(rutEmpresa)){
                                registroTrabajador(UserId,RutTrab);
                            }

                        }

                    }else{
                        mostrarDialogoSweetError(message);
                    }

                }else{

                    mostrarDialogoSweetError(message);

                }

            } catch (JSONException e) {
                mostrarDialogoSweetError("Se generó un problema en la conexión con el servidor por favor verifique su conexión a internet e intente nuevamente");
            }
        },
                error -> {
                    String demos = "Errores ";
                    mostrarDialogoSweetError("Error inesperado");

                }

        );

        request.add(peticion);
    }


    private void registroTrabajador(Integer idTrab, String rutTrab) {

        //Cadena de coxion a la BD
        ConexionSQLite conn = new ConexionSQLite(this,"dbSycSol",null,1);

        //Abrimos la BD para poder escribir en ella
        SQLiteDatabase db = conn.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Utilidades.CAMPO_USER_ID,idTrab);
        values.put(Utilidades.CAMPO_RUT,rutTrab);

        Long idResul = db.insert(Utilidades.TABLA_TABAJADORES,Utilidades.CAMPO_USER_ID,values);

    }



    /** Called when the user touches the button */
    public void Boton1(View view) {
        if (EscribirEn=="1"){
            txtUser.setText(txtUser.getText() + "1");
        }else if(EscribirEn=="2"){
            txtPass.setText(txtPass.getText() + "1");
        }
    }

    public void Boton2(View view) {
        if (EscribirEn=="1"){
            txtUser.setText(txtUser.getText() + "2");
        }else if(EscribirEn=="2"){
            txtPass.setText(txtPass.getText() + "2");
        }
    }

    public void Boton3(View view) {
        if (EscribirEn=="1"){
            txtUser.setText(txtUser.getText() + "3");
        }else if(EscribirEn=="2"){
            txtPass.setText(txtPass.getText() + "3");
        }
    }

    public void Boton4(View view) {
        if (EscribirEn=="1"){
            txtUser.setText(txtUser.getText() + "4");
        }else if(EscribirEn=="2"){
            txtPass.setText(txtPass.getText() + "4");
        }
    }

    public void Boton5(View view) {
        if (EscribirEn=="1"){
            txtUser.setText(txtUser.getText() + "5");
        }else if(EscribirEn=="2"){
            txtPass.setText(txtPass.getText() + "5");
        }
    }

    public void Boton6(View view) {
        if (EscribirEn=="1"){
            txtUser.setText(txtUser.getText() + "6");
        }else if(EscribirEn=="2"){
            txtPass.setText(txtPass.getText() + "6");
        }
    }

    public void Boton7(View view) {
        if (EscribirEn=="1"){
            txtUser.setText(txtUser.getText() + "7");
        }else if(EscribirEn=="2"){
            txtPass.setText(txtPass.getText() + "7");
        }
    }

    public void Boton8(View view) {
        if (EscribirEn=="1"){
            txtUser.setText(txtUser.getText() + "8");
        }else if(EscribirEn=="2"){
            txtPass.setText(txtPass.getText() + "8");
        }
    }

    public void Boton9(View view) {
        if (EscribirEn=="1"){
            txtUser.setText(txtUser.getText() + "9");
        }else if(EscribirEn=="2"){
            txtPass.setText(txtPass.getText() + "9");
        }
    }

    public void Boton0(View view) {
        if (EscribirEn=="1"){
            txtUser.setText(txtUser.getText() + "0");
        }else if(EscribirEn=="2"){
            txtPass.setText(txtPass.getText() + "0");
        }
    }

    public void Botonk(View view) {
        if (EscribirEn=="1"){
            txtUser.setText(txtUser.getText() + "K");
        }else if(EscribirEn=="2"){
            txtPass.setText(txtPass.getText() + "K");
        }
    }

    public void BotonR(View view) {
        if (EscribirEn=="1"){
            if (txtUser.getText().length()>0){
                String cadena = String.valueOf(txtUser.getText());
                cadena=cadena.substring(0, cadena.length()-1);
                txtUser.setText(cadena);
            }
        }else if(EscribirEn=="2"){
            if (txtPass.getText().length()>0){
                String cadena = String.valueOf(txtPass.getText());
                cadena=cadena.substring(0, cadena.length()-1);
                txtPass.setText(cadena);
            }
        }
    }

    public void continuar(View view){

        TIPO_VERIFICACION="Clave";
        rutPerson=txtUser.getText().toString();
        HASH=txtPass.getText().toString();
        mostrarDialogoEntradaSalida();

    }


    public void BotonEntrada(View view) {

        mostrarDialogoCarga();
        momentoEvento();
        //Verificamos si hay conexion a internet
        ConnectivityManager cm =(ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(isConnected){

            enviarMarcaServidor(MARCACION,"I",LATITUD,LONGITUD,rutPerson,HASH);

        }else{
            guardarLocalmente(MARCACION,"I",LATITUD,LONGITUD,rutPerson);
        }

    }

    public void BotonSalida(View view) {

        mostrarDialogoCarga();
        momentoEvento();
        //Verificamos si hay conexion a internet
        ConnectivityManager cm =(ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(isConnected){

            enviarMarcaServidor(MARCACION,"O",LATITUD,LONGITUD,rutPerson,HASH);

        }else{
            guardarLocalmente(MARCACION,"O",LATITUD,LONGITUD,rutPerson);
        }



    }

    private void guardarLocalmente(String marca, String tipoMarca, String lat, String lon, String nDoc) {

        //Abrimos la BD para poder escribir en ella
        SQLiteDatabase db = conn.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Utilidades.CAMPO_MARCACION,marca);
        values.put(Utilidades.CAMPO_TIPO_MARCA,tipoMarca);
        values.put(Utilidades.CAMPO_LATITUD,lat);
        values.put(Utilidades.CAMPO_LONGITUD,lon);
        values.put(Utilidades.CAMPO_N_DOCUMENTO,nDoc);

        Long idResul = db.insert(Utilidades.TABLA_MARCAJES,Utilidades.CAMPO_TIPO_MARCA,values);

        if (idResul > 0){

        }else{
            mostrarDialogoSweetError("Se generó un problema al intentar almacenar la informacion localmente");
        }


    }

    protected Integer idTrab(String nDoc){
        Integer idTrabj=0;

        String trab;
        try{
            SQLiteDatabase db = conn.getReadableDatabase();
            String[] parametros = {nDoc};
            String[] campos = {Utilidades.CAMPO_USER_ID};

            Cursor cursor = db.query(Utilidades.TABLA_TABAJADORES , campos , Utilidades.CAMPO_RUT+"=?",parametros,null,null,null);
            cursor.moveToFirst();

            trab = cursor.getString(0);
            if(trab.length()<1){
                elTrabajadorEsDeLaEmpresa=false;
            }else{
                idTrabj = cursor.getInt(0);
                elTrabajadorEsDeLaEmpresa=true;
            }
        }catch (Exception e){
            elTrabajadorEsDeLaEmpresa=false;
        }

        return idTrabj;
    }
}