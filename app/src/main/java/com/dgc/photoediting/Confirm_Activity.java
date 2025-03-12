package com.dgc.photoediting;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dgc.photoediting.PopUp.PopupCallBackOneButton;
import com.dgc.photoediting.PopUp.PopupClass;
import com.dgc.photoediting.setgetclass.APIClient;
import com.dgc.photoediting.setgetclass.UpdatePasswordResult;
import com.dgc.photoediting.setgetclass.UpdatePasswordValue;
import com.dgc.photoediting.utility.Utility;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;

public class Confirm_Activity extends AppCompatActivity {
    TextInputEditText new_pass,confirm_pass;
    Button btn_confirm;
    TextView tram,privacy;
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_activity);
        new_pass = (TextInputEditText)findViewById(R.id.new_pass);
        confirm_pass = (TextInputEditText)findViewById(R.id.confirm_pass);
        btn_confirm = (Button)findViewById(R.id.btn_confirm);
        tram = (TextView)findViewById(R.id.tram);
        privacy = (TextView)findViewById(R.id.privacy);
        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        if(b!=null)
        {
             email =(String) b.get("email");
        }
        tram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(), Termsconditions.class);
                startActivity(i);
            }
        });
        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(), Privacypolicy.class);
                startActivity(i);
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String new_p = new_pass.getText().toString();
                final String con_p = confirm_pass.getText().toString();

                if (TextUtils.isEmpty(new_p)) {
                    new_pass.setError("Enter your New Password");
                    new_pass.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(con_p)) {
                    confirm_pass.setError("Enter your Con");
                    confirm_pass.requestFocus();
                    return;
                }
                update_password(email,new_pass.getText().toString(),confirm_pass.getText().toString());
            }
        });
    }
    public void update_password(String email,String new_pass,String confirm_pass){
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        final JSONObject jsonBodyObj = new JSONObject();
//        String url = Config.BASEURL+"auth/updatePassword";
//        try{
//            jsonBodyObj.put("emailId", email);
//            jsonBodyObj.put("password", new_pass);
//            jsonBodyObj.put("confirmPassword", confirm_pass);
//        }catch (JSONException e){
//            e.printStackTrace();
//        }
//        final String requestBody = jsonBodyObj.toString();
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
//                url, null, new Response.Listener<JSONObject>(){
//            @Override    public void onResponse(JSONObject response) {
//                Log.e("ResponseBody",String.valueOf(response));
//                try {
//                    String statusCode = response.getString("statusCode");
//                    String message = response.getString("message");
//                    Log.e("ResponseBody",statusCode+" "+message);
//                    if (statusCode.equals("2000")){
//                        Intent i=new Intent(getApplicationContext(), LoginActivity.class);
//                        startActivity(i);
//                    }
//                    else if (statusCode.equals("2001")){
//                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
//                    }
//                    else if (statusCode.equals("2002")){
//                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
//                    }
//                }
//                catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override    public void onErrorResponse(VolleyError error) {
//                VolleyLog.e("Error: ", error.getMessage());
//                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
//            }
//        }){
//            @Override    public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                return headers;
//            }
//            @Override    public byte[] getBody() {
//                try {
//                    return requestBody == null ? null : requestBody.getBytes("utf-8");
//                } catch (UnsupportedEncodingException uee) {
//                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
//                            requestBody, "utf-8");
//                    return null;
//                }
//            }
//        };
//        requestQueue.add(jsonObjectRequest);
        /////////////////////////////////////////////////retrofit////////////////////////////////////////////////////
        if (Utility.checkConnectivity(getApplicationContext())) {
            UpdatePasswordValue passwordValue = new UpdatePasswordValue();
            passwordValue.setEmailId(email);
            passwordValue.setPassword(new_pass);
            passwordValue.setConfirmPassword(confirm_pass);
            Log.d("UpgradeSchool", "onClick: "+new Gson().toJson(passwordValue));
            Call<UpdatePasswordResult> call = APIClient.getInstance().UpdatePasswordResult(
                    passwordValue
            );
            call.enqueue(new Callback<UpdatePasswordResult>() {
                @Override
                public void onResponse(Call<UpdatePasswordResult> call, retrofit2.Response<UpdatePasswordResult> response) {
//                Log.e("DATA", " "+response.body().getStatusCode());
                    if (response.isSuccessful()){
                        String message = response.body().getMessage();
                        if (response.body().getStatusCode().equals("2000")){
                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(i);
                        }
                        else if (response.body().getStatusCode().equals("2001")){
                            Log.e("DATA", response.body().getMessage());
                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                        }
                        else if (response.body().getStatusCode().equals("2002")){
                            Log.e("DATA", response.body().getMessage());
                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Log.e("DATA", " "+response.message());
                    }

                }

                @Override
                public void onFailure(Call<UpdatePasswordResult> call, Throwable t) {

                }
            });
        }
        else {
            PopupClass.showPopUpWithTitleMessageOneButton(Confirm_Activity.this, "Ok", "", "Sorry, No Internet connection found, Please check your network connection", "", new PopupCallBackOneButton() {
                @Override
                public void onFirstButtonClick() {
                    finish();
                }
            });
        }


    }
}
