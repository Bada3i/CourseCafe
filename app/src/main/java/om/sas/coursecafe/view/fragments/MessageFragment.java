package om.sas.coursecafe.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import om.sas.coursecafe.R;
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.notification.Client;
import om.sas.coursecafe.view.notification.Data;
import om.sas.coursecafe.view.notification.MyResponse;
import om.sas.coursecafe.view.notification.Sender;
import om.sas.coursecafe.view.notification.Token;
import om.sas.coursecafe.view.adapter.MassageAdapter;
import om.sas.coursecafe.view.model.Chat;
import om.sas.coursecafe.view.model.ChatListItemModel;
import om.sas.coursecafe.view.model.UserModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageFragment extends Fragment {

    private MessageFragmentInterface mListener;
    private Context mContext;
    private MassageAdapter massageAdapter;
    private ArrayList<Chat> mChat;
    private RecyclerView recyclerView;

    private CircleImageView profile_image;
    private TextView username;

    private FirebaseUser firebaseUser;
    private String firebaseCurrentUserID;
    private String receiverFirebaseID;


    private DatabaseReference userDBReference;
    private DatabaseReference chatListDBReference;
    private DatabaseReference chatConversationDBReference;

    private ImageButton btn_send;
    private EditText text_send;

    private String currentChatHistoryNode = null;
    private String chatListItemReciver = null;

    private APIService apiService;
    boolean notify = false;
    private ValueEventListener seenListener;
    private DatabaseReference reference;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String postProviderId;
    private String lastMessageSent="";
    private String lastMessageSentTime="";
    private String userProfilePic;

    public MessageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessageFragment newInstance(String uId) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, uId);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            receiverFirebaseID = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_message, container, false);


        if (mListener != null) {
            //mListener.onFragmentInteraction("Message");
            mListener.hideBottomNavBar();
            mListener.hideBackButton();
        }

        //notfcaton
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseCurrentUserID = firebaseUser.getUid();
        // if(UserModel.getInstance().getUsertype().equals(USERTYPE1)) {
        // Getiing a Chat node for Normal user
        getOrSetFirebaseChatID();
        // }else {
        // Getiing a Chat node for Course Owner
        //    getOrSetFirebaseChatIDForOwner();
        //}
        profile_image = parentView.findViewById(R.id.image_profile1);
        username = parentView.findViewById(R.id.username1);

        FirebaseDatabase dbInstance = FirebaseDatabase.getInstance();
        userDBReference = dbInstance.getReference(MyConstants.FIREBASE_KEY_USERS).child(receiverFirebaseID);
        chatListDBReference = dbInstance.getReference(MyConstants.FIREBASE_KEY_CHAT);
        chatConversationDBReference = dbInstance.getReference(MyConstants.FIREBASE_KEY_CHAT_CONVERSATION);
        currentChatHistoryNode = MyConstants.current_ChatHistory_Node;
        userDBReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                if (userModel !=null){
                    username.setText(userModel.getFullName());
                    userProfilePic=userModel.getProfilePic();
                    Glide.with(getActivity()).load(userModel.getProfilePic()).placeholder(R.drawable.ic_person).into(profile_image);
                    readMessages();
                    //readMessages(firebaseCurrentUserID, receiverFirebaseID, userModel.getProfilePic());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        recyclerView = parentView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        massageAdapter = new MassageAdapter(mContext,userProfilePic);
        recyclerView.setAdapter(massageAdapter);



        ImageView btnBack=parentView.findViewById(R.id.back_arrow);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToChatListScreenBack();
            }
        });

        btn_send = parentView.findViewById(R.id.btn_send_msg);
        text_send = parentView.findViewById(R.id.text_send_msg);

        btn_send.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                notify = true;
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(msg);
                    lastMessageSent = msg;
                    lastMessageSentTime =getCurrentDate();
                    readMessages();
                    insertLastCommentIntheChatNodeBeforeClose();
                    insertLastCommentIntheChatNodeBeforeCloseA();



                }
                text_send.setText("");
            }

        }
        );







        return parentView;
    }


    private void getOrSetFirebaseChatID() {
        final DatabaseReference currentUseReference = FirebaseDatabase.getInstance().getReference(MyConstants.FIREBASE_KEY_CHAT).child(firebaseCurrentUserID);
        final DatabaseReference receiverUseReference = FirebaseDatabase.getInstance().getReference(MyConstants.FIREBASE_KEY_CHAT).child(receiverFirebaseID);

        currentUseReference.orderByChild(MyConstants.FIREBASE_CHATHISTORY_OWNER).equalTo(receiverFirebaseID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String chatHistoryNode = null;
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        currentChatHistoryNode = dataSnapshot.child(MyConstants.FIREBASE_KEY_CHAT_CONVERSATION).getValue(String.class);
                    }
                } else {
                    currentChatHistoryNode = chatListDBReference.push().getKey();

                    String chatNodeID  = currentUseReference.push().getKey();
                    ChatListItemModel chatListItemSender = new ChatListItemModel();
                    chatListItemSender.setChatHistory(currentChatHistoryNode);
                    chatListItemSender.setChatOwner(receiverFirebaseID);
                    chatListItemSender.setLastMessage(lastMessageSent);
                    chatListItemSender.setMsgTime(lastMessageSentTime);
                    chatListItemSender.setNodeIDRef(chatNodeID);
                    currentUseReference.child(chatNodeID).setValue(chatListItemSender);

                    //--------------------
                    String chatNodeIDReciver  = receiverUseReference.push().getKey();
                    ChatListItemModel chatListItemReciver = new ChatListItemModel();
                    chatListItemReciver.setChatHistory(currentChatHistoryNode);
                    chatListItemReciver.setChatOwner(firebaseCurrentUserID);
                    chatListItemReciver.setLastMessage(lastMessageSent);
                    chatListItemReciver.setMsgTime(lastMessageSentTime);
                    chatListItemReciver.setNodeIDRef(chatNodeIDReciver);
                    receiverUseReference.child(chatNodeIDReciver).setValue(chatListItemReciver);




                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void sendMessage(String message) {
        if (currentChatHistoryNode == null) {
            //TODO Some problem in creating a chatHistory node
        } else {
            Chat newMessage = new Chat();
            newMessage.setMessage(message);
            newMessage.setTime(getCurrentDate());
            newMessage.setChatOwner(receiverFirebaseID);
            newMessage.setSender(firebaseCurrentUserID);

            chatConversationDBReference.child(currentChatHistoryNode).push().setValue(newMessage);


        }

        final String msg = message;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(MyConstants.FIREBASE_KEY_USERS).child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel = snapshot.getValue(UserModel.class);
                if (notify) {
                    sendNotification(receiverFirebaseID, userModel.getFullName(), msg);
                }
                notify = false;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void sendNotification(final String receiver, final String username, final String msg) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(MyConstants.CHAT_TOKENS);
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, username + ": " + msg,
                            "New Message", receiverFirebaseID);
       //             assert token != null;
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void readMessages() {
        mChat = new ArrayList<>();

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference(MyConstants.FIREBASE_KEY_CHAT_CONVERSATION).child(currentChatHistoryNode);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getChatOwner().equals(firebaseCurrentUserID) && firebaseCurrentUserID.equals(receiverFirebaseID) ||
                            chat.getChatOwner().equals(receiverFirebaseID) && firebaseCurrentUserID.equals(firebaseCurrentUserID)) {
                    }
                    mChat.add(chat);
//                    massageAdapter = new MassageAdapter(getContext(), mChat, imageurl);
//                    recyclerView.setAdapter(massageAdapter);
                }
                massageAdapter.updateMassageAdapter(mChat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    // get current date
    private String getCurrentDate() {
        return android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", new java.util.Date()).toString();
    }

    @Override
    public void onStop() {
        super.onStop();
        //insertLastCommentIntheChatNodeBeforeClose();
        //insertLastCommentIntheChatNodeBeforeCloseA();

    }

    private void insertLastCommentIntheChatNodeBeforeClose() {
        final DatabaseReference currentUseReference = FirebaseDatabase.getInstance().getReference(MyConstants.FIREBASE_KEY_CHAT).child(firebaseCurrentUserID);

        currentUseReference.orderByChild(MyConstants.FIREBASE_CHATHISTORY_OWNER).equalTo(receiverFirebaseID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ChatListItemModel chatListItem = dataSnapshot.getValue(ChatListItemModel.class);


                        currentUseReference.child(chatListItem.getNodeIDRef()).child("lastMessage").setValue(lastMessageSent);
                        currentUseReference.child(chatListItem.getNodeIDRef()).child("msgTime").setValue(lastMessageSentTime);



                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void insertLastCommentIntheChatNodeBeforeCloseA() {
        final DatabaseReference receiverUseReference = FirebaseDatabase.getInstance().getReference(MyConstants.FIREBASE_KEY_CHAT).child(receiverFirebaseID);

        receiverUseReference.orderByChild(MyConstants.FIREBASE_CHATHISTORY_OWNER).equalTo(firebaseCurrentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ChatListItemModel chatListItem = dataSnapshot.getValue(ChatListItemModel.class);
                        receiverUseReference.child(chatListItem.getNodeIDRef()).child("lastMessage").setValue(lastMessageSent);
                        receiverUseReference.child(chatListItem.getNodeIDRef()).child("msgTime").setValue(lastMessageSentTime);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof MessageFragmentInterface) {
            mListener = (MessageFragmentInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CourseListFragmentListerner");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

        SharedPreferences.Editor editor = getActivity().getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", null);
        editor.apply();
    }

    @Override
    public void onResume() {

        super.onResume();
        currentUserOnChat(receiverFirebaseID);
    }

    @Override
    public void onPause() {

        super.onPause();
        currentUserOnChat(receiverFirebaseID);
    }

    private void currentUserOnChat(String userid){
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }


    public interface MessageFragmentInterface {

        void onFragmentInteraction(String pageTitle);
        void hideBottomNavBar();
        //void showBackButton(boolean show);
        void goToChatListScreenBack();
        void hideBackButton();
    }

}









