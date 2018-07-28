package sahil.iiitk_foundationday_app.views;
//Made by Tanuj
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.db.chart.animation.Animation;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.renderer.XRenderer;
import com.db.chart.tooltip.Tooltip;
import com.db.chart.view.HorizontalBarChartView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.Locale;
import java.util.Map;

import sahil.iiitk_foundationday_app.R;
import sahil.iiitk_foundationday_app.adapters.MyToast;

public class AdminInsightsFragment extends Fragment {

    PieChart pieChart;
    TextView barchart_label1,barchart_label2,barchart_label3,barchart_label4;
    HorizontalBarChartView barChart1,barChart2,barChart3,barChart4;
    FirebaseDatabase db;
    String club_name;
    int club_number;
    int[] order={5,4,3,2,1,0};
    Boolean isPieChartReady=false;
    long total_reg,technical_reg,cultural_reg,literary_reg,photography_reg;
    long[] technical_events_counters=new long[6];
    long[] cultural_events_counters=new long[6];
    long[] literary_events_counters=new long[5];
    long[] photography_events_counters=new long[6];
    EventsNamesInterface2 provider;

    public interface EventsNamesInterface2{
        public String[] getEventNames(int club_number);
        public int getClubNumber();
        public String getSelectedClubName();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            provider=(EventsNamesInterface2) activity;
        }catch (ClassCastException e){
            Log.e("interface",e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_insights, container, false);
        pieChart=(PieChart)view.findViewById(R.id.admin_insights_pie);
        barChart1=(HorizontalBarChartView) view.findViewById(R.id.barChart1);
        barchart_label1=view.findViewById(R.id.barChart1_value);
        barChart2=(HorizontalBarChartView) view.findViewById(R.id.barChart2);
        barchart_label2=view.findViewById(R.id.barChart2_value);
        barChart3=(HorizontalBarChartView) view.findViewById(R.id.barChart3);
        barchart_label3 =view.findViewById(R.id.barChart3_value);
        barChart4=(HorizontalBarChartView) view.findViewById(R.id.barChart4);
        barchart_label4 =view.findViewById(R.id.barChart4_value);

        db=FirebaseDatabase.getInstance();
        downloadAllCounters();

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            if (isPieChartReady){
                pieChart.update();
                pieChart.startAnimation();
                barChart1.show(new Animation().inSequence(.5f,order));
                barChart2.show(new Animation().inSequence(.5f,order));
                int[] orderfor3={4,3,2,1,0};
                barChart3.show(new Animation().inSequence(.5f,orderfor3));
                barChart4.show(new Animation().inSequence(.5f,order));
            }
        }
    }
// app crashes when onResume is fired .. don't know why
//    @Override
//    public void onResume() {
//        super.onResume();
//        Log.e("insights","onResume");
//        if (isPieChartReady){
//            pieChart.update();
//            pieChart.startAnimation();
//            barChart1.show(new Animation().inSequence(.5f,order));
//            barChart2.show(new Animation().inSequence(.5f,order));
//            int[] orderfor3={4,3,2,1,0};
//            barChart3.show(new Animation().inSequence(.5f,orderfor3));
//            barChart4.show(new Animation().inSequence(.5f,order));
//        }
//    }

    public void downloadAllCounters(){

        club_name=provider.getSelectedClubName();
        club_number=provider.getClubNumber();

        final DatabaseReference ref=db.getReference("Counters");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map values=(Map) dataSnapshot.getValue();
                total_reg=(long)values.get("total_reg");
                technical_reg=(long) values.get("technical_reg");
                cultural_reg=(long) values.get("cultural_reg");
                literary_reg=(long) values.get("literary_reg");
                photography_reg=(long) values.get("photography_reg");

                for (int i=0;i<=5;i++){
                    technical_events_counters[i]=(long) values.get("Event0"+i);
                }
                for (int i=0;i<=5;i++){
                    cultural_events_counters[i]=(long) values.get("Event1"+i);
                }
                for (int i=0;i<=4;i++){
                    literary_events_counters[i]=(long) values.get("Event2"+i);
                }
                for (int i=0;i<=5;i++){
                    photography_events_counters[i]=(long) values.get("Event3"+i);
                }
                Log.e("admin","total:"+total_reg);
                //now make all the charts
                makePieChart();
                makeBarChart1();
                makeBarChart2();
                makeBarChart3();
                makeBarChart4();

                //todo currently i am not updating the graphs in realtime but will do later
                //we will have to update the values with .updateValues method in the horizontal bar charts otherwise charts
                // will crash if we create new chart every time
                ref.removeEventListener(this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("admin",databaseError.getMessage());
            }
        });
    }

    public void makePieChart(){
        pieChart.addPieSlice(new PieModel("Technical Club",technical_reg, Color.parseColor("#FE6DA8")));
        pieChart.addPieSlice(new PieModel("Cultural Club",cultural_reg, Color.parseColor("#56B7F1")));
        pieChart.addPieSlice(new PieModel("Literary and Dramatics Club",literary_reg, Color.parseColor("#CDA67F")));
        pieChart.addPieSlice(new PieModel("Fine Arts and Photography Club",photography_reg, Color.parseColor("#FED70E")));
        pieChart.setCurrentItem(club_number);
        pieChart.setAutoCenterInSlice(true);
        pieChart.setDrawValueInPie(true);
        isPieChartReady=true;
        //pieChart.startAnimation();
    }

    public void makeBarChart1(){
        String[] labels=provider.getEventNames(0);
        if (labels==null){
            new MyToast(getActivity(), "Reload this page after connecting to internet.", Toast.LENGTH_LONG,false).show();
            getActivity().finish();
        }
        barchart_label1.animate().alpha(0).setDuration(100);

        Tooltip tip=new Tooltip(getActivity());
        tip.setBackgroundColor(Color.parseColor("#f39c12"));
        tip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA,1)).setDuration(150);
        tip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA,0)).setDuration(150);

        barChart1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barchart_label1.animate().alpha(0).setDuration(100);
            }
        });

        barChart1.setOnEntryClickListener(new OnEntryClickListener() {
            @Override
            public void onClick(int setIndex, int entryIndex, Rect rect) {
                barchart_label1.setText(String.format(Locale.ENGLISH,"%d", technical_events_counters[entryIndex]));
                barchart_label1.animate().alpha(1).setDuration(200);
            }
        });

        barChart1.setTooltips(tip);
        BarSet barSet=new BarSet();
        Bar bar;
        for (int i=0;i<labels.length;i++){
            bar=new Bar(labels[i],technical_events_counters[i]);
            switch (i){
                case 0: bar.setColor(Color.parseColor("#77c63d"));
                break;
                case 1: bar.setColor(Color.parseColor("#27ae60"));
                    break;
                case 2: bar.setColor(Color.parseColor("#47bac1"));
                    break;
                case 3: bar.setColor(Color.parseColor("#16a085"));
                    break;
                case 4: bar.setColor(Color.parseColor("#3498db"));
                    break;
                case 5: bar.setColor(Color.parseColor("#212121"));
                    break;
                default: break;
            }
            barSet.addBar(bar);
        }
        barChart1.addData(barSet);
        barChart1.setXLabels(XRenderer.LabelPosition.NONE).show(new Animation().inSequence(.5f,order));
    }

    public void makeBarChart2(){
        String[] labels=provider.getEventNames(1);
        barchart_label2.animate().alpha(0).setDuration(100);
        Tooltip tip=new Tooltip(getActivity());
        tip.setBackgroundColor(Color.parseColor("#f39c12"));
        tip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA,1)).setDuration(150);
        tip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA,0)).setDuration(150);

        barChart2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barchart_label2.animate().alpha(0).setDuration(100);
            }
        });

        barChart2.setOnEntryClickListener(new OnEntryClickListener() {
            @Override
            public void onClick(int setIndex, int entryIndex, Rect rect) {
                barchart_label2.setText(String.format(Locale.ENGLISH,"%d",cultural_events_counters[entryIndex]));
                barchart_label2.animate().alpha(1).setDuration(200);
            }
        });

        barChart2.setTooltips(tip);
        BarSet barSet=new BarSet();
        Bar bar;
        for (int i=0;i<labels.length;i++){
            bar=new Bar(labels[i],cultural_events_counters[i]);
            switch (i){
                case 0: bar.setColor(Color.parseColor("#77c63d"));
                    break;
                case 1: bar.setColor(Color.parseColor("#27ae60"));
                    break;
                case 2: bar.setColor(Color.parseColor("#47bac1"));
                    break;
                case 3: bar.setColor(Color.parseColor("#16a085"));
                    break;
                case 4: bar.setColor(Color.parseColor("#3498db"));
                    break;
                case 5: bar.setColor(Color.parseColor("#212121"));
                    break;
                default: break;
            }
            barSet.addBar(bar);
        }
        barChart2.addData(barSet);
        barChart2.setXLabels(XRenderer.LabelPosition.NONE).show(new Animation().inSequence(.5f,order));
    }

    public void makeBarChart3(){
        String[] labels=provider.getEventNames(2);
        barchart_label3.animate().alpha(0).setDuration(100);
        Tooltip tip=new Tooltip(getActivity());
        tip.setBackgroundColor(Color.parseColor("#f39c12"));
        tip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA,1)).setDuration(150);
        tip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA,0)).setDuration(150);

        barChart3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barchart_label3.animate().alpha(0).setDuration(100);
            }
        });

        barChart3.setOnEntryClickListener(new OnEntryClickListener() {
            @Override
            public void onClick(int setIndex, int entryIndex, Rect rect) {
                barchart_label3.setText(String.format(Locale.ENGLISH,"%d", literary_events_counters[entryIndex]));
                barchart_label3.animate().alpha(1).setDuration(200);
            }
        });

        barChart3.setTooltips(tip);
        BarSet barSet=new BarSet();
        Bar bar;
        for (int i=0;i<labels.length;i++){
            bar=new Bar(labels[i],literary_events_counters[i]);
            switch (i){
                case 0: bar.setColor(Color.parseColor("#77c63d"));
                    break;
                case 1: bar.setColor(Color.parseColor("#27ae60"));
                    break;
                case 2: bar.setColor(Color.parseColor("#47bac1"));
                    break;
                case 3: bar.setColor(Color.parseColor("#16a085"));
                    break;
                case 4: bar.setColor(Color.parseColor("#3498db"));
                    break;
                case 5: bar.setColor(Color.parseColor("#212121"));
                    break;
                default: break;
            }
            barSet.addBar(bar);
        }
        barChart3.addData(barSet);
        int[] order={4,3,2,1,0};
        barChart3.setXLabels(XRenderer.LabelPosition.NONE).show(new Animation().inSequence(.5f,order));
    }

    public void makeBarChart4(){
        String[] labels=provider.getEventNames(3);
        barchart_label4.animate().alpha(0).setDuration(100);
        Tooltip tip=new Tooltip(getActivity());
        tip.setBackgroundColor(Color.parseColor("#f39c12"));
        tip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA,1)).setDuration(150);
        tip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA,0)).setDuration(150);

        barChart4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barchart_label4.animate().alpha(0).setDuration(100);
            }
        });

        barChart4.setOnEntryClickListener(new OnEntryClickListener() {
            @Override
            public void onClick(int setIndex, int entryIndex, Rect rect) {
                barchart_label4.setText(String.format(Locale.ENGLISH,"%d", photography_events_counters[entryIndex]));
                barchart_label4.animate().alpha(1).setDuration(200);
            }
        });

        barChart4.setTooltips(tip);
        BarSet barSet=new BarSet();
        Bar bar;
        for (int i=0;i<labels.length;i++){
            bar=new Bar(labels[i],photography_events_counters[i]);
            switch (i){
                case 0: bar.setColor(Color.parseColor("#77c63d"));
                    break;
                case 1: bar.setColor(Color.parseColor("#27ae60"));
                    break;
                case 2: bar.setColor(Color.parseColor("#47bac1"));
                    break;
                case 3: bar.setColor(Color.parseColor("#16a085"));
                    break;
                case 4: bar.setColor(Color.parseColor("#3498db"));
                    break;
                case 5: bar.setColor(Color.parseColor("#212121"));
                    break;
                default: break;
            }
            barSet.addBar(bar);
        }
        barChart4.addData(barSet);
        barChart4.setXLabels(XRenderer.LabelPosition.NONE).show(new Animation().inSequence(.5f,order));
    }

}
