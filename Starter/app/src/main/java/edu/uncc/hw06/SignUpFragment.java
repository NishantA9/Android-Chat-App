package edu.uncc.hw06;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import edu.uncc.hw06.databinding.FragmentSignUpBinding;

public class SignUpFragment extends Fragment {
    public SignUpFragment() {// Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    FragmentSignUpBinding binding;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();}
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.login();
            }});
        binding.buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.editTextName.getText().toString();
                String email = binding.editTextEmail.getText().toString();
                String password = binding.editTextPassword.getText().toString();
                if(name.isEmpty()){
                    new AlertDialog.Builder(getContext())
                            .setTitle("Error")
                            .setMessage("Enter a Valid Name!!!")
                            .setPositiveButton(android.R.string.ok, null).show();
                } else if(email.isEmpty()){
                    new AlertDialog.Builder(getContext())
                            .setTitle("Error")
                            .setMessage("Enter a Valid Email!!!")
                            .setPositiveButton(android.R.string.ok, null).show();
                } else if (password.isEmpty()){
                    new AlertDialog.Builder(getContext())
                            .setTitle("Error")
                            .setMessage("Enter a Valid Password!!!")
                            .setPositiveButton(android.R.string.ok, null).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();
                                FirebaseAuth.getInstance().getCurrentUser()
                                        .updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                    HashMap<String, Object> data = new HashMap<>();
                                                    data.put("name", name);
                                                    data.put("uid", user.getUid());
                                                    FirebaseFirestore.getInstance().collection("users")
                                                            .document(user.getUid())
                                                            .set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                mListener.authSuccessful();
                                                            } else {
                                                                new AlertDialog.Builder(getContext())
                                                                        .setTitle("Error!")
                                                                        .setMessage(task.getException().getLocalizedMessage())
                                                                        .setPositiveButton(android.R.string.ok, null).show();}}});
                                                } else {new AlertDialog.Builder(getContext())
                                                        .setTitle("Error!")
                                                        .setMessage(task.getException().getLocalizedMessage())
                                                        .setPositiveButton(android.R.string.ok, null).show();}}});
                            } else {new AlertDialog.Builder(getContext())
                                        .setTitle("Error!")
                                        .setMessage(task.getException().getLocalizedMessage())
                                        .setPositiveButton(android.R.string.ok, null).show();}}});}}});
        getActivity().setTitle(R.string.create_account_label);}
    SignUpListener mListener;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (SignUpListener) context;}
    interface SignUpListener {
        void login();
        void authSuccessful();}}