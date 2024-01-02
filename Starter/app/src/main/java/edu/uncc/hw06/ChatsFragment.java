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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.uncc.hw06.databinding.ChatRowItemBinding;

import edu.uncc.hw06.databinding.FragmentChatsBinding;


public class ChatsFragment extends Fragment {
    public ChatsFragment() {// Required empty public constructor
    }
    FragmentChatsBinding binding;
    ArrayList<Chat> chats = new ArrayList<>();
    ChatsAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("ChatsFragment", "onCreateView");
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        return binding.getRoot();}
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("My Chats");
        Log.d("ChatsFragment", "onViewCreated");

        adapter = new ChatsAdapter();
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setVisibility(View.VISIBLE);

        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.logout();
            }
        });

        binding.buttonCreateChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.createNewChat();
            }});

        FirebaseFirestore.getInstance().collection("chats").whereArrayContains("userIds", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> chatsTask) {
                        if(chatsTask.isSuccessful()){
                            chats.clear();
                            for(QueryDocumentSnapshot chatDoc : chatsTask.getResult()){
                                Log.d("ChatsFragment", "onComplete: " + chatDoc.getId() + " => " + chatDoc.getData());
                                Chat chat = chatDoc.toObject(Chat.class);
                                chats.add(chat);}
                            adapter.notifyDataSetChanged();
                        }else{
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Error!")
                                    .setMessage(chatsTask.getException().getLocalizedMessage())
                                    .setPositiveButton(android.R.string.ok, null).show();}}});}

    ForumsListener mListener;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ForumsListener) context;}
    interface ForumsListener {
        void createNewChat();
        void logout();
        //c1 steps
        void gotoChat(Chat chat);}
    class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder>{

        @NonNull
        @Override
        public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d("ChatsFragment", "onCreateView");
            ChatRowItemBinding itemBinding = ChatRowItemBinding.inflate(getLayoutInflater(), parent, false);
            return new ChatsViewHolder(itemBinding);}

        @Override
        public void onBindViewHolder(@NonNull ChatsViewHolder holder, int position) {
            Chat chat = chats.get(position);
            Log.d("ChatsAdapter", "onBindViewHolder: " + chat.toString());
            holder.setupUI(chat);
        }

        @Override
        public int getItemCount() {
            return chats.size();
        }
        class ChatsViewHolder extends RecyclerView.ViewHolder{
            ChatRowItemBinding mBinding;
            Chat mChat;
            public ChatsViewHolder(@NonNull ChatRowItemBinding itemBinding) {
                super(itemBinding.getRoot());
                mBinding = itemBinding;
            }
            public void setupUI(Chat chat){
                this.mChat = chat;
                mBinding.textViewChatText.setText(chat.getLastMsg().getMsgText());
                mBinding.textViewChatCreatedBy.setText(chat.getLastMsg().getOwnerName());


//                mBinding.textViewChatCreatedBy.setText(chat.getChatStarterName());

                Date date = chat.getLastMsg().getCreatedAt().toDate();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                mBinding.textViewChatDate.setText(sdf.format(date));

                mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.gotoChat(mChat);
                    }});}}}}