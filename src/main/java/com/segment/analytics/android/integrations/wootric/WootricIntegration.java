package com.segment.analytics.android.integrations.wootric;

import android.app.Activity;
import android.os.Bundle;

import com.segment.analytics.Analytics;
import com.segment.analytics.Traits;
import com.segment.analytics.ValueMap;
import com.segment.analytics.integrations.IdentifyPayload;
import com.segment.analytics.integrations.Integration;
import com.wootric.androidsdk.Wootric;

/**
 * Created by maciejwitowski on 11/4/15.
 */
public class WootricIntegration extends Integration<Wootric> {

    public static final Factory FACTORY = new Integration.Factory() {
        @Override
        public Integration<?> create(ValueMap settings, Analytics analytics) {
            String clientId = settings.getString("clientId");
            String clientSecret = settings.getString("clientSecret");
            String accountToken = settings.getString("accountToken");

            return new WootricIntegration(clientId, clientSecret, accountToken);
        }

        @Override
        public String key() {
            return WOOTRIC_KEY;
        }
    };

    private static final String WOOTRIC_KEY = "Wootric";

    Wootric wootric;
    final String clientId;
    final String clientSecret;
    final String accountToken;

    public WootricIntegration(String clientId, String clientSecret, String accountToken) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.accountToken = accountToken;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);
        wootric = Wootric.init(activity, clientId, clientSecret, accountToken);
    }

    @Override
    public Wootric getUnderlyingInstance() {
        return wootric;
    }

    @Override
    public void identify(IdentifyPayload identify) {
        super.identify(identify);

        Traits traits = identify.traits();

        String email = traits.email();
        wootric.setEndUserEmail(email);
    }
}
