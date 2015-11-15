package com.novoda.simplechromecustomtabs.demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.novoda.simplechromecustomtabs.SimpleChromeCustomTabs;
import com.novoda.simplechromecustomtabs.navigation.SimpleChromeCustomTabsIntentBuilder;
import com.novoda.simplechromecustomtabs.navigation.IntentCustomizer;
import com.novoda.simplechromecustomtabs.navigation.NavigationFallback;

import static com.novoda.simplechromecustomtabs.provider.SimpleChromeCustomTabsAvailableAppProvider.PackageFoundCallback;

public class ExtendedDemoActivity extends AppCompatActivity {

    private static final Uri WEB_URL = Uri.parse("http://www.novoda.com");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.simple_demo_layout);
        findViewById(R.id.open_url_button).setOnClickListener(openUrlButtonClickListener);
    }

    private final View.OnClickListener openUrlButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SimpleChromeCustomTabs.getInstance().withFallback(navigationFallback)
                    .withIntentCustomizer(intentCustomizer)
                    .navigateTo(WEB_URL, ExtendedDemoActivity.this);
        }
    };

    private final NavigationFallback navigationFallback = new NavigationFallback() {
        @Override
        public void onFallbackNavigateTo(Uri url) {
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .setData(url)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    };

    private final IntentCustomizer intentCustomizer = new IntentCustomizer() {
        @Override
        public SimpleChromeCustomTabsIntentBuilder onCustomiseIntent(SimpleChromeCustomTabsIntentBuilder simpleChromeCustomTabsIntentBuilder) {
            //TODO customize a bit more.
            return simpleChromeCustomTabsIntentBuilder.withToolbarColor(ContextCompat.getColor(getApplicationContext(), android.R.color.black))
                    .showingTitle()
                    .withUrlBarHiding();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        checkForPackageAvailable();
        SimpleChromeCustomTabs.getInstance().connectTo(this);
    }

    private void checkForPackageAvailable() {
        SimpleChromeCustomTabs.getInstance().findBestPackage(packageFoundCallback);
    }

    private final PackageFoundCallback packageFoundCallback = new PackageFoundCallback() {
        @Override
        public void onPackageFound(String packageName) {
            View contentView = findViewById(android.R.id.content);
            Snackbar.make(contentView, R.string.application_found, Snackbar.LENGTH_INDEFINITE).show();
        }

        @Override
        public void onPackageNotFound() {
            View contentView = findViewById(android.R.id.content);
            Snackbar.make(contentView, R.string.application_not_found, Snackbar.LENGTH_INDEFINITE).show();
        }
    };

    @Override
    protected void onPause() {
        SimpleChromeCustomTabs.getInstance().disconnectFrom(this);

        super.onPause();
    }

}
