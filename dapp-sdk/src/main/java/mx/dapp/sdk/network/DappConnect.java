package mx.dapp.sdk.network;

import android.os.AsyncTask;
import android.util.Base64;

import mx.dapp.sdk.tools.Dapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by carlos on 9/06/17.
 */

class DappConnect extends AsyncTask<String, Long, String> {

    private DappResponseProcess dappResponseProcess;
    private HashMap<String, String> postValues;
    private static Exception exception;

    private static final String BASIC_URL = Dapp.getEnviroment().getTarget();
    private static final String URL_VERSION = "v1";


    private static String performPostCall(String requestUrl,
                                         HashMap<String, String> postDataParams) {

        URL url;
        String response = "";
        try {
            url = new URL(requestUrl);

            String base64 = "";
            try {
                byte[] data = (Dapp.getMerchantId() + ":" + Dapp.getApiKey()).getBytes("UTF-8");
                base64 = Base64.encodeToString(data, Base64.NO_WRAP);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestProperty("Authorization", "Basic " + base64);
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));
            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";

            }
        } catch (Exception e) {
            exception = e;
        }

        return response;
    }

    private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (HashMap.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    DappConnect (HashMap<String, String> postValues, DappResponseProcess dappResponseProcess){
        this.postValues = postValues;
        this.dappResponseProcess = dappResponseProcess;
    }

    @Override
    protected void onPreExecute() {
        if (dappResponseProcess != null) {
            dappResponseProcess.processStart();
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        String endpoint = strings[0];

        String responseBody = performPostCall(BASIC_URL + URL_VERSION + endpoint, postValues);

        return responseBody;
    }

    @Override
    protected void onProgressUpdate(Long... progress) {
    }

    @Override
    protected void onCancelled() {

    }

    @Override
    protected void onPostExecute(String result) {
        if (dappResponseProcess != null) {
            if (exception != null) {
                dappResponseProcess.processFailed(exception);
                return;
            }
            if (result != null) {
                dappResponseProcess.processSuccess(result);
            }
        }
    }

}
