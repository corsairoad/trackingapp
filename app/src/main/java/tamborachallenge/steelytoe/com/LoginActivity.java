package tamborachallenge.steelytoe.com;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import tamborachallenge.steelytoe.com.common.Impl.CrudTempUser;
import tamborachallenge.steelytoe.com.model.TempUser;

import butterknife.BindView;
import butterknife.ButterKnife;

//import com.google.android.gms.auth.api.Auth;
//import com.google.android.gms.auth.api.signin.GoogleSignInResult;

/**
 * Created by hadi on 07/02/2017.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.password)
    EditText password;

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 0;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    private SignInButton btnSignInGoogle;
    private Button btnSignOut;
    private LinearLayout llProfileLayout;
    private ImageView imgProfilePic;
    private TextView txtName, txtEmail;

    private CrudTempUser crudTempUser  = new CrudTempUser(this);
    private TempUser tempUser = new TempUser();

    String personName = null, email = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        btnSignInGoogle = (SignInButton) findViewById(R.id.btn_sign_in_google);
        btnSignOut = (Button) findViewById(R.id.btn_sign_out);

        llProfileLayout = (LinearLayout) findViewById(R.id.llProfile);
        imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);

        btnSignInGoogle.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();

        // Customizing G+ button
        btnSignInGoogle.setSize(SignInButton.SIZE_STANDARD);
        btnSignInGoogle.setScopes(gso.getScopeArray());
    }


    @Override
    public void onStart() {
        super.onStart();

//        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
//        if (opr.isDone()) {
//            Log.d(TAG, "Got cached sign-in");
//            GoogleSignInResult result = opr.get();
//            handleSignInResult(result);
//        } else {
//            showProgressDialog();
//            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(GoogleSignInResult googleSignInResult) {
//                    hideProgressDialog();
//                    handleSignInResult(googleSignInResult);
//                }
//            });
//        }
    }


    private void signIn() {
//        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            handleSignInResult(result);
//        }
    }

    private void signOut() {
//        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
//        new ResultCallback<Status>() {
//            @Override
//            public void onResult(Status status) {
//                updateUI(false, personName, email);
//            }
//        });
    }


//    private void handleSignInResult(GoogleSignInResult result) {
//        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
//
//        if (result.isSuccess()) {
//            // Signed in successfully, show authenticated UI.
//            GoogleSignInAccount acct = result.getSignInAccount();
//            personName = acct.getDisplayName();
//            email = acct.getEmail();

//            Log.e(TAG, "display name: " + acct.getDisplayName());

//            String personPhotoUrl = acct.getPhotoUrl().toString();

//            Log.e(TAG, "Name: " + personName + ", email: " + email
//                    + ", Image: " );

//            checkEmail(email);
//            if(checkEmail(email) != null){
//                Log.d("Check Email ADA ", " - " + checkEmail(email) );
//
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);
//                finish();
//            } else {
//                // Insert Data
//                insertToSQLiteDatabase(personName,email);
//
//            }


//            txtName.setText(personName);
//            txtEmail.setText(email);
//            Glide.with(getApplicationContext()).load(personPhotoUrl)
//                    .thumbnail(0.5f)
//                    .crossFade()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(imgProfilePic);

//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            intent.putExtra("name", personName);
//            intent.putExtra("email", email);
//            intent.putExtra("photo", personPhotoUrl);
//            startActivity(intent);
//            finish();

//            updateUI(true, personName, email);
//        } else {
//            // Signed out, show unauthenticated UI.
//            updateUI(false, personName, email);
//        }
//    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_sign_in_google:
                signIn();
                break;

            case R.id.btn_sign_out:
                signOut();
                break;

        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
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

    private void updateUI(boolean isSignedIn, String nama, String email) {
        if (isSignedIn) {
//            btnSignInGoogle.setVisibility(View.GONE);
//            btnSignOut.setVisibility(View.VISIBLE);
//            llProfileLayout.setVisibility(View.VISIBLE);

            //check email
            tempUser = crudTempUser.getUserByEmail(email);
            String emailUser = tempUser.email_user;

            if(emailUser != null){ // email sudah ada
                Log.d(TAG, "Email sudah ada");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }else{
                Log.d(TAG, "Email belum ada");
                String emailUserBaru;
                tempUser.nama_user = nama;
                tempUser.email_user = email;

                emailUserBaru = crudTempUser.insert(tempUser);
                checkEmailbaru(emailUserBaru);
                Log.d(TAG,"email baru sudah masuk" + checkEmailbaru(emailUserBaru));
            }

        } else {
//            btnSignInGoogle.setVisibility(View.VISIBLE);
//            btnSignOut.setVisibility(View.GONE);
//            llProfileLayout.setVisibility(View.GONE);
        }
    }


    /// ================================================================= PROSES CHECK USER
    public final String checkEmailbaru(String email){
        tempUser = crudTempUser.getUserByEmail(email);
        String emailUser = tempUser.email_user;
        return emailUser;
    }



}
