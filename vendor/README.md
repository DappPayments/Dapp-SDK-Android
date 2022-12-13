# Dapp Vendor SDK for Android
Este SDK esta pensado para las aplicaciones de negocios con ventas presenciales. Cuenta con dos funciones principales:

- Leer códigos QR Request to Pay integrados al ambiente Dapp.
- Generar y monitorear el estado de códigos Dapp QR POS.

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

5. Añade la dependencia de compilación con la última versión de **Dapp Core** y **Dapp Vendor SDK** al archivo *build.gradle*:

```java

        dependencies {
          implementation 'mx.dapp.sdk:core:3.1.0@aar'
          implementation 'mx.dapp.sdk:vendor:3.2.0@aar'
        }
```

6. Compila el proyecto y ya puedes inicializar Dapp Vendor en tu aplicación.

De forma estándar el SDK monitorea el estado de los códigos QR POS vía peticiones HTTP. Existe una versión alternativa que sigue el estado del código QR a través de WebSockets con ayuda de la librería [OkHttp](https://github.com/square/okhttp). Si deseas utilizar esta versión incluye esta línea en las dependencias:

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

1. Añade la dependencia de la libreria [zxing 3.4.0](https://github.com/zxing/zxing/releases/tag/zxing-3.4.0) .
```java
    implementation 'com.google.zxing:core:3.4.0'
```

2. Obtén el listado de wallets que pueden pagar el código que vas a generar
```java
        DappPosCode.getWallets(new DappCodesWalletsCallback() {
            @Override
            public void onSuccess(List<DappWallet> wallets) {
            }

            @Override
            public void onError(DappException exception) {
            }
        });
```

3. Seleccionado el Wallet crea un objeto **DappPosCode**.
```java
       DappPosCode dappPosCode = new DappPosCode(10.0, "my description", "my reference", wallets[0]);
```

3.1 Crea un objeto **DappPosCode** pasando adicionalmente el párametro _expirationMinutes_.
```java
       DappPosCode dappPosCode = new DappPosCode(10.0, "my description", "my reference", wallets[0], 5);
```

4. Genera el código.

```java
        dappPosCode.createWithImage(500, 500, new DappCodePoSImageCallback() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                
            }

            @Override
            public void onError(DappException exception) {
            
            }
        });
```

4.1 Genera el código sin imagen

```java
        dappPosCode.create(new DappCodePoSCallback() {
               @Override
               public void onSuccess() {
                    //Accede a los atributos del objeto DappPosCode
                    String qrText = dappPosCode.getQrText();
                    String urlImage = dappPosCode.getUrlImage();
               }
   
               @Override
               public void onError(DappException exception) {
               
               }
           });
```


5. Empieza a monitorear el estado de pago del código con la función _listen_

```java
        dappPosCode.listen();
```

6. Utiliza la funcion _stopListening_ para detener el monitoreo del pago
```java
        dappPosCode.stopListening();
```

7. Envía códigos POS por push notifications

El comercio puede hacer llegar el cobro al dispositivo de su cliente mediante una notificación push.

Una vez generado el código de cobro y seleccionada la aplicación del cliente llama a la función _sendPushNotification(phoneNumber)_ del objeto _DappPOSCode_.
```java
        dappPosCode.sendPushNotification("5555555555", new DappCodePosPushNotificationCallback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(DappException exception) {

                            }
                        });
```


8. Enviar cobro CoDi por push notification

En caso de que el comercio solo tenga habilitado cobros CoDi, una vez que el código QR ha sido creado, puede enviarlo a la aplicación CoDi del usuario a través de una push notification con la siguiente función.
```java
        dappPosCode.sendPushNotification("5555555555", new DappCodePosPushNotificationCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(DappException exception) {

            }
        });
```


## CÓDIGOS QR REQUEST TO PAY
Los códigos QR RP, son códigos generados por usuarios, diseñados para dar permiso al negocio lector de realizar un cobro a su cuenta.

En caso de que la aplicación ya cuente con un lector de códigos QR propio, crea un objeto *DappRPCode* con el valor del código QR y llama a la función **charge()** para realizar un cargo al usuario.
```java
void codeScanned(String qrString) {
    DappRPCode code = DappRPCode(qrString);
    double amount = 100;
    String description  = "Payment description";
    String reference = "Internal reference";
    code.charge(amount, description: description, reference: referencenew DappPaymentCallback() {
        @Override
        public void onSuccess(DappPayment payment) {
                
        }

        @Override
        public void onError(DappException exception) {

        }
    });
```
## LECTOR DE CÓDIGOS QR RP
Para utilizar esta funcionalidad agrege las dependencias a las librerias [CameraX](https://developer.android.com/jetpack/androidx/releases/camera) y [ML Kit Barcode Scanning](https://developers.google.com/ml-kit/vision/barcode-scanning/android):
```java
    //ML Kit Barcode Scanning
    implementation 'com.google.mlkit:barcode-scanning:16.1.1'

    //CameraX Dependencies
    implementation "androidx.camera:camera-core:1.1.0-alpha02"
    implementation "androidx.camera:camera-camera2:1.1.0-alpha02"
    implementation "androidx.camera:camera-lifecycle:1.1.0-alpha02"
    implementation "androidx.camera:camera-view:1.0.0-alpha22"
    implementation "androidx.camera:camera-extensions:1.0.0-alpha22"
    implementation 'com.google.android.material:material:1.4.0-alpha01'
```
La versión de compilación y mínima del SDK debe ser 29 y 21 respectivamente o superior:
```java
android {
    compileSdkVersion 29
    defaultConfig {
        minSdkVersion 21
    }
}
```
Agregue las opciones de compilación para Java:
```java
android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```

Las funciones del lector se pueden implementar de dos formas:

 - **Como activity**:  Más rápido y sencillo. Crea un _DappVendorScannerActivity_ y preséntalo. Éste activity se encarga de obtener la información de los códigos QR Dapp y de todos los aspectos relacionados con el UX.
 - **Como fragment** : Más flexible. Crea un _DappScannerFragment_ que solo se encargará de leer el código QR, e inclúyelo dentro de tu activity. Esto te permite implementar un UX que vaya más acorde con tu aplicación.

### Integra el lector como activity

1. Crea un **Intent** e implementa el método _onActivityResult_ para recibir información del pago asociada al código QR escaneado.
```java
    void scanner(double amount, String description, String reference){
        Intent intent = new Intent(requireActivity(), DappVendorScannerActivity.class);
        intent.putExtra(DappVendorScannerActivity.AMOUNT, amount);
        intent.putExtra(DappVendorScannerActivity.DESCRIPTION, description);
        intent.putExtra(DappVendorScannerActivity.REFERENCE, reference);
        startActivityForResult(intent, 5555);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 5555){
            if(resultCode == requireActivity().RESULT_OK){
                DappPayment payment = data.getExtras().getParcelable(DappVendorScannerActivity.PAYMENT);
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

2. Asigna una implementacion de _DappScannerCallback_ para recibir la lectura del código QR.
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

## REPORTE DE PAGOS

Obtener un reporte de pagos en un rango de fechas

1. Obtén el listado de pagos especificando una _fechaInicio_ y una _fechaFin_ con el formato **yyyy-MM-dd**
```java
    DappPayment.getDappPayments("2021-11-16", "2021-12-16", new DappPaymentsCallback() {
        @Override
        public void onSuccess(List<DappPayment> payments) {
                
        }
        
        @Override
        public void onError(DappException exception) {
            
        }
    });
```

## LICENCIA
[MIT](../LICENSE.txt)


