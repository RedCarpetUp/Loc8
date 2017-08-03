# Location and Fake Location Detector 

<img align="center" src='https://github.com/balrampandey19/FakeLocationDetctor/blob/master/Screen/loc8.png' width='200' height='200'/>

Loc8 is a android location manager library to get accurate location simply.

This libray use [Android LocationManager](https://developer.android.com/reference/android/location/LocationManager.html)  and very cool and optimized Google play service location provider api [FusedLocationProvider](https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderApi)uses a mix of hardware to determine location based on the context of the request, meaning it's optimized transparently to you. It will also cache captured locations between applications to avoid unnecessary work to determine location info. So if a user has a variety of location-aware apps, they potentially avoid taxing the device (and waiting) for a location capture as one may have already been cached.
## By using logy you can get

* Get accurate current location.
* Check is locktion is Mocked.
* Check whether the application has required permission or not
* Check whether GPS Provider is enabled or not
* Check whether Network Provider is enabled or not

# Usage

#### Get location with log8
'''
 Loc8 loc8 = Loc8.getInstance(mcontext, Loc8.DEFAULT);
        loc8.getLocation(new LocationCallback() {
            @Override
            public void onError(String error) {
               
          }

            @Override
            public void onSuccess(Location location) {
              }
        });
        
'''



# Installation




# License

```
      Copyright 2016 Balram Pandey

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

```




