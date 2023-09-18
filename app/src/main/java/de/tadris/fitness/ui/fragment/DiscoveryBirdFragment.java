package de.tadris.fitness.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import de.tadris.fitness.R;

public class DiscoveryBirdFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_find_bird, container, false);

       /* recyclerView = view.findViewById(R.id.profile_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<PersonBean> dataList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            if (i ==0) {
                dataList.add(new PersonBean(R.mipmap.person_icon_a,"Personal"));
            } else if (i ==1) {
                dataList.add(new PersonBean(R.mipmap.notification,"Notification"));
            } else if (i ==2) {
                dataList.add(new PersonBean(R.mipmap.wallet,"Wallet"));
            } else if (i ==3) {
                dataList.add(new PersonBean(R.mipmap.history,"History"));
            } else if (i ==4) {
                dataList.add(new PersonBean(R.mipmap.save,"Save"));
            }
        }

        recyclerView.setAdapter(new ProfileAdapter(dataList));
*/
        return view;
    }
}
