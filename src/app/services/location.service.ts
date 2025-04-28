import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LocationService {

  constructor(private http: HttpClient) {}

  getLocation() {
    const url = `https://www.googleapis.com/geolocation/v1/geolocate?key=AIzaSyACHVvWbKBkZ1He6kXYXwm4ZCaKi7TTO54`;
    return this.http.post(url, {}); // Payload is empty
  }
  getCurrentLocation(): Promise<GeolocationPosition> {
    return new Promise((resolve, reject) => {
      if ('geolocation' in navigator) {
        navigator.geolocation.getCurrentPosition(
          position => resolve(position),
          error => reject(error),
          {
            enableHighAccuracy: true,
            timeout: 10000,
            maximumAge: 0
          }
        );
      } else {
        reject(new Error('Geolocation is not supported by this browser.'));
      }
    });
  }
}
