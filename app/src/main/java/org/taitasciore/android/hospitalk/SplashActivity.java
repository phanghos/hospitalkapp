package org.taitasciore.android.hospitalk;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import org.taitasciore.android.hospitalk.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by roberto on 03/05/17.
 */

public class SplashActivity extends AppCompatActivity {

    private static final int DELAY = 3000;

    @BindView(R.id.progressBar) ProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        new SimulateLoadingTaskAsync().execute();
    }

    private class SimulateLoadingTaskAsync extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setProgress(0);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 1; i <= 5; i++) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress(i * 20);
                if (isCancelled()) break;
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mProgressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }
}
