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
import com.dgc.photoediting.setgetclass.CheckuseremailResult;
import com.dgc.photoediting.utility.Utility;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;

public class ForgotPassword_Activity extends AppCompatActivity {
    TextInputEditText emailEdit;
    Button validedId;
    TextView tram,privacy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpassword_activity);
        emailEdit = (TextInputEditText)findViewById(R.id.emailEdit);
        validedId = (Button)findViewById(R.id.validedId);
        tram = (TextView)findViewById(R.id.tram);
        privacy = (TextView)findViewById(R.id.privacy);
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
        validedId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailId = emailEdit.getText().toString();
                if (TextUtils.isEmpty(emailId)) {
                    emailEdit.setError("Enter your Email");
                    emailEdit.requestFocus();
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailId).matches()) {
                    emailEdit.setError("Enter a valid email");
                    emailEdit.requestFocus();
                    return;
                }
                forgotpass_valid(emailId);
            }
        });
    }
    public void forgotpass_valid(String email){
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.BASEURL + "auth/check_user_email/"+email,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        JSONObject j = null;
//                        try {
//                            j = new JSONObject(response);
//                            String statusCode = j.getString("statusCode");
//                            String message = j.getString("message");
//                            if (statusCode.equals("2000")){
//                                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
//                                Intent ii=new Intent(ForgotPassword_Activity.this, Confirm_Activity.class);
//                                ii.putExtra("email", email);
//                                startActivity(ii);
//                            }
//                            else if (statusCode.equals("2001")){
//                                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
//                            }
//                            else if (statusCode.equals("2002")){
//                                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
////                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                return params;
//            }
//        };
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        VolleySingleton volleySingleton = VolleySingleton.getInstance(this);
//        stringRequest.setShouldCache(false);
//        volleySingleton.addToRequestQueue(stringRequest);
        ///////////////////////////////////////////////retrofit//////////////////////////////////////
        if (Utility.checkConnectivity(getApplicationContext())) {
            Call<CheckuseremailResult> call = APIClient.getInstance().getCheckuseremailResultCall(email);
            call.enqueue(new Callback<CheckuseremailResult>() {
                @Override
                public void onResponse(Call<CheckuseremailResult> call, retrofit2.Response<CheckuseremailResult> response) {
                    if (response.isSuccessful()){
                        String message = response.body().getMessage();
                        if (response.body().getStatusCode().equals("2000")){
                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                            Intent ii=new Intent(ForgotPassword_Activity.this, Confirm_Activity.class);
                            ii.putExtra("email", email);
                            startActivity(ii);
                        }
                        else if (response.body().getStatusCode().equals("2001")){
                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                        }
                        else if (response.body().getStatusCode().equals("2002")){
                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                        }
                        else if (response.body().getStatusCode().equals("2003")){
                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Log.e("DATA", " "+response.message());
                    }
                }
                @Override
                public void onFailure(Call<CheckuseremailResult> call, Throwable t) {

                }
            });
        }
        else {
            PopupClass.showPopUpWithTitleMessageOneButton(ForgotPassword_Activity.this, "Ok", "", "Sorry, No Internet connection found, Please check your network connection", "", new PopupCallBackOneButton() {
                @Override
                public void onFirstButtonClick() {
                    finish();
                }
            });
        }
    }
}
