package com.wootric.analytics.android.integrations.wootric;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.segment.analytics.Traits;
import com.segment.analytics.ValueMap;
import com.segment.analytics.integrations.IdentifyPayload;
import com.segment.analytics.integrations.Integration;
import com.segment.analytics.integrations.TrackPayload;
import com.segment.analytics.internal.Utils;
import com.wootric.androidsdk.Wootric;

import java.util.Date;
import java.util.HashMap;

import static com.segment.analytics.internal.Utils.isNullOrEmpty;

/**
 * Created by maciejwitowski on 11/4/15.
 */
public class WootricIntegration extends Integration<Wootric> {

    public static final Factory FACTORY = new Integration.Factory() {
        @Override
        public Integration<?> create(ValueMap settings, Analytics analytics) {
            String accountToken = settings.getString("accountToken");

            return new WootricIntegration(accountToken);
        }

        @Override
        public String key() {
            return WOOTRIC_KEY;
        }
    };

    private static final String WOOTRIC_KEY = "Wootric";

    final String accountToken;

    Wootric wootric;
    String endUserEmail;
    String eventName;
    long endUserCreatedAt = -1;
    HashMap<String, String> endUserProperties;

    public WootricIntegration(String accountToken) {
        this.accountToken = accountToken;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);
        if (activity instanceof FragmentActivity) {
            wootric = Wootric.init((FragmentActivity) activity, accountToken);
        } else {
            wootric = Wootric.init(activity, accountToken);
        }
        updateEndUserAttributes();
    }

    @Override
    public Wootric getUnderlyingInstance() {
        return wootric;
    }

    @Override
    public void identify(IdentifyPayload identify) {
        super.identify(identify);

        Traits traits = identify.traits();

        endUserEmail = traits.email();
        endUserCreatedAt = dateToLong(traits.createdAt());

        endUserProperties = (HashMap<String, String>) traits.toStringMap();
        endUserProperties.remove("email");
        endUserProperties.remove("createdAt");

        if(wootric != null) {
            updateEndUserAttributes();
        }
    }

    @Override
    public void track(TrackPayload track) {
        Properties properties = track.properties();
        this.eventName = track.event();
        String language = properties.getString("language");

        if (endUserProperties == null) {
            endUserProperties = (HashMap<String, String>) properties.toStringMap();
        } else {
            endUserProperties.putAll(properties.toStringMap());
        }

        if (language != null) {
            wootric.setLanguageCode(language);
        }

        if(wootric != null) {
            wootric.setEventName(this.eventName);
            updateEndUserAttributes();
        }

    }

    private void updateEndUserAttributes() {
        wootric.setEndUserEmail(endUserEmail);
        wootric.setEndUserCreatedAt(endUserCreatedAt);
        wootric.setProperties(endUserProperties);
    }

    private long dateToLong(String dateString) {
        if(isNullOrEmpty(dateString)) {
            return -1;
        }

        try {
            Date date = Utils.parseISO8601Date(dateString);
            return date.getTime() / 1000;
        } catch (Exception e) {
            return Long.valueOf(dateString);
        }
    }
}
