package com.example.shakiewakie.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shakiewakie.R;
import com.example.shakiewakie.models.Alarm;
import com.example.shakiewakie.utils.DatabaseHelper;
import java.util.List;

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.AlarmViewHolder> {
    private List<Alarm> alarmList;
    private Context context;
    private DatabaseHelper dbHelper;

    public AlarmListAdapter(Context context, List<Alarm> alarmList) {
        this.context = context;
        this.alarmList = alarmList;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_alarm, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        Alarm alarm = alarmList.get(position);
        holder.alarmTimeTextView.setText(alarm.getTime());
        holder.alarmSwitch.setChecked(alarm.isEnabled());

        holder.alarmSwitch.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            alarm.setEnabled(isChecked);
            dbHelper.updateAlarmState(alarm.getId(), isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView alarmTimeTextView;
        Switch alarmSwitch;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            alarmTimeTextView = itemView.findViewById(R.id.tv_alarm_time);
            alarmSwitch = itemView.findViewById(R.id.switch_alarm);
        }
    }
}
