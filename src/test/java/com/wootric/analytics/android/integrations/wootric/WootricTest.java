package com.wootric.analytics.android.integrations.wootric;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.segment.analytics.Analytics;
import com.segment.analytics.Traits;
import com.segment.analytics.ValueMap;
import com.segment.analytics.integrations.IdentifyPayload;
import com.segment.analytics.integrations.TrackPayload;
import com.segment.analytics.internal.Utils;
import com.wootric.androidsdk.Wootric;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by maciejwitowski on 11/4/15.
 */

@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "android.*", "org.json.*" })
@PrepareForTest(Wootric.class)
public class WootricTest {

    @Mock
    Analytics analytics;
    @Mock Wootric wootric;
    @Mock Context context;
    @Mock Resources mResources;
    @Mock FragmentActivity mFragmentActivity;
    @Mock Window mWindow;

    WootricIntegration integration;

    @Before
    public void setUp() {
        initMocks(this);
        integration = new WootricIntegration("account_token");
    }

    @Test
    public void factory() {
        ValueMap settings = new ValueMap().putValue("accountToken", "account_token");

        WootricIntegration wootricIntegration =
                (WootricIntegration) WootricIntegration.FACTORY.create(settings, analytics);

        assertThat(wootricIntegration.accountToken).isEqualTo("account_token");
    }

    @Test public void activityCreate() {
        Activity activity = mock(Activity.class);
        Bundle bundle = mock(Bundle.class);
        when(mResources.getBoolean(R.bool.isTablet)).thenReturn(true);
        when(activity.getResources()).thenReturn(mResources);
        when(activity.getWindow()).thenReturn(mWindow);
        when(activity.getApplicationContext()).thenReturn(context);

        integration.onActivityCreated(activity, bundle);
        assertThat(integration.wootric).isEqualTo(Wootric.init(activity, ""));
    }

    @Test public void fragmentActivityCreate() {
        FragmentActivity fragmentActivity = mock(FragmentActivity.class);
        Bundle bundle = mock(Bundle.class);
        when(mResources.getBoolean(R.bool.isTablet)).thenReturn(true);
        when(fragmentActivity.getResources()).thenReturn(mResources);
        when(fragmentActivity.getWindow()).thenReturn(mWindow);
        when(fragmentActivity.getApplicationContext()).thenReturn(context);

        integration.onActivityCreated(fragmentActivity, bundle);
        assertThat(integration.wootric).isEqualTo(Wootric.init(fragmentActivity, ""));
    }

    @Test public void identify() {
        integration.wootric = mock(Wootric.class);

        String timeNow = "2019-01-31T23:00:00+00:00";
        Long timeUnixTimestamp = Utils.parseISO8601Date(timeNow).getTime() / 1000;
        Traits traits = new Traits();
        traits.putEmail("nps@example.com");
        traits.putCreatedAt(timeNow);
        traits.put("company", "wootric");
        traits.put("plan", "basic");

        integration.identify(new IdentifyPayload.Builder().userId("user_id").traits(traits).build());


        assertThat(integration.endUserEmail).isEqualTo("nps@example.com");
        assertThat(integration.endUserCreatedAt).isEqualTo(timeUnixTimestamp);

        HashMap properties = integration.endUserProperties;
        assertThat(properties.get("company")).isEqualTo("wootric");
        assertThat(properties.get("plan")).isEqualTo("basic");

        verify(integration.wootric).setEndUserEmail("nps@example.com");
        verify(integration.wootric).setEndUserCreatedAt(timeUnixTimestamp);
        verify(integration.wootric).setProperties(properties);
    }

    @Test public void track() {
        integration.wootric = mock(Wootric.class);

        integration.track((new TrackPayload.Builder()).anonymousId("1234").event("On click").build());

        assertThat(integration.eventName).isEqualTo("On click");
    }
}
