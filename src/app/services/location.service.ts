import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';


@Injectable({
  providedIn: 'root'
})
export class LocationService {

  constructor(private http: HttpClient) {}

  getLocation() {
    const url = `https://www.googleapis.com/geolocation/v1/geolocate?key=${environment.googleAPI}`;
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

  private formatWifiList(wifiList: any[]): any[] {
    return wifiList.map(ap => ({
      macAddress: ap.mac || ap.bssid, // depends on what your scan returns
      signalStrength: ap.signal_level || ap.signalStrength || -65,
      signalToNoiseRatio: ap.signalToNoiseRatio || 40 // optional, placeholder
    }));
  }

  getAccurateLocation(wifiList: any[]) {
    const wifiAccessPoints = wifiList.map(ap => ({
      macAddress: ap.mac || ap.bssid,
      signalStrength: Math.round(ap.signal_level), // must be integer
      signalToNoiseRatio: 40 // Optional, fixed value or computed if available
    }));
    const body = {
      considerIp: false, // optional: set false to ignore IP-based geolocation
      wifiAccessPoints
    };
    const url = `https://www.googleapis.com/geolocation/v1/geolocate?key=${environment.googleAPI}`;

    return this.http.post<{ location: { lat: number, lng: number }, accuracy: number }>(url, body);
  }
}
