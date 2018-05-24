package com.wootric.analytics.android.integrations.wootric;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.segment.analytics.Analytics;
import com.segment.analytics.Traits;
import com.segment.analytics.ValueMap;
import com.segment.analytics.integrations.IdentifyPayload;
import com.segment.analytics.integrations.Integration;
import com.segment.analytics.internal.Utils;
import com.wootric.androidsdk.Wootric;

import java.text.ParseException;
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
            String clientId = settings.getString("clientId");
            String accountToken = settings.getString("accountToken");

            return new WootricIntegration(clientId, accountToken);
        }

        @Override
        public String key() {
            return WOOTRIC_KEY;
        }
    };

    private static final String WOOTRIC_KEY = "Wootric";

    final String clientId;
    final String accountToken;

    Wootric wootric;
    String endUserEmail;
    long endUserCreatedAt = -1;
    HashMap<String, String> endUserProperties;

    public WootricIntegration(String clientId, String accountToken) {
        this.clientId = clientId;
        this.accountToken = accountToken;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);
        wootric = Wootric.init((FragmentActivity) activity, clientId, accountToken);
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
            Date date = Utils.toISO8601Date(dateString);
            return date.getTime();
        } catch (ParseException e) {
            return Long.valueOf(dateString);
        }
    }
}
