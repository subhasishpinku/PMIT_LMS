package com.dgc.photoediting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dgc.photoediting.PopUp.PopupCallBackOneButton;
import com.dgc.photoediting.PopUp.PopupClass;
import com.dgc.photoediting.setgetclass.APIClient;
import com.dgc.photoediting.setgetclass.LoginResult;
import com.dgc.photoediting.setgetclass.LoginToken;
import com.dgc.photoediting.setgetclass.LoginValue;
import com.dgc.photoediting.setgetclass.SocialResult;
import com.dgc.photoediting.setgetclass.Socialvalue;
import com.dgc.photoediting.utility.Utility;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;

public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {
    Button loginButton;
    TextView tram,privacy;
    TextInputEditText mmnumber,userName,ppassword;
    TextView forgotPasswordText;
    private LoginButton Fbtn_sign_in;
    private SignInButton Gbtn_sign_in;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private Button btnSignOut;
    private CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.login_screen);
        Gbtn_sign_in = (SignInButton)findViewById(R.id.Gbtn_sign_in);
        Fbtn_sign_in = (LoginButton)findViewById(R.id.Fbtn_sign_in);
        btnSignOut = (Button) findViewById(R.id.btn_sign_out);
        loginButton = (Button)findViewById(R.id.loginButton);
        tram = (TextView)findViewById(R.id.tram);
        privacy = (TextView)findViewById(R.id.privacy);
        mmnumber = (TextInputEditText) findViewById(R.id.mmnumber);
        userName = (TextInputEditText) findViewById(R.id.userName);
        ppassword = (TextInputEditText) findViewById(R.id.ppassword);
        forgotPasswordText = (TextView)findViewById(R.id.forgotPasswordText);
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
                Intent ii= new Intent(getApplicationContext(), Privacypolicy.class);
                startActivity(ii);
            }
        });
        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iii=new Intent(getApplicationContext(), ForgotPassword_Activity.class);
                startActivity(iii);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                startActivity(i);
                 String phone = mmnumber.getText().toString().trim();
                 String username = userName.getText().toString().trim();
                 String pass = ppassword.getText().toString().trim();
                 int phonee = mmnumber.getText().length();
                 int usernamee = userName.getText().length();
                int passs = ppassword.getText().length();
                if (phonee>0 && usernamee>0){
                    Toast.makeText(getApplicationContext(), "Please Enter Mobile No Or Username and password", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (phonee>0) {
                        if (phonee < 10) {
                            mmnumber.setError("Please enter Valid Mobile No");
                            mmnumber.requestFocus();
                            return;
                        }
                        else {
                            username = "";
                            pass = "";
                            login(phone,username,pass);
                        }
                    }
                    else {
                        if (usernamee>0 || passs>0) {
                            if (TextUtils.isEmpty(username)) {
                                userName.setError("Enter your UserName");
                                userName.requestFocus();
                                return;
                            } else {
                                //ForgetPassword(username,email);
                                if (TextUtils.isEmpty(pass)) {
                                    ppassword.setError("Enter your password");
                                    ppassword.requestFocus();
                                    return;
                                } else {
                                    phone = "";
                                    login(phone,username,pass);


                                }

                            }
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Please Enter Mobile No Or Username and password", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        Gbtn_sign_in.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);
        Fbtn_sign_in.setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Customizing G+ button
        Gbtn_sign_in.setSize(SignInButton.SIZE_STANDARD);
        Gbtn_sign_in.setScopes(gso.getScopeArray());
    }

    public void login(String phone,String usernamee,String pass){
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        final JSONObject jsonBodyObj = new JSONObject();
//        String url = Config.BASEURL+"auth/authenticate";
//        try{
//            jsonBodyObj.put("phone", phone);
//            jsonBodyObj.put("userName", usernamee);
//            jsonBodyObj.put("password", pass);
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
//                        JSONObject jsonObject = response.getJSONObject("payload");
//                        //for (int i = 0;i<jsonArray.length();i++){
//                          //  JSONObject jsonObject = jsonArray.getJSONObject(i);
//                            String accessToken = jsonObject.getString("accessToken");
//                            Log.e("ResponseBody1",accessToken);
//                  //      }
//                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
//                        Intent i=new Intent(getApplicationContext(), NavigationDrawerActivity.class);
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
//

        /////////////////////////////////////////////////retrofit////////////////////////////////////////////////////
        if (Utility.checkConnectivity(getApplicationContext())) {
            LoginValue loginValue = new LoginValue();
            loginValue.setPhone(phone);
            loginValue.setUserName(usernamee);
            loginValue.setPassword(pass);
            Log.d("UpgradeSchool", "onClick: "+new Gson().toJson(loginValue));
            Call<LoginResult> call = APIClient.getInstance().loginResultCall(
                    loginValue
            );
            call.enqueue(new Callback<LoginResult>() {
                @Override
                public void onResponse(Call<LoginResult> call, retrofit2.Response<LoginResult> response) {
//                Log.e("DATA", " "+response.body().getStatusCode());
                    if (response.isSuccessful()){
                        String message = response.body().getMessage();
                        if (response.body().getStatusCode().equals("2000")){
                            String accessToken = response.body().getPayload().getAccessToken();
                            Log.e("accessToken",accessToken);
                            LoginToken loginToken = new LoginToken(
                                    accessToken);
                            SharedPrefManager.getInstance(getApplicationContext()).userToken(loginToken);
                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                            startActivity(i);
                        }
                        else if (response.body().getStatusCode().equals("2001")){
                          //  Log.e("DATA", response.body().getMessage());
                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                        }
                        else if (response.body().getStatusCode().equals("2002")){
                          //  Log.e("DATA", response.body().getMessage());
                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Log.e("DATA", " "+response.message());
                    }

                }

                @Override
                public void onFailure(Call<LoginResult> call, Throwable t) {

                }
            });

        }
        else {
            PopupClass.showPopUpWithTitleMessageOneButton(LoginActivity.this, "Ok", "", "Sorry, No Internet connection found, Please check your network connection", "", new PopupCallBackOneButton() {
                @Override
                public void onFirstButtonClick() {
                    finish();
                }
            });
        }

    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.e("TAG", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.e("TAG", "display name: " + acct.getDisplayName());
            String personName = acct.getDisplayName();
            Uri photoUrl = acct.getPhotoUrl();
            String personPhotoUrl = String.valueOf(photoUrl);
           // String personPhotoUrl = acct.getPhotoUrl().toString();
            String email = acct.getEmail();
            String familyName = acct.getFamilyName();
            String givenName = acct.getGivenName();
            String token = acct.getIdToken();
            String id = acct.getId();
            String account = String.valueOf(acct.getAccount());
            String serverAuthCode = acct.getServerAuthCode();
            String grantedScopes = String.valueOf(acct.getGrantedScopes());
            String requestedScopes = String.valueOf(acct.getRequestedScopes());
            Log.e("TAG", "Name: " + personName + ", email: " + email
                    + ", Image: " + personPhotoUrl+ " "+  ",familyName" +" "+familyName+" "+
                    "givenName"+" "+givenName+" "
                    +",token"+" "+token+" "+",id"+" "
                    +id+""+",account"+account+" "+",serverAuthCode"
                    +serverAuthCode+" "+",grantedScopes"+grantedScopes+" "+",requestedScopes"+requestedScopes);
//            txtName.setText(personName);
//            txtEmail.setText(email);
//            Glide.with(getApplicationContext()).load(personPhotoUrl)
//                    .thumbnail(0.5f)
//                    .crossFade()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(imgProfilePic);
            socialLogin(personName,email,personPhotoUrl,"GOOGLE");
            Log.e("social-G","  "+"personName"+"  "+personName+"   "+"email"+email+"  "+"personPhotoUrl"+"   "+personPhotoUrl);
            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.Gbtn_sign_in:
                if (Utility.checkConnectivity(getApplicationContext())) {
                    signIn();
                }
                else {
                    PopupClass.showPopUpWithTitleMessageOneButton(LoginActivity.this, "Ok", "", "Sorry, No Internet connection found, Please check your network connection", "", new PopupCallBackOneButton() {
                        @Override
                        public void onFirstButtonClick() {
                            finish();
                        }
                    });
                }

                break;

            case R.id.btn_sign_out:
                if (Utility.checkConnectivity(getApplicationContext())) {
                    signOut();
                    revokeAccess();
                }
                else {
                    PopupClass.showPopUpWithTitleMessageOneButton(LoginActivity.this, "Ok", "", "Sorry, No Internet connection found, Please check your network connection", "", new PopupCallBackOneButton() {
                        @Override
                        public void onFirstButtonClick() {
                            finish();
                        }
                    });
                }
                break;
            case R.id.Fbtn_sign_in:
                onFblogin();
                break;
            default:

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            int statusCode = result.getStatus().getStatusCode();
            Log.e("TAG",""+statusCode);
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.e("TAG", "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.e("TAG", "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            Gbtn_sign_in.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.VISIBLE);
//            btnRevokeAccess.setVisibility(View.VISIBLE);
            //llProfileLayout.setVisibility(View.VISIBLE);
        } else {
            Gbtn_sign_in.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.GONE);
//            btnRevokeAccess.setVisibility(View.GONE);
           // llProfileLayout.setVisibility(View.GONE);
        }
    }
    private void onFblogin()
    {
        callbackManager = CallbackManager.Factory.create();
        Fbtn_sign_in.setReadPermissions(Arrays.asList("public_profile", "email"));
//        LoginManager.getInstance().registerCallback(callbackManager,
//                new FacebookCallback<LoginResult>() {
        Fbtn_sign_in.registerCallback(callbackManager, new FacebookCallback<com.facebook.login.LoginResult>() {
            @Override
            public void onSuccess(com.facebook.login.LoginResult loginResult) {

                Log.e("TAG","Success"+"00");
                System.out.println("Success");
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject json, GraphResponse response) {
                                if (response.getError() != null) {
                                    System.out.println("ERROR");
                                    Log.e("TAG","Success"+"0");
                                } else {
                                    Log.e("TAG","Success"+"1");
                                    System.out.println("Success");
                                    try {
                                        String jsonresult = String.valueOf(json);
                                        System.out.println("TAG"+jsonresult+"DATA");
                                        String facebookID = json.getString("id");
                                        String name = json.getString("name");
                                        String email = json.getString("email");
                                        //String gender = json.getString("gender");
                                        String token = loginResult.getAccessToken().getToken();
                                        String applicationId = loginResult.getAccessToken().getApplicationId();
                                        Log.e("Facebook","Success"+facebookID+" "+name+" "+email+" "+" "+"token"+" "
                                                +token+" "+"applicationId"+" "+applicationId);
                                        Profile profile = Profile.getCurrentProfile();
                                        String id = profile.getId();
                                        String link = profile.getLinkUri().toString();
                                        Log.e("Facebook",link+" "+"id"+" "+id);
                                        if (Profile.getCurrentProfile()!=null)
                                        {
                                            Log.e("Facebook", "ProfilePic" + Profile.getCurrentProfile().getProfilePictureUri(200, 200));
                                            socialLogin(name,email, String.valueOf(Profile.getCurrentProfile().getProfilePictureUri(200, 200)),"Facebook");
                                            Log.e("social-F","  "+"personName"+"  "+name+"   "+"email"+email+"  "+"personPhotoUrl"+"   "+String.valueOf(Profile.getCurrentProfile().getProfilePictureUri(200, 200)));

                                        }
//                                                Intent intent = new Intent(MainActivity.this,CalculatorActivity.class);
//                                                startActivity(intent);
                                      //
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }

                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.e("Cancel",""+"");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("Error",error.toString());
            }
        });
    }
  public void socialLogin(String name,String email,String ink,String provider){
      if (Utility.checkConnectivity(getApplicationContext())) {
          Socialvalue loginValue = new Socialvalue();
          loginValue.setName(name);
          loginValue.setEmail(email);
          loginValue.setImageUrl(ink);
          loginValue.setProvider(provider);
          Log.e("social-M", "onClick: "+new Gson().toJson(loginValue));
          Call<SocialResult> call = APIClient.getInstance().socialResultCall(
                  loginValue
          );
          call.enqueue(new Callback<SocialResult>() {
              @Override
              public void onResponse(Call<SocialResult> call, retrofit2.Response<SocialResult> response) {
//                Log.e("DATA", " "+response.body().getStatusCode());
                  if (response.isSuccessful()){
                      String message = response.body().getMessage();
                      if (response.body().getStatusCode().equals("2000")){
                          String accessToken = response.body().getPayload().getAccessToken();
                          Log.e("accessTokenSocal",accessToken);
                          LoginToken loginToken = new LoginToken(
                                  accessToken);
                          SharedPrefManager.getInstance(getApplicationContext()).userToken(loginToken);
                          Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                          Intent i=new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                          startActivity(i);
                      }
                      else if (response.body().getStatusCode().equals("2001")){
                          Log.e("DATA11", response.body().getMessage());
                          Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                      }
                      else if (response.body().getStatusCode().equals("2002")){
                          Log.e("DATA22", response.body().getMessage());
                          Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();


                      }
                  }
                  else {
                      Log.e("DATA", " "+response.message());
                  }

              }

              @Override
              public void onFailure(Call<SocialResult> call, Throwable t) {

              }
          });

      }
      else {
          PopupClass.showPopUpWithTitleMessageOneButton(LoginActivity.this, "Ok", "", "Sorry, No Internet connection found, Please check your network connection", "", new PopupCallBackOneButton() {
              @Override
              public void onFirstButtonClick() {
                  finish();
              }
          });
      }
  }
}