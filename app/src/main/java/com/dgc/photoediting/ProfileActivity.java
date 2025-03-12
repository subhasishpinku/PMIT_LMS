package com.dgc.photoediting;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.dgc.photoediting.PopUp.PopupCallBackOneButton;
import com.dgc.photoediting.PopUp.PopupClass;
import com.dgc.photoediting.setgetclass.UpdateProfileResult;
import com.dgc.photoediting.setgetclass.UpdateProfileValue;
import com.dgc.photoediting.setgetclass.UserProfile;
import com.dgc.photoediting.utility.Utility;
import com.dgc.photoediting.volley.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class ProfileActivity extends AppCompatActivity {
    EditText usernameId,contractID,emailID,passwordId,dobId;
    private Calendar myDob = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener dobDate;
    AppCompatAutoCompleteTextView countryID, stateId;
    String Country_Id = "";
    String STATE_ID = "";
    String cnty_id, state_id, state_name, getSTATE_ID;
    String cnty_name = "";
    String getSTATE_NAME= "";
    //    String[] country ={"India","Australia","Bangladesh","China","Germany","Iceland","Japan","Mexico"};
//    String[] state ={"Andhra Pradesh","Arunachal Pradesh","Assam","Bihar","Chhattisgarh","Goa","Gujarat","Haryana"};
    private RadioGroup radioSexGroup;
    private Button btnDisplay;
    private JSONArray result;
    private JSONArray state_result;
    String sid, scountry, sactiveStatus, message, statusCode, State_message, State_statusCode, state_countryID, state, activeStatus;
    ArrayList<String> COUNTRY;
    ArrayList<String> STATE;
    TextView tram, privacy;
    private RadioButton radioMale, radioFemale;
    String gender;
    Button savedetaildID;
    CircularImageView imageview_account_profile;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        String loginToken = SharedPrefManager.getInstance(getApplicationContext()).getKeyToken();
        Log.e("token",loginToken);
        usernameId = (EditText)findViewById(R.id.usernameId);
        contractID = (EditText)findViewById(R.id.contractID);
        emailID = (EditText)findViewById(R.id.emailID);
        passwordId = (EditText)findViewById(R.id.passwordId);
        dobId = (EditText)findViewById(R.id.dobId);
        countryID = (AppCompatAutoCompleteTextView) findViewById(R.id.countryID);
        stateId = (AppCompatAutoCompleteTextView) findViewById(R.id.stateId);
        savedetaildID = (Button)findViewById(R.id.savedetaildID);
        imageview_account_profile = (CircularImageView)findViewById(R.id.imageview_account_profile);
        countryID.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cnty_id = getID(position);
                cnty_name = (String) parent.getItemAtPosition(position);
                STATE.clear();
                state(cnty_id);
            }
        });
        stateId.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getSTATE_ID = getSTATEID(position);
                getSTATE_NAME = (String) parent.getItemAtPosition(position);
                //    Toast.makeText(parent.getContext(), "OFF" + getSTATE_ID + " " + " " + getSTATE_NAME, Toast.LENGTH_LONG).show();

            }
        });
        dobDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myDob.set(Calendar.YEAR, year);
                myDob.set(Calendar.MONTH, monthOfYear);
                myDob.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateFormat();
            }
        };
        dobId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog date = new DatePickerDialog(ProfileActivity.this, dobDate,
                        myDob.get(Calendar.YEAR),
                        myDob.get(Calendar.MONTH),
                        myDob.get(Calendar.DAY_OF_MONTH));

                date.getDatePicker().setMaxDate(System.currentTimeMillis());
                date.show();

            }
        });
        COUNTRY = new ArrayList<>();
        STATE = new ArrayList<>();
        country();
        radioSexGroup = (RadioGroup) findViewById(R.id.radioSexGroup);
        radioMale = (RadioButton) findViewById(R.id.radioMale);
        radioFemale = (RadioButton) findViewById(R.id.radioFemale);
        radioSexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioMale) {
                    gender = "male";
                } else if (checkedId == R.id.radioFemale) {
                    gender = "female";
                } else {
                    Toast.makeText(getApplicationContext(), "No Selected",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        getprofile(loginToken);
        savedetaildID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileUpdate();
            }
        });
    }
    public void getprofile(String loginToken){
        if (Utility.checkConnectivity(getApplicationContext())) {
            Call<UserProfile> call = ApiClientToken.getInstance().getUser(
            );
            call.enqueue(new Callback<UserProfile>() {
                @Override
                public void onResponse(Call<UserProfile> call, retrofit2.Response<UserProfile> response) {
                    Log.e("FetchUser", ""+response.body());
                    if (response.isSuccessful()){
                        if (response.body().getStatusCode().equals("2000")){
                             Log.e("FetchUser", response.body().getPayloaduser().getId()+"");
                             String id = response.body().getPayloaduser().getId();
                             String name = response.body().getPayloaduser().getName();
                            String mobileno = response.body().getPayloaduser().getMobileNo();
                            String emailid = response.body().getPayloaduser().getEmailId();
                            String password = response.body().getPayloaduser().getPassword();
                            String dob = response.body().getPayloaduser().getDob();
                            String gender = response.body().getPayloaduser().getGender();
                            String country = response.body().getPayloaduser().getCountry();
                            String state = response.body().getPayloaduser().getState();
                            String provider = response.body().getPayloaduser().getProvider();
                            String ImageUrl = response.body().getPayloaduser().getImageUrl();
//                            Glide.with(getApplicationContext())
//                                    .load(ImageUrl)
//                                    .into(imageview_account_profile);
                            ImageLoader imageLoader = PhotoApp.getInstance().getImageLoader();
                            imageview_account_profile.setImageUrl(ImageUrl,imageLoader);
                            Log.e("FetchUserPROFILE", response.body().getPayloaduser().getImageUrl()+"");
                            usernameId.setText(name);
                            contractID.setText(mobileno);
                            emailID.setText(emailid);
                            passwordId.setText(password);
                            dobId.setText(dob);
                        }
                        else if (response.body().getStatusCode().equals("2001")){
                            Log.e("FetchUser", response.body().getMessage());
                        }
                        else if (response.body().getStatusCode().equals("2002")){
                            Log.e("FetchUser", response.body().getMessage());
                        }
                    }
                    else {
                        Log.e("DATA", " "+response.message());
                    }

                }

                @Override
                public void onFailure(Call<UserProfile> call, Throwable t) {

                }
            });
        }
        else {
            PopupClass.showPopUpWithTitleMessageOneButton(ProfileActivity.this, "Ok", "", "Sorry, No Internet connection found, Please check your network connection", "", new PopupCallBackOneButton() {
                @Override
                public void onFirstButtonClick() {
                    finish();
                }
            });
        }

    }
    private void updateDateFormat() {
        String myformat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myformat, Locale.US);
        dobId.setText(sdf.format(myDob.getTime()));
        // dateEdit.setText(Integer.toString(calculateAge(myDob.getTimeInMillis())));
    }

    private int calculateAge(long date) {
        Calendar dob = Calendar.getInstance();
        dob.setTimeInMillis(date);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)) {
            age--;
        }
        return age;
    }

    public void country() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.BASEURL + "master/get_country",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //   progressBar.setVisibility(View.GONE);
                        Log.e("SP11", response);
                        JSONObject j = null;
                        try {
                            j = new JSONObject(response);
                            statusCode = j.getString("statusCode");
                            message = j.getString("message");
                            Log.e("SPDATA", " " + statusCode + " " + message);
                            result = j.getJSONArray(Config.JSON_ARRAY);
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject jsonObject = result.getJSONObject(i);
                                sid = jsonObject.getString("id");
                                scountry = jsonObject.getString("country");
                                sactiveStatus = jsonObject.getString("activeStatus");
                                Log.e("SPDATA", " " + sid + " " + scountry + " " + sactiveStatus);
//                                db.districNameInsert(disID,distname);
                            }
                            getcountry(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton volleySingleton = VolleySingleton.getInstance(this);
        stringRequest.setShouldCache(false);
        volleySingleton.addToRequestQueue(stringRequest);

    }

    private void getcountry(JSONArray j) {
        for (int i = 0; i < j.length(); i++) {
            try {
                JSONObject json = j.getJSONObject(i);
                COUNTRY.add(json.getString(Config.TAG_COUNTRY));
                Log.e("COUNTRY", " " + COUNTRY);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        countryID.setAdapter(new ArrayAdapter<String>(ProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, COUNTRY));
    }

    private String getID(int position) {
        try {
            JSONObject json = result.getJSONObject(position);
            Country_Id = json.getString(Config.TAG_ID);
            Log.e("ide", " " + Country_Id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Country_Id;
    }

    public void state(String cnty_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.BASEURL + "master/get_state?countryId=" + cnty_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(response);
                            State_message = j.getString("statusCode");
                            State_statusCode = j.getString("message");
                            Log.e("state", " " + State_message + " " + State_statusCode);
                            state_result = j.getJSONArray(Config.JSON_ARRAY);
                            for (int i = 0; i < state_result.length(); i++) {
                                JSONObject jsonObject = state_result.getJSONObject(i);
                                state_id = jsonObject.getString("id");
                                state_countryID = jsonObject.getString("countryId");
                                state = jsonObject.getString("state");
                                activeStatus = jsonObject.getString("activeStatus");
                                Log.e("STATE_VALUE", " " + state_id + " " + state_countryID + " " + state + " " + activeStatus);

                            }
                            getstate(state_result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
//                params.put("countryId", cnty_id);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton volleySingleton = VolleySingleton.getInstance(this);
        stringRequest.setShouldCache(false);
        volleySingleton.addToRequestQueue(stringRequest);

    }

    private void getstate(JSONArray j) {
        for (int i = 0; i < j.length(); i++) {
            try {
                JSONObject json = j.getJSONObject(i);
                STATE.add(json.getString(Config.STATE));
                Log.e("STATE", " " + STATE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        stateId.setAdapter(new ArrayAdapter<String>(ProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, STATE));
    }

    private String getSTATEID(int position) {
        try {
            JSONObject json = state_result.getJSONObject(position);
            STATE_ID = json.getString(Config.STATE_ID);
            Log.e("state_id", " " + STATE_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return STATE_ID;
    }
    public void ProfileUpdate(){
       // EditText usernameId,contractID,emailID,passwordId,dobId;
        final String name = usernameId .getText().toString();
        final String mobileNo = contractID.getText().toString();
        final String emailId = emailID.getText().toString();
        final String password = passwordId.getText().toString();
        final String dob = dobId.getText().toString();

        if (TextUtils.isEmpty(name)) {
            usernameId.setError("Enter your Name");
            usernameId.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(mobileNo)) {
            contractID.setError("Enter your Number");
            contractID.requestFocus();
            return;
        }
        if (mobileNo.length() < 10){
            contractID.setError("Invalid Number");
            contractID.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(emailId)) {
            emailID.setError("Enter your Email");
            emailID.requestFocus();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailId).matches()) {
            emailID.setError("Enter a valid email");
            emailID.requestFocus();
            return;
        }
//        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
//        if (emailId.matches(emailPattern))
//        {
//
//        }
        if (TextUtils.isEmpty(password)) {
            passwordId.setError("Enter your Password");
            passwordId.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(dob)) {
            dobId.setError("Enter your DOB");
            dobId.requestFocus();
            Toast.makeText(getApplicationContext(),"Enter your DOB",Toast.LENGTH_SHORT).show();
            return;
        }
        if (cnty_name.equals("")){
            Toast.makeText(getApplicationContext(),"Select Country",Toast.LENGTH_SHORT).show();
        }
        else {
            if (getSTATE_NAME.equals("")){
                Toast.makeText(getApplicationContext(),"Select State",Toast.LENGTH_SHORT).show();
            }
            else {
                if(radioMale.isChecked() || radioFemale.isChecked())
                {
                    signUpNext(name,mobileNo,emailId,password,dob,gender,cnty_name,getSTATE_NAME);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Check Gender",Toast.LENGTH_SHORT).show();
                }

            }
        }
    }
    public void signUpNext(String name,String mobileNo,String emailId,String password,String dob,String gender,String cnty_name,String getSTATE_NAME){
        UpdateProfileValue registration= new UpdateProfileValue();
        registration.setName(name);
        registration.setMobileNo(mobileNo);
        registration.setEmailId(emailId);
        registration.setPassword(password);
        registration.setDob(dob);
        registration.setGender(gender);
        registration.setCountry(cnty_name);
        registration.setState(getSTATE_NAME);
        Log.d("UpgradeSchool", "onClick: "+new Gson().toJson(registration));
        Call<UpdateProfileResult> call = ApiClientToken.getInstance().upgradeProfile(
                registration
        );
        call.enqueue(new Callback<UpdateProfileResult>() {
            @Override
            public void onResponse(Call<UpdateProfileResult> call, retrofit2.Response<UpdateProfileResult> response) {
//                Log.e("DATA", " "+response.body().getStatusCode());
                if (response.isSuccessful()){
                    if (response.body().getStatusCode().equals("2000")){
                        String message = response.body().getMessage();
                        String id = response.body().getPayload().getId();
                        String name = response.body().getPayload().getName();
                        String mobileNo  = response.body().getPayload().getMobileNo();
                        String emailId  = response.body().getPayload().getEmailId();
                        String password = response.body().getPayload().getPassword();
                        String dob = response.body().getPayload().getDob();
                        String gender = response.body().getPayload().getGender();
                        String country = response.body().getPayload().getCountry();
                        String state = response.body().getPayload().getState();
                        String provider = response.body().getPayload().getProvider();
                        String imageurl = response.body().getPayload().getImageUrl();
                        Log.e("DATAREgistration", id+" "+name+" "+mobileNo+" "
                                +emailId+" "+password+" "+dob+" "+gender+" "+country+" "+state+" "+provider+" "+imageurl);
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(getApplicationContext(), TopActivity.class);
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
            public void onFailure(Call<UpdateProfileResult> call, Throwable t) {

            }
        });

    }
}
