package intan.steelytoe.com.ui.fragments.display;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import intan.steelytoe.com.R;
import intan.steelytoe.com.common.Impl.CrudTempLocationImpl;
import intan.steelytoe.com.ui.adapter.RunningHistoryItemAdapter;


public class RunningHistoryFragmet extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView listRunningHistory;
    private RunningHistoryItemAdapter listRunningAdapter;
    private CrudTempLocationImpl crudTempLocation;


    public RunningHistoryFragmet() {
    }

    public static RunningHistoryFragmet newInstance(String param1, String param2) {
        RunningHistoryFragmet fragment = new RunningHistoryFragmet();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        crudTempLocation = new CrudTempLocationImpl(getContext());
        listRunningAdapter = new RunningHistoryItemAdapter(crudTempLocation.getRunningHistory(), getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_running_history, container, false);

        listRunningHistory = (RecyclerView) view.findViewById(R.id.recycler_running_history);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        listRunningHistory.setLayoutManager(layoutManager);
        listRunningHistory.setAdapter(listRunningAdapter);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        itemAnimator.setChangeDuration(1000);

        listRunningHistory.setItemAnimator(itemAnimator);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle("History");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
