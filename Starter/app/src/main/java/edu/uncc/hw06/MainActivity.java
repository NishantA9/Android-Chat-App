package edu.uncc.hw06;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener, SignUpFragment.SignUpListener,
    ChatsFragment.ForumsListener, CreateChatFragment.CreateForumListener, ChatFragment.ChatFragmentlistener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.rootView, new LoginFragment())
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.rootView, new ChatsFragment())
                    .commit();}}
    @Override
    public void createNewAccount() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new SignUpFragment())
                .commit();}
    @Override
    public void login() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new LoginFragment())
                .commit();}
    @Override
    public void authSuccessful() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new ChatsFragment())
                .commit();}
    @Override
    public void createNewChat() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new CreateChatFragment())
                .addToBackStack(null)
                .commit();}
    @Override
    public void logout() {
        FirebaseAuth.getInstance().signOut();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new LoginFragment())
                .commit();}
    @Override
    public void gotoChat(Chat chat) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, ChatFragment.newInstance(chat))
                .addToBackStack(null)
                .commit();}
    @Override
    public void cancelCreateChat() {
        getSupportFragmentManager().popBackStack();
    }
    @Override
    public void doneCreateChat() {
        getSupportFragmentManager().popBackStack();
    }
    @Override
    public void close() {getSupportFragmentManager().popBackStack();}
    @Override
    public void addMessage(String id, String msgText, FirebaseUser mUser) {getSupportFragmentManager().popBackStack();}}