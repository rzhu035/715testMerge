package de.tadris.fitness.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.tadris.fitness.OnItemClickListener;
import de.tadris.fitness.R;
import de.tadris.fitness.model.WorkBean;
import de.tadris.fitness.ui.adapter.FitnessListAdapter;

public class FitnessTabFragment extends Fragment {
    private RecyclerView workoutsRy;
    private ArrayList<WorkBean> dataList;
    private FitnessListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fitness, container, false);
        workoutsRy = view.findViewById(R.id.workouts_list);
        initListData();
        return view;
    }

    private void initListData() {
        dataList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (i ==0) {
                dataList.add(new WorkBean(R.mipmap.person,"Nov 7,2023 12:47:07 PM","Running","work_distance","work_distance_time"));
            } else if (i ==1) {
                dataList.add(new WorkBean(R.mipmap.person,"Nov 8,2023 3:47:07 PM","Running","work_distance","work_distance_time"));
            } else if (i ==2) {
                dataList.add(new WorkBean(R.mipmap.person,"Nov 9,2023 5:47:07 PM","Working","work_distance","work_distance_time"));
            }
          /*  else if (i ==3) {
                dataList.add(new WorkBean(R.mipmap.person,"Nov 10,2023 8:47:07 PM","Working","work_distance","work_distance_time"));
            }*/
         /*   else if (i ==4) {
                dataList.add(new WorkBean(R.mipmap.person,"Nov 2,2023 2:47:07 PM","Running","running_distance","work_distance_time"));
            } else if (i ==5) {
                dataList.add(new WorkBean(R.mipmap.person,"Nov 23,2023 2:47:07 PM","Running","work_distance","work_distance_time"));
            } else if (i ==6){
                dataList.add(new WorkBean(R.mipmap.person,"Nov 7,2023 2:47:07 PM","Running","work_distance","work_distance_time"));
            }*/
        }
        adapter = new FitnessListAdapter(dataList);
        initAdapter();
        workoutsRy.setAdapter(adapter);
        // 1. 使用 LinearLayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        workoutsRy.setLayoutManager(linearLayoutManager);
    }

    private void initAdapter() {
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });
    }
}
