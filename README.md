# ImagePicker

[![Build Status](https://travis-ci.org/tikivn/ImagePicker.svg?branch=master)](https://travis-ci.org/tikivn/ImagePicker)

One picker to rule them all.

![](logo.png)

## Install
Declare `provider`

  - `AndroidManifest.xml`
    ~~~xml
      <application>
        ...
        <provider
          android:name="android.support.v4.content.FileProvider"
          android:authorities="PACKAGE_NAME.file_provider"
          android:exported="false"
          android:grantUriPermissions="true">
          <meta-data
            android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/file_paths"/>
        </provider>
        ...
      </application>
    ~~~   
  - @xml/file_paths
    ~~~xml
    <?xml version="1.0" encoding="utf-8"?>
    <paths xmlns:android="http://schemas.android.com/apk/res/android">
        <external-path name="my_images" path="Android/data/PACKAGE_NAME/files/Pictures" />
    </paths
    ~~~
  - PACKAGE_NAME: app's package name
 
See more in the sample

## Download

Download [the latest JAR][1] or grab via Gradle:
```groovy
compile 'vn.tiki.imagepicker:imagepicker:1.0.0'
```
or Maven:
```xml
<dependency>
  <groupId>vn.tiki.imagepicker</groupId>
  <artifactId>imagepicker</artifactId>
  <version>1.0.0</version>
</dependency>
```

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].



## ProGuard

No specific


## License

    Copyright 2016 Tiki Corp

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


 [1]: https://search.maven.org/remote_content?g=vn.tiki.imagepicker&a=imagepicker&v=LATEST
 [snap]: https://oss.sonatype.org/content/repositories/snapshots/
