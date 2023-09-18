package de.tadris.fitness.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.tadris.fitness.OnItemClickListener;
import de.tadris.fitness.R;
import de.tadris.fitness.model.WorkBean;

public class FitnessListAdapter extends  RecyclerView.Adapter<FitnessListAdapter.FitnessListViewHolder>{
    private List<WorkBean> dataList;

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    // 构造器
    public FitnessListAdapter(List<WorkBean> dataList) {
        this.dataList = dataList;
    }

    @Override
    public FitnessListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.workouts_list_item, parent, false);
        return new FitnessListViewHolder(itemView,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FitnessListViewHolder holder, int position) {
        WorkBean data = dataList.get(position);

        holder.workIcon.setImageResource(data.workTypeIcon);
        holder.workTime.setText(data.time);
        holder.workType .setText(data.workType);
        holder.workDistance .setText(data.workDistance);
        holder.workDistanceTime .setText(data.workDistanceTime);


    }

    @Override
    public int getItemCount () {
        return dataList.size();
    }

    // ViewHolder 类
    public static class FitnessListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private View separator;
        private TextView workDistanceTime;
        private TextView workDistance;
        private TextView workType;
        private OnItemClickListener mListener;
        private TextView workTime;
        private ImageView workIcon;
        public TextView textView;

        public FitnessListViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            workIcon = itemView.findViewById(R.id.work_icon);
            workTime = itemView.findViewById(R.id.work_time);
            workType = itemView.findViewById(R.id.work_type);
            workDistance = itemView.findViewById(R.id.work_distance);
            workDistanceTime = itemView.findViewById(R.id.work_distance_time);
            separator = itemView.findViewById(R.id.separator_work);

            mListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }
    }

}
