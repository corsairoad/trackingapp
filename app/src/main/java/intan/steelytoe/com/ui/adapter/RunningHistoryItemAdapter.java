package intan.steelytoe.com.ui.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.ListIterator;

import intan.steelytoe.com.R;
import intan.steelytoe.com.common.Impl.CrudRunningDetail;
import intan.steelytoe.com.model.RunningDetail;
import intan.steelytoe.com.model.RunningHeaderHistory;
import intan.steelytoe.com.ui.activity.HistoryMapsActivity;

/**
 * Created by fadlymunandar on 7/11/17.
 */

public class RunningHistoryItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private List<RunningHeaderHistory> runningHistories;
    private Context context;
    private static final int ITEM_PARENT = 100;
    private static final int ITEM_CHILD = 2;
    private CrudRunningDetail crudRunningDetail;
    private int rotationAngle = 0;

    public RunningHistoryItemAdapter(List<RunningHeaderHistory> runningHistories, Context context) {
        this.runningHistories = runningHistories;
        this.context = context;
        crudRunningDetail = CrudRunningDetail.getInstance(this.context);
    }

    @Override
    public int getItemViewType(int position) {

        if (runningHistories.get(position) instanceof  RunningDetail) {
            return ITEM_CHILD;
        }
        return ITEM_PARENT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ITEM_PARENT:
                view = inflater.inflate(R.layout.layout_running_history_item, parent, false);
                viewHolder = new MyViewHolder(view);
                break;
            case ITEM_CHILD:
                view = inflater.inflate(R.layout.layout_running_history_detail, parent, false);
                viewHolder = new ItemDetailViewHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case ITEM_PARENT:
                setUpParent(holder, position);
                break;
            case ITEM_CHILD:
                setUpChild(holder, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return runningHistories.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout containerParent;
        TextView textTotalRunning;
        TextView textRunningDate;
        ImageView imgArrow;

        public MyViewHolder(View itemView) {
            super(itemView);
            containerParent = (LinearLayout) itemView.findViewById(R.id.container_parent_running_history);
            textTotalRunning = (TextView) itemView.findViewById(R.id.text_total_running_item);
            textRunningDate = (TextView) itemView.findViewById(R.id.text_running_item_date);
            imgArrow = (ImageView) itemView.findViewById(R.id.img_arrow_down);
        }
    }

    public class ItemDetailViewHolder extends RecyclerView.ViewHolder {

        LinearLayout containerChild;
        TextView textRunningId;
        TextView textRunningDetailTime;
        TextView textRunningDetailDistance; // distance and duration

        public ItemDetailViewHolder(View view) {
            super(view);
            containerChild = (LinearLayout) view.findViewById(R.id.container_child_running_history);
            textRunningId = (TextView) view.findViewById(R.id.text_running_id);
            textRunningDetailTime = (TextView) view.findViewById(R.id.text_running_detail_time);
            textRunningDetailDistance = (TextView) view.findViewById(R.id.text_running_detail_distance);
        }
    }

    private void setUpParent(RecyclerView.ViewHolder viewHolder, final int position) {
        final RunningHeaderHistory headerHistory = runningHistories.get(position);
        final MyViewHolder holder = (MyViewHolder) viewHolder;
        holder.textTotalRunning.setText(String.valueOf(headerHistory.getTotalRunning()));
        holder.textRunningDate.setText(headerHistory.getRunningDateHeader());
        holder.containerParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dateHeader = headerHistory.getRunningDateHeader();
                List<RunningDetail> childs = crudRunningDetail.getRunningDetailsByDateHeader(dateHeader);

                if (!childs.isEmpty()) {
                    animate(holder.imgArrow);
                    if (isCollapsed(position)) {
                        removeChilds(childs, position);
                    } else {
                        addChilds(childs, position);
                    }
                }

            }
        });
    }

    private void setUpChild(RecyclerView.ViewHolder viewHolder, int position) {
        RunningDetail runningDetail = (RunningDetail) runningHistories.get(position);
        ItemDetailViewHolder holder = (ItemDetailViewHolder) viewHolder;

        holder.textRunningId.setText(runningDetail.getRunningId());
        holder.textRunningDetailTime.setText(runningDetail.getDateTime());
        holder.textRunningDetailDistance.setText(runningDetail.getDistance() + " km  in " + runningDetail.getDuration());
        holder.containerChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRunningLog(null);
            }
        });
    }

    private void addChilds(List<RunningDetail> childs, int position) {
        for (RunningDetail runningDetail : childs) {
            runningHistories.add(position+1, runningDetail);
            notifyItemInserted(position+1);
        }
        notifyDataSetChanged();
    }

    private void removeChilds(List<RunningDetail> childs, int position) {

        ListIterator<RunningHeaderHistory> iterator = runningHistories.listIterator(position+1);
        while (iterator.hasNext()) {
            if (iterator.next() instanceof RunningDetail) {
                iterator.remove();
                notifyItemRemoved(iterator.nextIndex());
                notifyItemRangeRemoved(iterator.nextIndex(), childs.size());
            }else {
                break;
            }
        }
    }

    private boolean isCollapsed(int position) {
        try {
            if (runningHistories.get(position + 1) != null) {
                if (runningHistories.get(position+1) instanceof RunningDetail) {
                    return true;
                }
            }
            return false;
        }catch (IndexOutOfBoundsException e) {
            return false;
        }

    }

    private void animate(ImageView v) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(v, "rotation",rotationAngle, rotationAngle + 180);
        anim.setDuration(100);
        anim.start();
        rotationAngle += 180;
        rotationAngle = rotationAngle%360;
    }

    private void openRunningLog(String runningId) {
        this.context.startActivity(new Intent(this.context, HistoryMapsActivity.class));
    }

}
