# Dapp SDK #

**Dapp SDK** es la forma más sencilla de integrar la plataforma de pagos **Dapp** en una aplicación Android. En consecuencia cuenta con las siguientes funcionalidades:

* Realizar un pago.
* Guardar una tarjeta de crédito o débito.

Para disponer de estas funcionalidades primero necesitas configurar **Dapp SDK** en tu aplicación como sigue:

1. Ve a Android Studio - New Project - Minimun SDK.

2. Selecciona *API 15: Android 4.0.3* o superior, y crea el proyecto.

3. Una vez creado el proyecto, abre *your_app | build.gradle*.

4. Añade esto a */app/build.gradle* en el nivel de *módulo* antes de *dependencies*:


```java

repositories {
  mavenCentral() 
}

```
5. Añade la dependencia de compilación con la última versión de **Dapp SDK** al archivo *build.gradle*:


```java

dependencies { 
  implementation 'mx.dapp:android-sdk:1.0.0'
}

```

6. Compila el proyecto y ya puedes inicializar **Dapp** en tu aplicación.

### Iniciar **Dapp SDK** ###

1. A continuación necesitarás un *api_key* y *merchand_id* generados por **Dapp**. Entra a  [ Dapp.mx ](https://dapp.mx)  y sigue las instrucciones para obtenerlos.

2. Abre el archivo *strings.xml*. Por ejemplo: */app/src/main/res/values/strings.xml*.

3. Añade una nueva cadena con el nombre *api_key* y *merchand_id*, que contienen los valores del identificador de la aplicación de **Dapp**:


```xml

<string name="api_key">457d5970-0411-467b-8b5a-040240e78f01</string>
<string name="merchand_id">346ac601-5fc5-4653-9817-6b1527d3ac19</string>

```

4. Abre el archivo *AndroidManifest.xml*.

5. Añade un elemento *uses-permission* al manifiesto:


```xml

<uses-permission android:name="android.permission.INTERNET"/>

```

6. Ahora puedes iniciar **Dapp SDK** desde la función *onCreate* de tu actividad, ten en cuenta el *DappEnviroment* que deseas utilizar:


```java

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    Dapp.init(this, getString(R.string.api_key), getString(R.string.merchand_id), DappEnviroment.SANDBOX);
}

```
### Utilizar **Dapp SDK** desde tu aplicación Android. ###

Una vez completados los pasos de **Configuración** e **Inicio** tu aplicación está lista para integrar las fucionalidades **Dapp**.

### Realiza un pago utilizando la plataforma **Dapp** ###

1. Invoque la funcionalidad de pagar en el cuerpo de la actividad donde se inicio **Dapp SDK**:


```java

//Cantidad a pagar (obligatoria y mayor que 0)
double amount = 100.00;

//Descripcion del pago (obligatoria)
String description = "Any description";

//Referencia del desarrollador (Opcional y nunca se muestra)
String reference = "Any development reference";


//Busca una instalación de Dapp App en el dispositivo para el procesamiento del pago.
//De no exixtir en el dispositivo una instalacion válida de Dapp App se redirecciona a
// Google Play para su descarga

Dapp.requestPayment(amount, description, reference, new DappPaymentCallback() {...});

```

**AYUDA**: La función *isDappAvailable* permite conocer si existe una instalación válida de _Dapp_ en el dispoitivo:

```java

if(Dapp.isDappAvailable(context)){
       //request payment...
    }else{
       //do your alternative stuff...
    }

```

2. En la función *onActivityResult* devuelve el resultado a **Dapp SDK**:


```java

@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    Dapp.onResult(requestCode, resultCode, data);
}

```

3. Procese su respuesta en los callbacks correspondientes:


```java

@Override
public void onSuccess(DappPayment transaction) {
   Log.d("Dapp SDK", " callback success transaction: " + transaction.getId());
}

@Override
public void onError(DappException e) {
   Log.d("Dapp SDK", "callback result: " + e.getCodeError() + ", message: " + e.getMessage());
}

```

### Guarda una tarjeta de crédito o débito en **Dapp** ###
 
1. Invoque la funcionalidad de guardar tarjeta en el cuerpo de la actividad donde se inició **Dapp SDK**:


```java

//Número de la tarjeta (obligatorio y longitud igual a 16)
String cardNumber = "5515150180013278";

//Nombre del tarjeta habiente (obligatorio)
String cardHolder = "Luke Skywalker";

//Mes de expiración (obligatorio y longitud menor o igual a 2)
String expMonth = "05";

//Año de expiración (obligatorio y longitud menor o igual a 2)
String expYear = "21";

//Código de seguradad (obligatorio y longitud menor o igual a 4)
String cvv = "036";

Dapp.addCard(cardNumber, cardHolder, expMonth, expYear, cvv, new DappCardCallback() {...});

```

2. Procese su respuesta en los *callbacks* correspondientes:


```java

@Override
public void onSuccess(DappCard card) {
   Log.d("Dapp SDK", " callback success add card: " + card.getToken());
}

@Override
public void onError(DappException e) {
    Log.d("Dapp SDK", "callback result: " + e.getCodeError() + ", message: " + e.getMessage());
}

```
