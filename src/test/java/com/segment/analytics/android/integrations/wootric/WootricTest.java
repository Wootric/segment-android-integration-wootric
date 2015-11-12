package com.segment.analytics.android.integrations.wootric;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.segment.analytics.Analytics;
import com.segment.analytics.Traits;
import com.segment.analytics.ValueMap;
import com.segment.analytics.test.IdentifyPayloadBuilder;
import com.wootric.androidsdk.Wootric;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by maciejwitowski on 11/4/15.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "android.*", "org.json.*" })
@PrepareForTest(Wootric.class)
public class WootricTest {

    @Mock
    Analytics analytics;
    @Mock Wootric wootric;
    @Mock Context context;

    WootricIntegration integration;

    @Before
    public void setUp() {
        initMocks(this);
        integration = new WootricIntegration("client_id", "client_secret", "account_token");
    }

    @Test
    public void factory() {
        ValueMap settings = new ValueMap().putValue("clientId", "client_id")
                .putValue("clientSecret", "client_secret")
                .putValue("accountToken", "account_token");

        WootricIntegration wootricIntegration =
                (WootricIntegration) WootricIntegration.FACTORY.create(settings, analytics);

        assertThat(wootricIntegration.accountToken).isEqualTo("account_token");
        assertThat(wootricIntegration.clientId).isEqualTo("client_id");
        assertThat(wootricIntegration.clientSecret).isEqualTo("client_secret");
    }

    @Test public void activityCreate() {
        Activity activity = mock(Activity.class);
        Bundle bundle = mock(Bundle.class);
        when(activity.getApplicationContext()).thenReturn(context);

        integration.onActivityCreated(activity, bundle);
        assertThat(integration.wootric).isEqualTo(Wootric.get());
    }

    @Test public void identify() {
        integration.wootric = mock(Wootric.class);
        integration.identify(new IdentifyPayloadBuilder()
                .traits(new Traits().putEmail("nps@example.com")).build());
        verify(integration.wootric).setEndUserEmail("nps@example.com");
    }
}
