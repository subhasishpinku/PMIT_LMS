package com.dgc.photoediting;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.android.volley.toolbox.StringRequest;
import com.dgc.photoediting.PopUp.PopupCallBackOneButton;
import com.dgc.photoediting.PopUp.PopupClass;
import com.dgc.photoediting.setgetclass.APIClient;
import com.dgc.photoediting.setgetclass.Registration;
import com.dgc.photoediting.setgetclass.Result;
import com.dgc.photoediting.utility.Utility;
import com.dgc.photoediting.volley.VolleySingleton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class RegistrationActivity extends AppCompatActivity {
    Button signUpButton;
    TextView loginText;
    TextInputEditText nameEdit, mobileEdit, emailEdit, passwordEdit, dateEdit;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_screen);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        loginText = (TextView) findViewById(R.id.loginText);
        nameEdit = (TextInputEditText) findViewById(R.id.nameEdit);
        mobileEdit = (TextInputEditText) findViewById(R.id.mobileEdit);
        emailEdit = (TextInputEditText) findViewById(R.id.emailEdit);
        passwordEdit = (TextInputEditText) findViewById(R.id.passwordEdit);
        dateEdit = (TextInputEditText) findViewById(R.id.dateEdit);
        countryID = (AppCompatAutoCompleteTextView) findViewById(R.id.countryID);
        stateId = (AppCompatAutoCompleteTextView) findViewById(R.id.stateId);
        tram = (TextView) findViewById(R.id.tram);
        privacy = (TextView) findViewById(R.id.privacy);
        tram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Termsconditions.class);
                startActivity(i);
            }
        });
        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Privacypolicy.class);
                startActivity(i);
            }
        });
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
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>
//                (this,android.R.layout.select_dialog_item,country);
//        countryID.setThreshold(1);//will start working from first character
//        countryID.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
//        countryID.setTextColor(Color.BLUE);
        /////////////////////////////////////////////////////////
//        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>
//                (this,android.R.layout.select_dialog_item,state);
//        stateId.setThreshold(1);//will start working from first character
//        stateId.setAdapter(adapter1);//setting the adapter data into the AutoCompleteTextView
//        stateId.setTextColor(Color.BLUE);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.checkConnectivity(getApplicationContext())) {
                    signUp();
                }
                else {
                    PopupClass.showPopUpWithTitleMessageOneButton(RegistrationActivity.this, "Ok", "", "Sorry, No Internet connection found, Please check your network connection", "", new PopupCallBackOneButton() {
                        @Override
                        public void onFirstButtonClick() {
                            finish();
                        }
                    });
                }


            }
        });
        TextView textView = (TextView) findViewById(R.id.tram);
        SpannableString content = new SpannableString("Terms and conditions");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);

        TextView privacy = (TextView) findViewById(R.id.privacy);
        SpannableString content1 = new SpannableString("privacy policy");
        content1.setSpan(new UnderlineSpan(), 0, content1.length(), 0);
        privacy.setText(content1);
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
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
        dateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog date = new DatePickerDialog(RegistrationActivity.this, dobDate,
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

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.dgc.photoediting",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    private void updateDateFormat() {
        String myformat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myformat, Locale.US);
        dateEdit.setText(sdf.format(myDob.getTime()));
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

//    public void addListenerOnButton() {
//
//        radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);
//        btnDisplay = (Button) findViewById(R.id.btnDisplay);
//
//        btnDisplay.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                // get selected radio button from radioGroup
//                int selectedId = radioSexGroup.getCheckedRadioButtonId();
//
//                // find the radiobutton by returned id
//                radioSexButton = (RadioButton) findViewById(selectedId);
//
//                Toast.makeText(RegistrationActivity.this,
//                        radioSexButton.getText(), Toast.LENGTH_SHORT).show();
//
//            }
//
//        });
//
//    }

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
        countryID.setAdapter(new ArrayAdapter<String>(RegistrationActivity.this, android.R.layout.simple_spinner_dropdown_item, COUNTRY));
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
        stateId.setAdapter(new ArrayAdapter<String>(RegistrationActivity.this, android.R.layout.simple_spinner_dropdown_item, STATE));
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
    public void signUp(){
        final String name = nameEdit .getText().toString();
        final String mobileNo = mobileEdit.getText().toString();
        final String emailId = emailEdit.getText().toString();
        final String password = passwordEdit.getText().toString();
        final String dob = dateEdit.getText().toString();

        if (TextUtils.isEmpty(name)) {
            nameEdit.setError("Enter your Name");
            nameEdit.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(mobileNo)) {
            mobileEdit.setError("Enter your Number");
            mobileEdit.requestFocus();
            return;
        }
        if (mobileNo.length() < 10){
            mobileEdit.setError("Invalid Number");
            mobileEdit.requestFocus();
            return;
        }
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
//        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
//        if (emailId.matches(emailPattern))
//        {
//
//        }
        if (TextUtils.isEmpty(password)) {
            passwordEdit.setError("Enter your Password");
            passwordEdit.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(dob)) {
            dateEdit.setError("Enter your DOB");
            dateEdit.requestFocus();
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

    public void signUpNext(String name,String mobileNo,String emailId,String password,String dob,String gender, String cnty_name,String getSTATE_NAME){
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        final JSONObject jsonBodyObj = new JSONObject();
//        String url = Config.BASEURL+"auth/register";
//        try{
//            jsonBodyObj.put("name", name);
//            jsonBodyObj.put("mobileNo", mobileNo);
//            jsonBodyObj.put("emailId", emailId);
//            jsonBodyObj.put("password", password);
//            jsonBodyObj.put("dob", dob);
//            jsonBodyObj.put("gender", gender);
//            jsonBodyObj.put("country", cnty_name);
//            jsonBodyObj.put("state", getSTATE_NAME);
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
//                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
//                        JSONObject jsonObject = response.getJSONObject("payload");
////                        for (int i = 0;i<jsonArray.length();i++){
//                         //   JSONObject jsonObject = jsonArray.getJSONObject(i);
//                            String id = jsonObject.getString("id");
//                            String name = jsonObject.getString("name");
//                            String mobileNo  = jsonObject.getString("mobileNo");
//                            String emailId  = jsonObject.getString("emailId");
//                            String password = jsonObject.getString("password");
//                            String dob = jsonObject.getString("dob");
//                            String gender = jsonObject.getString("gender");
//                            String country = jsonObject.getString("country");
//                            String state = jsonObject.getString("state");
//                            Log.e("ResponseBody",id+" "+name+""+mobileNo+" "+emailId+" "+password+" "+dob+" "+gender+" "+country+" "+state);
//                                 Intent i=new Intent(getApplicationContext(), LoginActivity.class);
//                                startActivity(i);
//                       // }
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
        Registration registration= new Registration();
        registration.setName(name);
        registration.setMobileNo(mobileNo);
        registration.setEmailId(emailId);
        registration.setPassword(password);
        registration.setDob(dob);
        registration.setGender(gender);
        registration.setCountry(cnty_name);
        registration.setState(getSTATE_NAME);
        Log.d("UpgradeSchool", "onClick: "+new Gson().toJson(registration));
        Call<Result> call = APIClient.getInstance().upgradeSchoolData(
                registration
        );
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {
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
                        Log.e("DATAREgistration", id+" "+name+" "+mobileNo+" "+emailId+" "+password+" "+dob+" "+gender+" "+country+" "+state);
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
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });


    }
}