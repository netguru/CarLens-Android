# Car Regonition

| environment | google play           | app center         | google play   |
|-------------|-----------------------|--------------------|---------------|
| Android     | [Google Play](https://play.google.com/store/apps/details?id=co.netguru.android.carrecognition) |  [App Center](https://appcenter.ms/orgs/office-4dmm/apps/car-recognition-android)| [![Build Status](https://app.bitrise.io/app/9daec990ebe15a1e/status.svg?token=gGmNVn-KF3WX1axova7c3A&branch=master)](https://app.bitrise.io/app/9daec990ebe15a1e) |
<!--- If applies, add link to app on Google Play -->

## About
Welcome to the **Car Regonition** project. It's an internal application made for detecting cars and showing informations about them.

## Testing
 - Unit Testing using Mockito + JUnit. Tests concerns Presnters layer and partially Model layer of MVP architecture

## Building
1. Clone repository:

	```bash
	# over https:
	git clone https://github.com/netguru/car-recognition-android.git
	# or over SSH:
	git@github.com:netguru/car-recognition-android.git
	```
2. Place `secrets.properties` file in application root folder:
   ```bash
   AppCenterAppId=ACTUAL_KEY
   # (...)
   ```  
3. Open the project with Android Studio.

### Build types

#### Debug
 - debuggable,
 - disabled ProGuard,
 - uses built-in shrinking (no obfuscation),
 - disabled crash reporting.
 
#### Staging
 - non-debuggable,
 - uses full ProGuard configuration,
 - enables zipAlign, shrinkResources,
 - enabled crash reporting.
 
#### Release
 - non-debuggable,
 - uses full ProGuard configuration,
 - enables zipAlign, shrinkResources,
 - enabled crash reporting.

### Build properties

| Property             | External property name | Environment variable |
|----------------------|------------------------|----------------------|
| App Center App ID    | AppCenterAppId         | APP_CENTER_APP_ID    |
| Sonar access token   | -                      | SONAR_ACCESS_TOKEN   |

### Bitrise
 Bitrise is separated for workflow mentioned below. Feature, MasterPr and Master workflows are responsible for builds stagingRelease flavours of the project which is used for developing new features.
 - feature - workflow triggered on Push or PR marge to develop branch;
 - develop - workflow triggered on Push to develop branch;
 - production - workflow triggered on Push to production branch;

### AppCenter / Fabric environments
 - Project use App Center for beta distribution and Crash managing and monitoring. Deploy to App Center is performed automatically by Bitrise system in develop and production step.

### Supported devices
Supported devices are listed here: https://developers.google.com/ar/discover/supported-devices 

## Related repositories
- [iOS](https://github.com/netguru/car-recognition-ios)
- [Machine Learning](https://github.com/netguru/car-recognition-ml)
