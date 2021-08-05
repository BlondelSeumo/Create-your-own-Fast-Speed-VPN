package fast.pi.vpn.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

public class BackgroundJobService extends JobService {

    private static final String TAG = "BackgroundJobService";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        //Log.e(TAG, "Started Job ID--" + jobParameters.getJobId());
        startService(new Intent(getApplicationContext(), MyService.class));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        //Log.e(TAG,"Stopped Job ID--"+jobParameters.getJobId());
        return false;
    }
}
