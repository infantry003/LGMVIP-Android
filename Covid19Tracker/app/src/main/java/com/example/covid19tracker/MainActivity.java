package com.example.covid19tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.covid19tracker.api.ApiUtilities;
import com.example.covid19tracker.api.CountryData;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView totalConfirmed, todayConfirmed, date, totalActive, totalDeceased, todayDeceased, totalRecovered, todayRecovered, totalTested;
    private PieChart pieChart;

    private List<CountryData> list;
    String country = "India";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = new ArrayList<>();
        if (getIntent().getStringExtra("country") != null)
            country = getIntent().getStringExtra("country");


        init();

        TextView cname = findViewById(R.id.cname);
        cname.setText(country);


        cname.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CountryActivity.class)));

        ApiUtilities.getApiInterface().getCountryData()
                .enqueue(new Callback<List<CountryData>>() {
                    @Override
                    public void onResponse(Call<List<CountryData>> call, Response<List<CountryData>> response) {
                        list.addAll(response.body());

                        for (int i = 0; i<list.size(); i++){
                            if (list.get(i).getCountry().equals(country)) {
                                int confirmed = Integer.parseInt(list.get(i).getCases());
                                int active = Integer.parseInt(list.get(i).getActive());
                                int deceased = Integer.parseInt(list.get(i).getDeaths());
                                int recovered = Integer.parseInt(list.get(i).getRecovered());

                                totalConfirmed.setText(NumberFormat.getInstance().format(confirmed));
                                totalActive.setText(NumberFormat.getInstance().format(active));

                                totalDeceased.setText(NumberFormat.getInstance().format(deceased));

                                totalRecovered.setText(NumberFormat.getInstance().format(recovered));


                                todayConfirmed.setText(NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayCases())));
                                todayDeceased.setText(NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayDeaths())));

                                todayRecovered.setText(NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayRecovered())));
                                totalTested.setText(NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTests())));


                                setText(list.get(i).getUpdated());

                                pieChart.addPieSlice(new PieModel("Confirmed", confirmed, getResources().getColor(R.color.yellow)));  //Resources from EazeGraph
                                pieChart.addPieSlice(new PieModel("Active", active, getResources().getColor(R.color.blue_pie)));      //Resources from EazeGraph
                                pieChart.addPieSlice(new PieModel("Deceased", deceased, getResources().getColor(R.color.red_pie)));   //Resources from EazeGraph
                                pieChart.addPieSlice(new PieModel("Recovered", recovered, getResources().getColor(R.color.green_pie)));   //Resources from EazeGraph

                                pieChart.startAnimation();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CountryData>> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Error : "+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setText(String updated) {
        DateFormat format = new SimpleDateFormat("dd MMM yyyy");

        long milliseconds = Long.parseLong(updated);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        date.setText("Last Updated on "+format.format(calendar.getTime()));

    }

    private void init() {

        totalConfirmed = findViewById(R.id.totalConfirmed);
        todayConfirmed = findViewById(R.id.todayConfirmed);
        totalActive = findViewById(R.id.totalActive);
        totalDeceased = findViewById(R.id.totalDeceased);
        todayDeceased = findViewById(R.id.todayDeceased);
        totalRecovered = findViewById(R.id.totalRecovered);
        todayRecovered = findViewById(R.id.todayRecovered);
        totalTested = findViewById(R.id.totalTested);
        pieChart = findViewById(R.id.pieChart);
        date = findViewById(R.id.date);
    }
}