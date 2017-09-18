package com.dan.hotpopcorn;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.dan.hotpopcorn.model.Movie;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;


public class DetailActivity extends AppCompatActivity {


    Movie movie;
    @Bind(R.id.toolImage)
    ImageView toolImage;
    @Bind(R.id.imageView)
    ImageView imageView;
    @Bind(R.id.titleView)
    TextView titleView;
    @Bind(R.id.rating)
    TextView rating;
    @Bind(R.id.overview)
    TextView overview;
    @Bind(R.id.releaseText)
    TextView releaseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        //get data from intent
        movie = (Movie) getIntent().getParcelableExtra("movie");
        //set each view with specific text or images
        Picasso.with(this).load(movie.getBackdrop()).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(toolImage);
        Picasso.with(this).load(movie.getPoster()).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(imageView);
        titleView.setText(movie.getTitle());
        rating.setText(getString(R.string.Rating)+ " "+ Double.toString(movie.getRating()) + "/10");
        releaseText.setText(getString(R.string.release_on) +" "+ movie.getReleaseDate());
        overview.setText(movie.getOverview());
    }
}
