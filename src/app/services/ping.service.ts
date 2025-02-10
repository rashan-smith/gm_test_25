import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Device } from '@capacitor/device';
import { v4 as uuidv4 } from 'uuid';
import { IndexedDBService } from './indexed-db.service';
import { StorageService } from './storage.service';

export interface PingResult {
  timestamp: Date;
  isConnected: boolean;
  errorMessage: string | null;
  deviceId: string;
  app_local_uuid: string;
}

@Injectable({
  providedIn: 'root',
})
export class PingService {
  private activeHours = { start: 8, end: 20 }; // Active hours: 8 AM to 8 PM
  private isElectron: boolean;
  private dns: any;
  private net: any;

  private connectivityChecks = {
    dns: ['8.8.8.8', '1.1.1.1'],
    hosts: ['google.com', 'cloudflare.com', 'microsoft.com'],
    ports: [53, 443],
  };

  constructor(private http: HttpClient, private indexedDBService: IndexedDBService    
  ) {}

  private isWithinActiveHours(): boolean {
    const now = new Date();
    const currentHour = now.getHours();
    return currentHour >= this.activeHours.start && currentHour < this.activeHours.end;
  }

  private async checkNavigatorOnline(): Promise<boolean> {
    return navigator.onLine;
  }

  private async checkDNSResolution(host: string): Promise<boolean> {
    return new Promise((resolve) => {
      this.dns.lookup(host, (err: any) => resolve(!err));
    });
  }

  private async checkTCPConnection(host: string, port: number): Promise<boolean> {
    return new Promise((resolve) => {
      const socket = new this.net.Socket();
      socket.setTimeout(5000);

      socket.on('connect', () => {
        socket.destroy();
        resolve(true);
      });

      socket.on('timeout', () => {
        socket.destroy();
        resolve(false);
      });

      socket.on('error', () => {
        socket.destroy();
        resolve(false);
      });

      socket.connect(port, host);
    });
  }

  private async checkFetchAPI(): Promise<boolean> {
    try {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 5000);

      await fetch('https://1.1.1.1/cdn-cgi/trace', {
        mode: 'no-cors',
        signal: controller.signal,
      });

      clearTimeout(timeoutId);
      return true;
    } catch {
      return false;
    }
  }

  async checkConnectivity(): Promise<PingResult> {
    let isConnected = false;
    let errorMessage = null;
    let deviceId;
    let uniqueId = uuidv4();

    try {
      isConnected = await this.checkNavigatorOnline();
      this.getDeviceId().then((b) => {
        deviceId = b.uuid;
      });

      if (this.isElectron) {
        const dnsChecks = await Promise.all(
          this.connectivityChecks.dns.map((ip) => this.checkDNSResolution(ip))
        );
        if (!dnsChecks.some((result) => result)) {
          throw new Error('DNS resolution failed');
        }

        const connectionChecks = await Promise.all(
          this.connectivityChecks.hosts.reduce(
            (acc, host) => [
              ...acc,
              ...this.connectivityChecks.ports.map((port) => this.checkTCPConnection(host, port)),
            ],
            []
          )
        );

        isConnected = connectionChecks.some((result) => result);
      } else {
        isConnected = await this.checkFetchAPI();
      }
    } catch (error) {
      isConnected = false;
      errorMessage = error.message;
    }

    return {
      timestamp: new Date(),
      isConnected,
      errorMessage,
      deviceId: deviceId,
      app_local_uuid: uniqueId,
    };
  }

  async getDeviceId() {
    const deviceId = await Device.getId();
    return deviceId;
  }

  async performCheck(): Promise<PingResult | null> {
    if (!this.isWithinActiveHours()) {
      console.log('Skipping check: Outside active hours.');
      return null;
    }

    return await this.checkConnectivity();
  }

  startPeriodicChecks(frequency: number, callback: (result: PingResult | null) => void) {
    setInterval(async () => {
      const result = await this.performCheck();
      if (result) {
        console.log('Ping result:', result);
        this.indexedDBService.savePingResult(result);
      } else {
        console.log('Ping skipped: Outside active hours.');
      }
    }, frequency);
  }
}
