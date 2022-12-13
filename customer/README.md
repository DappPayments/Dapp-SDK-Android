# Dapp Customer SDK for Android

Este SDK está pensado para las aplicaciones de negocios con ventas no presenciales  y cuenta con dos funcionalidades:

 - Tokenizar tarjetas.
 - Realizar solicitudes de pago a los wallets integrados en el ambiente Dapp.

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

5. Añade la dependencia de compilación con la última versión de **Dapp Core** y **Dapp Customer SDK** al archivo *build.gradle*:

```java

        dependencies {
          implementation 'mx.dapp.sdk:core:3.1.0@aar'
          implementation 'mx.dapp.sdk:customer:3.0.0@aar'
        }
```

6. Compila el proyecto y ya puedes inicializar Dapp Customer en tu aplicación.

## CONFIGURACIÓN
1. Inicializa Dapp reemplazando _your-dapp-api-key_ con tu clave, el ambiente de trabajo y el contexto:

```java
        Dapp.init(your_api_key, DappEnviroment.SANDBOX, requireContext());
```
## TOKENIZAR TARJETAS
Tokeniza las tarjetas de tus usuarios, guarda la referencia en tu base de datos y realiza pagos con esa tarjeta cuando lo desee el usuario.

```java

    void tokenizeCard() {
        String card = "5515150180013278";
        String cardHolder = "Daenerys Targaryen";
        String cvv = "123";
        String month = "01";
        String year = "2030";
        String email = "daenerys@gameofthrones.com";
        String phoneNumber = "5512345678";
    
        try {
            DappCard.add(card, cardHolder, month, year, cvv, email, phoneNumber, new DappCardCallback() {
                @Override
                public void onSuccess(DappCard card) {

                }

                @Override
                public void onError(DappException exception) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

```
## REALIZAR PAGOS
Realiza solicitudes de pago a cualquier wallet integrado al ambiente Dapp que el usuario tenga instalado en su dispositivo.

1. Agrega las siguientes líneas en tu fichero _strings.xml_ y reemplaza los valores  _myhost.mx_ y _myscheme_ con valores referentes a tu aplicación para crear tu **URL única**.
```xml
    <string name="dapp_callback_host">myhost.mx</string>
    <string name="dapp_callback_path_prefix">/payment/</string>
    <string name="dapp_callback_scheme">myscheme</string>
```

2. Configurar el archivo **AndroidManifest.xml**. Agrega un elemento **intent-filter** a la activity responsable de recibir los datos del pago.
```xml    
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />

                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                
                <data
                    android:host="@string/dapp_callback_host"
                    android:pathPrefix="@string/dapp_callback_path_prefix"
                    android:scheme="@string/dapp_callback_scheme" />
            </intent-filter>
        </activity>
```

3. Crea un objeto DappPosCode, asigna un callback para poder obtener la respuesta del pago y utiliza la funcion **pay()**

```java
        DappPosCode dappPosCode = new DappPosCode(10.0, "my description", "my reference");
        
        dappPosCode.pay(requireContext(), new DappCallback() {
            @Override
            public void onError(DappException exception) {
                exception.printStackTrace();
            }
        });
```

4. Sobreescribe el metodo **onResume()** en tu activity y utiliza el metodo estático _getPaymentId_ de la clase **DappPosCode**:
    
```java
        @Override
        public void onResume(){
            super.onResume()
            Intent intent = getIntent();
            String paymentId = DappPosCode.getPaymentId(intent, this);
            if(paymentId != null){
                //do something
            }
        }
```
## LICENCIA
[MIT](../LICENSE.txt)

