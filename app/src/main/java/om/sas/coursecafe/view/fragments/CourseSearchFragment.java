package om.sas.coursecafe.view.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.SearchRecentSuggestions;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import om.sas.coursecafe.R;
import om.sas.coursecafe.view.MyConstants;
import om.sas.coursecafe.view.SearchSuggestionProvider;
import om.sas.coursecafe.view.UtilityClass;
import om.sas.coursecafe.view.adapter.CoursesListAdapter;
import om.sas.coursecafe.view.model.CoursesModel;


public class CourseSearchFragment extends Fragment {

    private CoursesListAdapter mAdapter;
    private Context mContext;
    private CourseSearchFragmentListerner mListener;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private ArrayList<CoursesModel> list;
    private SearchView searchView;
    private TextView mNoResultText;

    public CourseSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mListener != null) {
            mListener.onFragmentInteraction(getString(R.string.searchTitle));
            mListener.showBottomNavBar();
            mListener.showBackButton(false);
        }
        // Inflate the layout for this fragment
        View parentView= inflater.inflate(R.layout.fragment_course_list_search, container, false);

        //listViewSearch = parentView.findViewById(R.id.lv_course_list_searchPage);
        recyclerView = parentView.findViewById (R.id.rv_courses_search);
        recyclerView.setLayoutManager( new LinearLayoutManager(mContext));


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(MyConstants.FIREBASE_KEY_POSTS);

        mNoResultText =parentView.findViewById(R.id.tv_no_search_result);

        mAdapter = new CoursesListAdapter(mContext);
        searchView = parentView.findViewById(R.id.search_view);

        SearchManager searchManager=(SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchManager !=null){
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));}

        // Do not iconify the widget; expand it by default
        //  searchView.setIconifiedByDefault(false);
        //  searchView.setQueryRefinementEnabled(true);
        //  searchView.requestFocus(1);

        Intent intent = getActivity().getIntent();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(mContext,
                    SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);

        }

        return  parentView;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof CourseSearchFragmentListerner) {
            mListener = (CourseSearchFragmentListerner) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LoginListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public void onItemPressed(CoursesModel courseItem) {
        if (mListener != null) {
            mListener.onCourseItemClick(courseItem);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (databaseReference != null) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        list = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            list.add(ds.getValue(CoursesModel.class));
                        }
                        mAdapter.updateCourseArrayList(list);

                        recyclerView.setAdapter(mAdapter);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Toast.makeText(mContext, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    SearchRecentSuggestions suggestions = new SearchRecentSuggestions(mContext,
                            SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
                    suggestions.saveRecentQuery(s, null);

                    search(s);
                    UtilityClass.hideKeyBoard(getActivity());
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {

                    if (s.length() > 0) {
                        //so the text length is grater that 0
                        // user input some thing
                        // implements the filter logic
                         search(s);
                        return true;
                    } else {
                        // user don't input anything
                        return false;

                    }
                }
            });

            searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                @Override
                public boolean onSuggestionSelect(int position) {
                    return true;
                }

                @Override
                public boolean onSuggestionClick(int position) {
                    CursorAdapter selectedView = searchView.getSuggestionsAdapter();
                    Cursor cursor = (Cursor) selectedView.getItem(position);
                    int index = cursor.getColumnIndexOrThrow(SearchManager.SUGGEST_COLUMN_TEXT_1);
                    searchView.setQuery(cursor.getString(index), true);
                    return true;
                }
            });


        }
    }

    // Search Method
    private void search(String str) {
        if (list != null && list.size() > 0) {
            ArrayList<CoursesModel> myList = new ArrayList<>();
            for (CoursesModel object : list) {


                // TODO : error cuz courseProvider not found in database

                boolean courseTitle = object.getTitle().toLowerCase().contains(str.toLowerCase());
                boolean courseProvider = object.getPostProviderName().toLowerCase().contains(str.toLowerCase());
                boolean courseInstitute = object.getInstitution().toLowerCase().contains(str.toLowerCase());
                boolean courseStartTime = object.getStartTime().toLowerCase().contains(str.toLowerCase());
                boolean courseEndTime = object.getEndTime().toLowerCase().contains(str.toLowerCase());
                boolean courseStartDate = object.getStartDate().toLowerCase().contains(str.toLowerCase());
                boolean courseEndDate = object.getEndDate().toLowerCase().contains(str.toLowerCase());
                boolean courseLocation = object.getLocationAddress().toLowerCase().contains(str.toLowerCase());

                if (courseTitle  || courseProvider || courseInstitute||courseStartTime || courseEndTime || courseStartDate ||  courseEndDate || courseLocation) {

                    //if (courseTitle) {
                    myList.add(object);
                }
            }

            if (myList.size()==0){
                mNoResultText.setVisibility(View.VISIBLE);

            }

            mAdapter.updateCourseArrayList(myList);
            //listViewSearch.setAdapter(mAdapter);
            recyclerView.setAdapter(mAdapter);

        }
    }



    // Interface Methods
    public interface CourseSearchFragmentListerner{

        void onCourseItemClick(CoursesModel couseItem);

        void onFragmentInteraction(String pageTitle);

        void showBottomNavBar();
        void showBackButton(boolean show);
    }


}
