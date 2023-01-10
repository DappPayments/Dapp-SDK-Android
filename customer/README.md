# Dapp Customer SDK for Android

Este SDK esta pensado para las aplicaciones de negocios con ventas no presenciales.  Puedes realizar solicitudes de pago a los wallets integrados en el ecosistema a través del Dapp Checkout.

## INSTALACIÓN

1. Ve a Android Studio - New Project - Minimum SDK.
2. Selecciona *API 21: Android 5.0* o superior, y crea el proyecto.
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
          implementation 'mx.dapp.sdk:core:3.1.1@aar'
          implementation 'mx.dapp.sdk:customer:4.0.0@aar'
        }
```

6. Compila el proyecto y ya puedes inicializar Dapp Customer en tu aplicación.

## CONFIGURACIÓN
1. Inicializa Dapp reemplazando _your-dapp-api-key_ con tu clave, el ambiente de trabajo y el contexto:

```java
        Dapp.init(your_api_key, DappEnviroment.SANDBOX, requireContext());
```
## REALIZAR COBROS A TRAVÉS DE DAPP CHECKOUT
Para realizar cobros dentro de Dapp, los comercios deben generar códigos de cobro que serán pagados por el cliente a través de su aplicación preferida. El cliente puede elegir esta aplicación de manera transparente para el comercio a través de la plataforma Dapp Checkout.

1. Inicializa un objeto DappCode, utiliza la función **create**, esta llamada es asíncrona, asignale un callback.
2. En el callback de la creacion del codigo se debe llamar a la activity _DappCheckoutActivity_, pasandole por el intent el DappCode creado en el paso anterior.
3. Debes mandar llamar la activity esperando un resultado para poder recibir la informacion de pago en **onActivityResult**.
4. Cuando el usuario haya realizado el pago, la _DappCheckoutActivity_ se dejará de presentar y recibirás la información de pago en **onActivityResult**.


```java

public class MainActivity extends AppCompatActivity {
    
    private int requestCodeDapp = 28;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Dapp.init("your_api_key", DappEnviroment.SANDBOX, this);

        final DappPosCode dappCode = new DappPosCode(28, "dapp test", "321123");
        dappCode.create(new DappPosCodeCallback() {
            @Override
            public void onSuccess() {
                Intent i = new Intent(MainActivity.this, DappCheckoutActivity.class);
                i.putExtra(DappCheckoutActivity.DAPP_CODE, dappCode);
                startActivityForResult(i, requestCodeDapp);
            }

            @Override
            public void onError(DappException exception) {
                Log.e("MYLOG", "Error creando dapp code", exception);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestCodeDapp) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                DappPayment payment = extras.getParcelable(DappCheckoutActivity.DAPP_PAYMENT);
                Log.d("MYLOG", payment.getId());
            }
            else if (resultCode == RESULT_CANCELED) {
                Bundle extras = data.getExtras();
                String errorMsg = extras.getString(DappCheckoutActivity.DAPP_ERROR);
                Log.e("MyLOG", errorMsg);
            }
        }
    }
    
}

```
## LICENCIA
[MIT](../LICENSE.txt)

