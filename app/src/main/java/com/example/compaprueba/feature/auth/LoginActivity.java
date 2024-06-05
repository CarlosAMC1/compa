package com.example.compaprueba.feature.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.compaprueba.R;
import com.example.compaprueba.feature.homepage.MainActivity;
import com.example.compaprueba.model.auth.AuthResponse;
import com.example.compaprueba.utils.ViewModelFactory;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 50;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton signInButton;
    private ProgressDialog progressDialog;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeUI();
        configureGoogleSignIn();
        setupViewModel();
    }

    private void initializeUI() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Cargando ...");
        progressDialog.setMessage("Iniciando Sesión ...");

        signInButton = findViewById(R.id.btn_GoogleInicio);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelFactory()).get(LoginViewModel.class);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                firebaseAuthWithGoogle(account.getIdToken());
            } else {
                showError("Google Sign-In Account is null");
            }
        } catch (ApiException e) {
            showError("Google sign in failed: " + e.getStatusCode());
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        if (idToken == null || idToken.isEmpty()) {
            showError("Error: idToken is empty");
            return;
        }

        progressDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.hide();
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                fetchFcmToken(user);
                            } else {
                                showError("Error: User is null");
                            }
                        } else {
                            showError("Authentication Failed: " + task.getException().getMessage());
                        }
                    }
                });
    }

    private void fetchFcmToken(FirebaseUser user) {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(token -> {
                    viewModel.login(createUserInfo(user, token)).observe(this, new Observer<AuthResponse>() {
                        @Override
                        public void onChanged(AuthResponse authResponse) {
                            handleAuthResponse(user, authResponse);
                        }
                    });
                })
                .addOnFailureListener(e -> showError("Failed to get FCM token"));
    }

    private UserInfo createUserInfo(FirebaseUser user, String token) {
        return new UserInfo(
                user.getUid(),
                user.getDisplayName(),
                user.getEmail(),
                user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null,
                null, // Cover URL can be set to null or provide the correct value
                token
        );
    }

    private void handleAuthResponse(FirebaseUser user, AuthResponse authResponse) {
        Toast.makeText(this, authResponse.getMessage(), Toast.LENGTH_SHORT).show();
        if (authResponse.getAuth() != null) {
            Toast.makeText(this, "Bienvenido, " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
            updateUI(user);
        } else {
            FirebaseAuth.getInstance().signOut();
            updateUI(null);
        }
    }

    private void showError(String message) {
        progressDialog.hide();
        signInButton.setVisibility(View.VISIBLE);
        Log.w(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            signInButton.setVisibility(View.VISIBLE);
        }
    }

    public static class UserInfo {
        String uid, name, email, profileUrl, coverUrl, userToken;

        public UserInfo(String uid, String name, String email, String profileUrl, String coverUrl, String userToken) {
            this.uid = uid;
            this.name = name;
            this.email = email;
            this.profileUrl = profileUrl;
            this.coverUrl = coverUrl;
            this.userToken = userToken;
        }
    }
}
