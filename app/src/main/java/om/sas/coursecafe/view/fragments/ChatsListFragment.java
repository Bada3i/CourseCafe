package om.sas.coursecafe.view.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import om.sas.coursecafe.R;
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.notification.Token;
import om.sas.coursecafe.view.adapter.ChatAdapter;
import om.sas.coursecafe.view.model.ChatListItemModel;
import om.sas.coursecafe.view.model.UserModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatsListFragment extends Fragment {

    private  ChatsListFragmentInterface mListener;

    private FirebaseUser currentUser;

    private DatabaseReference reference;
    private FirebaseDatabase database;
    private UserModel mUserModel;
    //CHAT LST
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatListItemModel> chatList;
    private FirebaseUser firebaseUser;
    // DatabaseReference reference;
    private List<String> userslist;
    private Context mContext;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // ODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public ChatsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatsListFragment newInstance(UserModel user) {
        ChatsListFragment fragment = new ChatsListFragment();
        Bundle args = new Bundle();
        args.putSerializable(MyConstants.KEY_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserModel = (UserModel) getArguments().getSerializable(MyConstants.KEY_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View parentView = inflater.inflate(R.layout.fragment_chat, container, false);

        if (mListener != null) {
            mListener.onFragmentInteraction(getString(R.string.chatsScreenTitle));
            mListener.showBottomNavBar();
            mListener.showBackButton(false);
        }

        recyclerView = parentView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(getContext(), chatList, false);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();


        loadChatList();
        updateToken(FirebaseInstanceId.getInstance().getToken());
        return parentView;
    }

    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(MyConstants.CHAT_TOKENS);
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);
    }

    private void loadChatList() {

        Query query = FirebaseDatabase.getInstance().getReference(MyConstants.FIREBASE_KEY_CHAT).child(firebaseUser.getUid())
                .orderByChild("msgTime");

        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference(MyConstants.FIREBASE_KEY_CHAT).child(firebaseUser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatListItemModel chatListItem = snapshot.getValue(ChatListItemModel.class);
                    if(!chatListItem.getMsgTime().equals("")){
                    chatList.add(chatListItem);}
                }
                Collections.reverse(chatList);
               chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof ChatsListFragmentInterface) {
            mListener = (ChatsListFragmentInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CourseListFragmentListerner");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface ChatsListFragmentInterface {

        void onFragmentInteraction(String pageTitle);
        void showBottomNavBar();
        void showBackButton(boolean show);
    }
}
