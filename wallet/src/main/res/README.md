h1. Primeros pasos _Dapp Wallet SDK Library_ para Android

    _Dapp Wallet SDK_ es la forma de leer y pagar códigos _Dapp_ en una aplicación Android. En consecuencia cuenta con las siguientes funcionalidades:

    * Comprobar que sea un código QR Dapp válido.
    * Obtener la información de un código QR Dapp.
    * Invocar el lector de códigos QR de __Dapp Wallet SDK__

    Para disponer de estas funcionalidades primero necesitas configurar _Dapp Wallet SDK_ en tu aplicación como sigue:

    1. Ve a Android Studio - New Project - Minimun SDK.

    2. Selecciona @API 15: Android 4.0.3@ o superior, y crea el proyecto.

    3. Una vez creado el proyecto, abre @your_app | build.gradle@.

    4. Añade esto a @/app/build.gradle@ en el nivel de @módulo@ antes de @dependencies@:

<pre><code class="java">

repositories {
  mavenCentral()
}

</code></pre>

    5. Añade la dependencia de compilación con la última versión de _Dapp Wallet SDK_ al archivo @build.gradle@:

<pre><code class="java">

dependencies {
  compile 'com.dapp.android:wallet-sdk:1.+'
}

</code></pre>

    6. Compila el proyecto y ya puedes inicializar Dapp Wallet en tu aplicación.

    h2. Iniciar _Dapp Wallet SDK_

    1. Explicación de como obtener tu @api_key@.

    2. Abre el archivo @strings.xml@. Por ejemplo: @/app/src/main/res/values/strings.xml@.

    3. Añade una nueva cadena con el nombre @api_key@, que contiene el valor del identificador de la aplicación de Dapp Wallet:

<pre><code class="xml">

<string name="api_key">d144459e-d730-4762-b937-e6639dc8f5e6</string>

</code></pre>

    4. Abre el archivo @AndroidManifest.xml@.

    5. Añade los elementos @uses-permission@ al manifiesto:

<pre><code class="xml">

<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.CAMERA" />

</code></pre>

    6. Ahora puedes iniciar _Dapp Wallet SDK_ desde el método @onCreate@ de tu actividad, señalando el ambiente en que deseas que funcione la libreria:

<pre><code class="java">

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    DappWallet.init(this, getString(R.string.api_key), DappEnviroment.SANDBOX);
}

</code></pre>

    h1. Utilizar _Dapp Wallet SDK_ desde tu aplicación Android.

    Una vez completados los pasos de _Configuración_ e _Inicio_ su aplicación esta lista para integrar las fucionalidades _Dapp Wallet_.

    h2. Comprobar que es un QR Dapp válido y obtener la información del mismo

    1. Invoque las funcionalidades de validar y posteriormente la de leer en el cuerpo de la actividad donde se inicio _Dapp Wallet SDK_:

<pre><code class="java">
                 
try {
    String qr = "https://dapp.mx/c/oW9BYXqJ"
    if (DappWallet.isValidDappQR(qr)){
        DappWallet.readDappQR(qr, new DappWalletCallback() {
            @Override
            public void onSuccess(DappWalletPayment payment) {
                Log.d("wallet", payment.toString());
            }

            @Override
            public void onError(DappException exception) {
                Log.d("wallet", exception.getMessage(), exception);
            }
        });
    }
} catch (DappException e) {
    e.printStackTrace();
}

</code></pre>

    h2. Invocar el lector de códigos QR de __Dapp Wallet SDK__

    1. Invoque la funcionalidad de lector de códigos en el cuerpo de la actividad donde se inició _Dapp Wallet SDK_:

<pre><code class="java">

DappWallet.dappReader(new DappWalletCallback() {
    @Override
    public void onSuccess(DappWalletPayment payment) {
        Log.d("wallet", payment.toString());
    }

    @Override
    public void onError(DappException exception) {
        Log.d("wallet", exception.getMessage(), exception);
    }
});

</code></pre>

    2. En el método @onActivityResult@ devuelva el resultado a _Dapp Wallet SDK_:

<pre><code class="java">

@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    DappWallet.onReaderResult(requestCode, resultCode, data);
}

</code></pre>
