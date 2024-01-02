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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import edu.uncc.hw06.databinding.FragmentCreateChatBinding;


public class CreateChatFragment extends Fragment {
    public CreateChatFragment() {// Required empty public constructor
    }
    ArrayList<User> users = new ArrayList<User>();
    ArrayAdapter<User> adapter;
    User selectedUser;
    FragmentCreateChatBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateChatBinding.inflate(inflater, container, false);
        return binding.getRoot();}
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("New Chat");
        adapter = new ArrayAdapter<User>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, users);
        binding.listViewUsers.setAdapter(adapter);

        binding.listViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedUser = users.get(position);
                binding.textViewSelectedUser.setText(selectedUser.getName());
            }
        });
        //getting the user
        FirebaseFirestore.getInstance().collection("users").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    users.clear();
                                    //to not show the current user in the list
                                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                    for(QueryDocumentSnapshot doc : task.getResult()){
                                        User user = doc.toObject(User.class);
                                        if(!user.getUid().equals(currentUser.getUid())){
                                            users.add(user);
                                        }}
                                    adapter.notifyDataSetChanged();
                                }else{
                                    new AlertDialog.Builder(getContext())
                                            .setTitle("Error!")
                                            .setMessage(task.getException().getLocalizedMessage())
                                            .setPositiveButton(android.R.string.ok, null).show();
                                }
                            }
                        });

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.cancelCreateChat();}});

        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msgText = binding.editTextChat.getText().toString();
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if(selectedUser == null){
                    new AlertDialog.Builder(getContext())
                            .setTitle("Error")
                            .setMessage("Please select a user!!!")
                            .setPositiveButton(android.R.string.ok, null).show();
                } else if (msgText.isEmpty()) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Error")
                            .setMessage("Message cannot be empty!!!")
                            .setPositiveButton(android.R.string.ok, null).show();
                }else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference chatRef = db.collection("chats").document();
                    HashMap<String, Object> chatData = new HashMap<>();
                    chatData.put("id", chatRef.getId());

                    //setting up the ids
                    ArrayList<String> uids = new ArrayList<>();
                    uids.add(currentUser.getUid());
                    uids.add(selectedUser.getUid());
                    chatData.put("userIds", uids);

                    //setting up the names
                    ArrayList<String> names = new ArrayList<>();
                    names.add(currentUser.getDisplayName());
                    names.add(selectedUser.getName());
                    chatData.put("userNames", names);

                    //setting up the messages
                    DocumentReference msgRef = chatRef.collection("messages").document();
                    HashMap<String, Object> msgData = new HashMap<>();
                    msgData.put("msgText", msgText);
                    msgData.put("ownerId", currentUser.getUid());
                    msgData.put("ownerName", currentUser.getDisplayName());
                    msgData.put("ownerName", currentUser.getDisplayName());
                    msgData.put("createdAt", FieldValue.serverTimestamp());
                    msgData.put("id", msgRef.getId());

                    //to store the last message
                    chatData.put("lastMsg", msgData);

                    chatRef.set(chatData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                msgRef.set(msgData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            mListener.doneCreateChat();
                                        }else{
                                            new AlertDialog.Builder(getContext())
                                                    .setTitle("Error!")
                                                    .setMessage(task.getException().getLocalizedMessage())
                                                    .setPositiveButton(android.R.string.ok, null).show();
                                        }}});}
                            else{
                                new AlertDialog.Builder(getContext())
                                        .setTitle("Error!")
                                        .setMessage(task.getException().getLocalizedMessage())
                                        .setPositiveButton(android.R.string.ok, null).show();}}});}}});}
    CreateForumListener mListener;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (CreateForumListener) context;
    }
    interface CreateForumListener{
        void cancelCreateChat();
        void doneCreateChat();}}