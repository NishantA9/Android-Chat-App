package edu.uncc.hw06;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.uncc.hw06.databinding.FragmentChatBinding;
import edu.uncc.hw06.databinding.NewChatRowItemBinding;

public class ChatFragment extends Fragment {
    private static final String ARG_PARAM_FORUM = "ARG_PARAM_FORUM";
    private Chat mChat;
    private FirebaseUser mUser;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<Message> mMessages = new ArrayList<>();
    CommentsAdapter adapter;
    public ChatFragment() {// Required empty public constructor
    }
    public static ChatFragment newInstance(Chat chat) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_FORUM, chat);
        fragment.setArguments(args);
        return fragment;}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mChat = (Chat) getArguments().getSerializable(ARG_PARAM_FORUM);}}
    FragmentChatBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();}
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Selected Chat");

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new CommentsAdapter();
        binding.recyclerView.setAdapter(adapter);
        binding.textViewChatTitle.setText(mChat.getLastMsg().getOwnerName());
        loadMessagesForChat(mChat.getId());

        mUser = mAuth.getCurrentUser();
        if (mUser != null) {// The user is authenticated. You can use mUser.
        } else {// The user is not authenticated. Handle this case.
            Log.e("ChatFragment", "User is not authenticated.");
        }

        binding.imageViewDeleteChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteChat(mChat.getId());}});

        binding.buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.close();
            }});

        binding.buttonSubmitChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msgText = binding.editTextChatMessage.getText().toString();
                addMessage(mChat.getId(), msgText, mUser);}});

    }

    private void loadMessagesForChat(String chatId) {
        FirebaseFirestore.getInstance().collection("chats").document(chatId).collection("messages")
                .orderBy("createdAt") // Order messages by creation time
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot messagesSnapshot, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("ChatFragment", "Error fetching messages: " + error.getMessage());
                            return;}
                        if (messagesSnapshot != null) {
                            mMessages.clear();
                            for (QueryDocumentSnapshot messageDoc : messagesSnapshot) {
                                Message message = messageDoc.toObject(Message.class);
                                mMessages.add(message);
                            }
                            adapter.notifyDataSetChanged();}}});}

    public void addMessage(String chatId, String msgText, FirebaseUser mUser){
        if (mUser == null || mUser.getUid() == null) {
            // Handle the case where currentUser is null, show an error, or take appropriate action
            Log.e("ChatFragment", "CurrentUser is null");
            return;
        }
        DocumentReference chatRef = db.collection("chats").document(chatId);
        DocumentReference msgRef = chatRef.collection("messages").document();
        HashMap<String, Object> msgData = new HashMap<>();
        msgData.put("msgText", msgText);
        msgData.put("ownerId", mUser.getUid());
        msgData.put("ownerName", mUser.getDisplayName());
        msgData.put("createdAt", FieldValue.serverTimestamp());
        msgData.put("id", msgRef.getId());

        // Update the last message in the chat document
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("lastMsg", msgData);

        // Add the new message to the "messages" subcollection
        msgRef.set(msgData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Update the last message in the chat document
                    chatRef.update(updateData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mListener.addMessage(mChat.getId(), msgText, mUser);
                            } else {
                                new AlertDialog.Builder(getContext())
                                        .setTitle("Error!")
                                        .setMessage(task.getException().getLocalizedMessage())
                                        .setPositiveButton(android.R.string.ok, null).show();
                            }}});}
                else {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Error!")
                            .setMessage(task.getException().getLocalizedMessage())
                            .setPositiveButton(android.R.string.ok, null).show();
                }}});}

    //to delete a complete chat from firebase and then the user goes to main screen
    public void deleteChat(String chatId){
        db.collection("chats").document(chatId).delete();
        mListener.close();}

    ChatFragmentlistener mListener;
    interface  ChatFragmentlistener{
        void close();
        void addMessage(String id, String msgText, FirebaseUser mUser);}
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ChatFragmentlistener) context;}

    class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>{
        @NonNull
        @Override
        public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            NewChatRowItemBinding itemBinding = NewChatRowItemBinding.inflate(getLayoutInflater(),parent,false);
            return  new CommentViewHolder(itemBinding);}

        @Override
        public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
            Message message = mMessages.get(position);
            holder.setupUI(message);}

        @Override
        public int getItemCount() {
            return mMessages.size();}
        class CommentViewHolder extends RecyclerView.ViewHolder{
            NewChatRowItemBinding mBinding;
            Message messageS;

            public CommentViewHolder(NewChatRowItemBinding itemBinding) {
                super(itemBinding.getRoot());
                mBinding = itemBinding;}
            public void setupUI(Message message){
                this.messageS = message;
                mBinding.textViewNewChatCreatedBy.setText(message.getOwnerName());
                mBinding.textViewNewChatText.setText(message.getMsgText());

                // To setup the date of comment
                if (messageS.getCreatedAt() != null) {
                    Timestamp createdAtTimestamp = messageS.getCreatedAt();
                    if (createdAtTimestamp != null) {
                        Date date = createdAtTimestamp.toDate();
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                        mBinding.textViewNewChatCreatedAt.setText(sdf.format(date));
                    } else {mBinding.textViewNewChatCreatedAt.setText("");}
                } else {mBinding.textViewNewChatCreatedAt.setText("");}

              //to show the delete button of that user who created it
                if (messageS.getOwnerId() != null && mAuth.getCurrentUser() != null &&
                        messageS.getOwnerId().equals(mAuth.getCurrentUser().getUid())) {
                    mBinding.imageViewDelete.setVisibility(View.VISIBLE);
                    mBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            db.collection("chats").document(mChat.getId())
                                    .collection("messages")
                                    .document(messageS.getId()).delete();}});
                } else {mBinding.imageViewDelete.setVisibility(View.INVISIBLE);}}}}}



