import { Injectable } from '@angular/core';
import { Network } from '@awesome-cordova-plugins/network/ngx';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';

type Ip4Data = {
  organization: string;
  country: string;
  asn: number;
  area_code: string;
  organization_name: string;
  country_code: string;
  country_code3: string;
  continent_code: string;
  latitude: string;
  region: string;
  city: string;
  longitude: string;
  accuracy: number;
  ip: string;
  timezone: string;
};

type IpInfoLiteData = {
  ip: string;
  asn: string;
  as_name: string;
  as_domain: string;
  country_code: string;
  country: string;
  continent_code: string;
  continent: string;
};

type IpInfoData = {
  ip: string;
  hostname: string;
  city: string;
  region: string;
  country: string;
  loc: string;
  org: string;
  postal: string;
  timezone: string;
  asn?: string | any;
};
@Injectable({
  providedIn: 'root',
})
export class NetworkService {
  accessServiceUrl = environment.restAPI + 'ip-metadata';
  ipInfoLiteUrl = 'https://api.ipinfo.io/lite/me?token=9906baf67eda8b'; //ONLY FOR LOCAL DEV TESTING
  // accessServiceUrl = 'https://ipinfo.io?token=060bdd9da6a22f'; //ONLY FOR LOCAL DEV TESTING
  headers: any;
  options: any;
  currentAccessInformation: any;
  connectionType = {
    icon: 'ion-help',
    label: 'Unknown',
  };
  constructor(private http: HttpClient, private network: Network) {
    const headersItem = new HttpHeaders({
      'Content-Type': 'application/json',
    });
    this.headers = headersItem;
  }

  // this function has a retrie logic of 3 times with exponential wait
  async getIpFromIpLite(): Promise<IpInfoLiteData> {
    console.log('getIpFromIpLite');
    const options = { headers: this.headers };
    let response = null;
    let retryCount = 0;
    const maxRetries = 3;

    while (retryCount < maxRetries) {
      try {
        console.log('accessServiceUrl', this.ipInfoLiteUrl);
        response = (await this.http.get(this.ipInfoLiteUrl, options).toPromise<any>()) as IpInfoLiteData;
        console.log('response', response);
        return response;
      } catch (error) {
        console.error('Error:', error);
        retryCount++;
        if (retryCount < maxRetries) {
          const waitTime = Math.pow(2, retryCount) * 1000; // Exponential backoff
          await new Promise((resolve) => setTimeout(resolve, waitTime));
        }
      }
    }
    throw new Error('Failed to retrieve IP information after multiple attempts.');
  }


  /**
   * Retrieves network information.
   * @returns {Promise<any>} A promise that resolves to the network information.
   */
  async getNetInfo() {
    const ipInfoLite = await this.getIpFromIpLite();
    console.log('getNetInfo');
    const options = { headers: this.headers };
    let response = null;
    try {
      console.log('accessServiceUrl', this.accessServiceUrl);
      response = this.standardData(
        await this.http.get(`${this.accessServiceUrl}/${ipInfoLite.ip}`, options).toPromise<any>()
      );
    } catch (error) {
      console.error('Error:', error);
      const ipGeoResponse = await fetch('https://ipv4.geojs.io/v1/ip/geo.json');
      const ipGeoData = await ipGeoResponse.json();
      return this.mapData(ipGeoData);
    }
    return response;
  }

  private mapData(source: Ip4Data): IpInfoData {
    return this.standardData({
      ip: source.ip,

      hostname: source.ip,
      city: source.city ?? '',
      region: source.region ?? '',
      country: source.country_code,
      loc: `${source.latitude},${source.longitude}`,
      org: source.organization ?? source.organization_name,
      postal: '',
      timezone: source.timezone,
    });
  }
  private standardData(source: IpInfoData): IpInfoData {
    return {
      ip: source?.ip ?? '',
      hostname: source?.hostname ?? '',
      city: source?.city ?? '',
      region: source?.region ?? '',
      country: source?.country ?? '',
      loc: source?.loc ?? '',
      org: source?.org ?? '',
      postal: source?.postal ?? '',
      timezone: source?.timezone ?? '',
      asn: source?.asn?.asn ?? source?.org.match(/AS[0-9]+/)?.[0] ?? '',
    };
  }
}
