package mx.dapp.sdk.core.network.http;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import mx.dapp.sdk.core.enums.DappResult;
import mx.dapp.sdk.core.exceptions.DappException;
import mx.dapp.sdk.core.network.tls.Tls12SocketFactory;

public class DappHttpClient extends AsyncTask<String, Long, String> {
    private DappResponseProcess dappResponseProcess;
    private HashMap<String, String> postValues;
    private final String header;
    private final String method;
    private static Exception exception;

    private String performGetCall(String requestUrl) {
        URL url;
        StringBuilder response = new StringBuilder();

        try {
            url = new URL(requestUrl);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            try {
                SSLContext sc = SSLContext.getInstance("TLSv1.2");
                sc.init(null, null, null);
                conn.setSSLSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }

            conn.setRequestProperty("Authorization", "Basic " + header);
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod(method);
            conn.setDoInput(true);

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            } else {
                response = new StringBuilder();

            }
        } catch (Exception e) {
            exception = e;
        }

        return response.toString();
    }

    private String performPostCall(String requestUrl,
                                   HashMap<String, String> postDataParams) {

        URL url;
        String response = "";
        try {
            url = new URL(requestUrl);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            try {
                SSLContext sc = SSLContext.getInstance("TLSv1.2");
                sc.init(null, null, null);
                conn.setSSLSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }

            conn.setRequestProperty("Authorization", "Basic " + header);
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

    public DappHttpClient(String method, HashMap<String, String> postValues, String header, DappResponseProcess dappResponseProcess) {
        this.postValues = postValues;
        this.dappResponseProcess = dappResponseProcess;
        this.header = header;
        this.method = method;
    }

    @Override
    protected void onPreExecute() {
        if (dappResponseProcess != null) {
            dappResponseProcess.processStart();
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        String url = strings[0];
        String responseBody = "";
        if (postValues == null || postValues.isEmpty()) {
            responseBody = performGetCall(url);
        } else {
            responseBody = performPostCall(url, postValues);
        }
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
                parseResult(result);
            }
        }
    }

    private void parseResult(String result) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
        } catch (JSONException e) {
            DappException de = new DappException(DappResult.RESULT_RESPONSE_ERROR);
            dappResponseProcess.processFailed(de);
            return;
        }
        int rc = jsonObject.optInt("rc", -1);
        String msg = jsonObject.optString("msg");
        Object data = jsonObject.opt("data");
        if (rc != 0) {
            dappResponseProcess.onError(rc, msg);
        } else {
            dappResponseProcess.processSuccess(data);
        }
    }

}
