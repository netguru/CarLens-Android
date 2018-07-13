<!-- 
    Couple of points about editing:
    
    1. Keep it SIMPLE.
    2. Refer to reference docs and other external sources when possible.
    3. Remember that the file must be useful for new / external developers, and stand as a documentation basis on its own.
    4. Try to make it as informative as possible.
    5. Do not put data that can be easily found in code.
    6. Include this file on ALL branches.
-->

<!-- Put your project's name -->
# netguru-android-template

<!-- METADATA -->
<!-- Add links to JIRA, Google Drive, mailing list and other relevant resources -->
<!-- Add links to CI configs with build status and deployment environment, e.g.: -->
| environment | google play           | status             | test coverage |
|-------------|-----------------------|--------------------|---------------|
| Android     | <!-- google play link --> | [![Build Status](https://app.bitrise.io/app/9daec990ebe15a1e/status.svg?token=gGmNVn-KF3WX1axova7c3A&branch=master)](https://app.bitrise.io/app/9daec990ebe15a1e) | [![codecov](https://codecov.io/gh/netguru/car-recognition-android/branch/master/graph/badge.svg?token=1GVnGdZt3A)](https://codecov.io/gh/netguru/car-recognition-android) |
<!--- If applies, add link to app on Google Play -->

## About
Welcome to the **Car Regonition** project. It's an internal application made for detecting cars and showing informations about them.

## Team

* [Miłosz Szyński](mailto:milosz.szynski@netguru.pl) - Project Manager
* [Paweł Bocheński](mailto:pawel.bochenski@netguru.pl) - Senior Android developer
* [Marcin Oziemski](mailto:marcin.oziemski@netguru.pl) - Android Developer
* [Marcin Stramowski](mailto:pawel.bochenski@netguru.pl) - Android Developer

## Testing
<!-- Describe the project's testing methodology -->
<!-- Examples: TDD? Using Espresso for views? What parts must be tested? etc -->
 - Unit Testing using Mockito + JUnit. Tests concerns Presnters layer and partially Model layer of MVP architecture

## Building
<!-- Aim to explain the process so that any new or external developer not familiar with the project can perform build and deploy -->

### Build types

#### debug
 - debuggable
 - disabled ProGuard
 - uses built-in shrinking (no obfuscation)
 - disabled crash reporting
 
#### staging
 - non-debuggable
 - uses full ProGuard configuration
 - enables zipAlign, shrinkResources
 - enabled crash reporting
 
#### release
 - non-debuggable
 - uses full ProGuard configuration
 - enables zipAlign, shrinkResources
 - enabled crash reporting

### Build properties
<!-- List all build properties that have to be supplied, including secrets. Describe the method of supplying them, both on local builds and CI -->

| Property             | External property name | Environment variable |
|----------------------|------------------------|----------------------|
| HockeyApp App ID     | HockeyAppId            | HOCKEY_APP_ID        |
| Sonar access token   | -                      | SONAR_ACCESS_TOKEN   |

#### Secrets
Place `secrets.properties` file in application root folder
```
HockeyAppId=ACTUAL_KEY
```

#### Supported devices
Supported devices are listed here: https://developers.google.com/ar/discover/supported-devices 
