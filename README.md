Wootric integration for [analytics-android](https://github.com/segmentio/analytics-android).

## Installation
This library is distributed as Android library project so it can be included by referencing it as a library project.

If you use Maven, you can include this library as a dependency:

```xml
<dependency>
    <groupId>com.wootric</groupId>
    <artifactId>analytics-integration-wootric</artifactId>
    <version>0.1.5</version>
</dependency>
```

For Gradle users:

```groovy
compile 'com.wootric:analytics-integration-wootric:0.1.5'
```

It is also assumed that Segment's android analytics is available. The latest version can be added as dependency this way:

```groovy
dependencies {
  repositories {
    mavenCentral()
    maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
  }

  compile 'com.segment.analytics.android:analytics:4.2.2'
}
```

## Usage

(It is advised to read [WootricSDK docs](https://github.com/Wootric/WootricSDK-Android) first.)

Analytics object should be initialized in the Application class, specifying that the WootricIntegration should be user.

```java
import com.segment.analytics.Analytics;
import com.wootric.analytics.android.integrations.wootric.WootricIntegration;

public class MainApplication extends Application {

    Analytics analytics;

    @Override
    public void onCreate() {
        super.onCreate();

        analytics = new Analytics.Builder(this, "write_key")
                .use(WootricIntegration.FACTORY)
                .build();
    }

    public Analytics getAnalytics() {
        return analytics;
    }
}
```

Afterwards in the activity the Wootric object can be used to set all optional configurations and call `survey()` method.

```java

import com.segment.analytics.Analytics;
import com.wootric.analytics.android.integrations.wootric.WootricIntegration;
import com.wootric.androidsdk.Wootric;

public class MainActivity extends Activity {
    @Override
    protected void onResume() {
        super.onResume();

        Analytics analytics = ((MainApplication) getApplication()).getAnalytics();
        analytics.onIntegrationReady(WootricIntegration.FACTORY.key(), new Analytics.Callback<Wootric>() {
            @Override
            public void onReady(Wootric wootric) {
                // Set all aptional configuration here like:
                // wootric.setSurveyImmediately(true);
                // wootric.setLanguageCode("PL");
                wootric.survey();
            }
        });
    }
}
```

## License

```
The MIT License (MIT)

Copyright (c) 2015 Wootric

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
