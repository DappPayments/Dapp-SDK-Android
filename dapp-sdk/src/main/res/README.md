h1. Primeros pasos _Dapp SDK Library_ para Android

    _Dapp SDK_ es la forma más sencilla de integrar la plataforma de pagos _Dapp_ en una aplicación Android. En consecuencia cuenta con las siguientes funcionalidades:

    * Realizar un pago.
    * Guardar una tarjeta de crédito o débito.

    Para disponer de estas funcionalidades primero necesitas configurar _Dapp SDK_ en tu aplicación como sigue:

    1. Ve a Android Studio - New Project - Minimun SDK.

    2. Selecciona @API 15: Android 4.0.3@ o superior, y crea el proyecto.

    3. Una vez creado el proyecto, abre @your_app | build.gradle@.

    4. Añade esto a @/app/build.gradle@ en el nivel de @módulo@ antes de @dependencies@:

<pre><code class="java">
repositories {
  mavenCentral()
}
</code></pre>

    5. Añade la dependencia de compilación con la última versión de _Dapp SDK_ al archivo @build.gradle@:

<pre><code class="java">
dependencies {
  compile 'com.dapp.android:dapp-android-sdk:1.+'
}
</code></pre>

    6. Compila el proyecto y ya puedes inicializar Dapp en tu aplicación.

    h2. Iniciar _Dapp SDK_

    1. Explicación de como obtener tu @api_key@ y tu @merchand_id@.

    2. Abre el archivo @strings.xml@. Por ejemplo: @/app/src/main/res/values/strings.xml@.

    3. Añade una nueva cadena con el nombre @api_key@ y @merchand_id@, que contienen los valores del identificador de la aplicación de Dapp:

<pre><code class="xml">
<string name="api_key">457d5970-0411-467b-8b5a-040240e78f01</string>
<string name="merchand_id">346ac601-5fc5-4653-9817-6b1527d3ac19</string>
</code></pre>

    4. Abre el archivo @AndroidManifest.xml@.

    5. Añade un elemento @uses-permission@ al manifiesto:

<pre><code class="xml">
<uses-permission android:name="android.permission.INTERNET"/>
</code></pre>

    6. Ahora puedes iniciar _Dapp SDK_ desde el metodo @onCreate@ de tu actividad:

<pre><code class="java">
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Dapp.init(this, getString(R.string.api_key), getString(R.string.merchand_id));
}
</code></pre>

    h1. Utilizar _Dapp SDK_ desde tu aplicación Android.

    Una vez completados los pasos de _Configuración_ e _Inicio_ su aplicación esta lista para integrar las fucionalidades _Dapp_.

    h2. Realiza un pago utilizando la plataforma _Dapp_

    1. Invoque la funcionalidad de pagar en el cuerpo de la actividad donde se inicio _Dapp SDK_:

<pre><code class="java">
Button btnPay = (Button) findViewById(R.id.btnPay);

//Cantidad a pagar (obligatoria y mayor que 0)
double amount = 100.00;

//Descripcion del pago (obligatoria)
String description = "Any description";

//Referencia del desarrollador (Opcional y nunca se muestra)
String reference = "Any development reference";

//Busca una instalacion de Dapp App en el dispositivo y de encontrarla se redirecciona de lo contrario
//se abre el formulario de Dapp SDK, si se pasa como false omite lo
//anterior y siempre muestra el formulario por defecto de Dapp SDK
boolean defaultUseForm = false;

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dapp.requestPayment(amount, description, reference, false, new DappPaymentCallback() {...});
            }
        });

</code></pre>

    2. En el metodo @onActivityResult@ devuelva el resultado a _Dapp SDK_:

<pre><code class="java">
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Dapp.onResult(requestCode, resultCode, data);
    }
</code></pre>

    3. Procese su respuesta en los callbacks correspondientes:

<pre><code class="java">
    @Override
    public void onSuccess(DappPayment transaction) {
        Log.d("Dapp SDK", " callback success transaction: " + transaction.getId());
    }

    @Override
    public void onError(DappException e) {
        Log.d("Dapp SDK", "callback result: " + e.getCodeError() + ", message: " + e.getMessage());
    }
</code></pre>

    h2. Guarda una tarjeta de crédito o débito en _Dapp_

    1. Invoque la funcionalidad de guardar tarjeta en el cuerpo de la actividad donde se inició _Dapp SDK_:

<pre><code class="java">
Button btnSave = (Button) findViewById(R.id.btnSave);

//Número de la tarjeta (obligatorio y longitud igual a 16)
String cardNumber = "5515150180013278";

//Nombre del tarjeta habiente (obligatorio)
String cardHolder = "Luke Skywalker";

//Mes de expiración (obligatorio y longitud menor o igual a 2)
String expMonth = "05";

//Año de expiración (obligatorio y longitud menor o igual a 2)
String expMonth = "21";

//Código de seguradad (obligatorio y longitud menor o igual a 4)
String cvv = "036";

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dapp.addCard(cardNumber, cardHolder, expMonth, expYear, cvv, new DappCardCallback() {...});
            }
        });

</code></pre>

    2. Procese su respuesta en los callbacks correspondientes:

<pre><code class="java">
    @Override
    public void onSuccess(DappCard card) {
        Log.d("Dapp SDK", " callback success add card: " + card.getToken());
    }

    @Override
    public void onError(DappException e) {
        Log.d("Dapp SDK", "callback result: " + e.getCodeError() + ", message: " + e.getMessage());
    }
</code></pre>








