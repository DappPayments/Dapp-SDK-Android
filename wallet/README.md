# Dapp Wallet SDK for Android

Este SDK está pensado para los Wallets electrónicos integrados al ambiente Dapp. Su función principal es leer códigos QR POS integrados al ambiente Dapp.

## INSTALACIÓN

1. Ve a Android Studio - New Project - Minimun SDK.
2. Selecciona *API 16: Android 4.1* o superior, y crea el proyecto.
3. Una vez creado el proyecto, abre *your_app | build.gradle*.
4. Añade esto a */app/build.gradle* en el nivel de *módulo* antes de *dependencies*:

```java

        repositories {
          mavenCentral()
        }
```

5. Añade la dependencia de compilación con la última versión de **Dapp Core** y **Dapp Wallet SDK** al archivo *build.gradle*:

```java

        dependencies {
          implementation 'mx.dapp.sdk:core:2.7.0@aar'
          implementation 'mx.dapp.sdk:wallet:2.3.5@aar'
        }
```

6. Compila el proyecto y ya puedes inicializar Dapp Wallet en tu aplicación.
    
De forma estándar el SDK monitorea el estado de los códigos QR POS vía peticiones HTTP.  Existe una versión alternativa que sigue el estado del código QR a través de WebSockets con ayuda de la librería [OkHttp](https://github.com/square/okhttp). Si deseas utilizar esta versión incluye esta línea en las dependencias:

```java

        dependencies {
          implementation 'com.squareup.okhttp3:okhttp:4.2.1'
        }
```

## CONFIGURACIÓN

1. Inicializa Dapp reemplazando _your-dapp-api-key_ con tu clave, el ambiente de trabajo y el contexto:

```java
        Dapp.init(your_api_key, DappEnviroment.SANDBOX, requireContext());
```

## CÓDIGOS QR POS

Los códigos QR POS, son códigos generados por negocios integrados al ambiente Dapp, diseñados para que los clientes puedan leer la información del cobro y pagar.

En caso de que el wallet ya cuente con un lector de códigos QR propio, valida y recibe la información de pago creando un objeto *DappCode* con el valor del código QR y llama a la función **read()**
```java
        DappPosCode dappPosCode = new DappPosCode(qrTextFromScanner);
        dappPosCode.read(new DappCodeReadCallback() {
            @Override
            public void onSuccess() {
                
            }

            @Override
            public void onError(DappException exception) {

            }
        });

```
El ambiente Dapp incluye códigos QR de diversas fuentes. En caso de ser un código hecho por Dapp puedes obtener el dapp ID de la siguiente manera:
```java
    dappPosCode.getDappId();
```
**Dapp Wallet SDK Android** también es compatible con CoDi. Existen dos funciones que puedes utilizar:
```java
    dappPosCode.isCodi() //true or false
    dappPosCode.getQRType() //codi, dapp, codiDapp, unknown
```
## LECTOR DE CÓDIGOS QR POS
Para utilizar esta funcionalidad agrege la dependencia a la libreria [barcodescanner](https://github.com/dm77/barcodescanner):
```java
    implementation 'me.dm7.barcodescanner:zxing:1.9.13'
```

Las funciones del lector se pueden implementar de dos formas:

 - **Como activity**:  Más rápido y sencillo. Crea un _DappWalletScannerActivity_ y preséntalo. Esta activity se encarga de obtener la información de los códigos QR Dapp y de todos los aspectos relacionados con el UX.
 - **Como fragment** : Más flexible. Crea un _DappScannerFragment_ que solo se encargará de leer el código QR. Esto te permite implementar un UX que vaya más acorde con tu aplicación.

### Integra el lector como activity

1. Crea un **Intent** e implementa el método _onActivityResult_ para recibir información asociada al código QR.

```java
    void scanner(){
        Intent intent = new Intent(requireActivity(), DappWalletScannerActivity.class);
        startActivityForResult(intent, 5555);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5555) {
            if (resultCode == requireActivity().RESULT_OK) {
                DappPosCode dappPosCode = data.getExtras().getParcelable(DappWalletScannerActivity.DAPP_CODE);
            }
        }
    }
```
### Integra el lector como fragment

1. Incluye **DappScannerFragment** en el _view_ de tu activity y crea una instancia en el _onCreate()_ de tu activity
```xml
    <fragment xmlns:android="http://schemas.android.com/apk/res/android"
        android:id="@+id/dapp_scanner_fragment"
        android:name="mx.dapp.sdk.core.scanner.DappScannerFragment"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
    </fragment>
```
```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dapp_scanner_activity);

        DappScannerFragment dappScannerFragment = (DappScannerFragment) getSupportFragmentManager().findFragmentById(R.id.dapp_scanner_fragment);
    }
```

2. Asigna una implementacion del _DappScannerCallback_ para recibir la lectura del código QR.
```java
    dappScannerFragment.setScannerCallback(new DappScannerCallback(){
        @Override
        public void onScan(String result){
        
        }
        
        @Override
        public void onClose(){
        
        }
        
        @Override
        public void onError(DappException exception) {
        
        }
    });
```

3. Sobreescribe los métodos del ciclo de vida de tu activity para controlar el estado del escáner.
```java
    @Override
    protected void onResume() {
        super.onResume();
        dappScannerFragment.startScanning();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        dappScannerFragment.stopScanning();
    }
```

4. En caso de necesitar saber si el lector está activo utilice la funcion _isScanning_
```java
        boolean isScanning = dappScannerFragment.isScanning()
```

## REALIZAR PAGOS A CODIGOS POS
Puedes recibir solicitudes de pago de cualquier app de negocio integrado al ambiente Dapp que el usuario tenga instalado en su dispositivo.

1. Configurar el archivo **AndroidManifest.xml**. Agrega un elemento **intent-filter** a la activity responsable de recibir los datos del pago usando los datos Dapp.
```xml    
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />

                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                
                <data
                    android:host="dapp.mx"
                    android:pathPrefix="/c/"
                    android:scheme="mxdapp" />
            </intent-filter>
        </activity>
```

2. Sobreescribe el método **onResume()** en tu activity y crea un objeto _DappPosCode_ con el **Intent** recibido:
```java
    @Override
    public void onResume(){
        super.onResume()
        Intent intent = getIntent();
        if (intent.getData() != null) {
             try {
                DappPosCode dappPosCode = new DappPosCode(intent);
                dappPosCode.read(new DappCodeReadCallback() {
                    @Override
                    public void onSuccess() {
                        //pay code via own server
                    }

                    @Override
                    public void onError(DappException exception) {
                        exception.printStackTrace();
                    }
                });
            } catch (DappCodeException e) {
                e.printStackTrace();
            }
        }
    }
```

3. Una vez que hayas realizado la transacción desde tu servidor, notifica a la aplicación del negocio con la función **returnPayment()**
```java
    String paymentIdFromServer = "dcd7dc9c-e955-4668-ba21-45b0a6c48e72";
    dappPosCode.returnPayment(paymentIdFromServer);
```

## LICENCIA
[MIT](../LICENSE.txt)


