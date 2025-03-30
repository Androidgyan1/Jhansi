package com.jhansi.FontsUitils;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(getFixedContext(newBase));
    }

    private Context getFixedContext(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return createConfigurationContext(getFixedConfiguration(context));
        } else {
            context.getResources().updateConfiguration(
                    getFixedConfiguration(context),
                    context.getResources().getDisplayMetrics()
            );
            return context;
        }
    }

    private Configuration getFixedConfiguration(Context context) {
        Configuration config = context.getResources().getConfiguration();
        config.fontScale = 1.0f; // Set fixed font scale
        return config;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Maintain fixed font scale when configuration changes
        if (newConfig.fontScale != 1.0f) {
            Configuration fixedConfig = new Configuration(newConfig);
            fixedConfig.fontScale = 1.0f;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                applyOverrideConfiguration(fixedConfig);
            } else {
                getResources().updateConfiguration(fixedConfig, getResources().getDisplayMetrics());
            }
        }
    }
}