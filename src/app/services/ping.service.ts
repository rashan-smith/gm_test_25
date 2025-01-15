import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { of } from 'rxjs';
import { catchError, map, timeout } from 'rxjs/operators';

export interface PingResult {
  timestamp: Date;
  isConnected: boolean;
  responseTimeMs: number | null;
  targetHost: string;
  packetsSent: number;
  packetsReceived: number;
  errorMessage: string | null;
  diagnostics: {
    platform: string;
    navigatorOnline: boolean;
    userAgent: string;
  };
}

@Injectable({
  providedIn: 'root',
})
export class PingService {
  private targetHosts = [
    'https://google.com',
    'https://bing.com',
    'https://yahoo.com',
  ];
  private timeoutDuration = 10000; // Default timeout: 10 seconds
  private packetsPerHost = 3;
  private activeHours = { start: 8, end: 20 }; // Active hours: 8 AM to 8 PM
  private isElectron: boolean;
  private dns: any;
  private net: any;

  // Configuration for different types of checks
  private connectivityChecks = {
    dns: ['8.8.8.8', '1.1.1.1'], // Google & Cloudflare DNS servers
    hosts: ['google.com', 'cloudflare.com', 'microsoft.com'],
    ports: [53, 443], // DNS and HTTPS ports
  };

  constructor(private http: HttpClient) {}

  /**
   * Checks if the current time is within the allowed active hours.
   */
  private isWithinActiveHours(): boolean {
    const now = new Date();
    const currentHour = now.getHours();
    return (
      currentHour >= this.activeHours.start &&
      currentHour < this.activeHours.end
    );
  }

  /**
   * Performs a single ping to the given host.
   */
  private pingHost(host: string): Promise<PingResult> {
    const startTime = Date.now();
    return this.http
      .get(host, { responseType: 'text' })
      .pipe(
        timeout(this.timeoutDuration),
        map(() => {
          const endTime = Date.now();
          return {
            timestamp: new Date(),
            isConnected: true,
            responseTimeMs: endTime - startTime,
            targetHost: host,
            packetsSent: 1,
            packetsReceived: 1,
            errorMessage: null,
            diagnostics: {
              platform: 'browser',
              navigatorOnline: navigator.onLine,
              userAgent: navigator.userAgent,
            },
          } as PingResult;
        }),
        catchError((error) => {
          return of({
            timestamp: new Date(),
            isConnected: false,
            responseTimeMs: null,
            targetHost: host,
            packetsSent: 1,
            packetsReceived: 0,
            errorMessage: error.message || 'Unknown error',
            diagnostics: {
              platform: 'browser',
              navigatorOnline: navigator.onLine,
              userAgent: navigator.userAgent,
            },
          } as PingResult);
        })
      )
      .toPromise();
  }

  /**
   * Tries to ping each host in the array until a successful response or all hosts are checked.
   */
  private async pingHosts(): Promise<PingResult> {
    for (const host of this.targetHosts) {
      const results = await Promise.all(
        Array(this.packetsPerHost)
          .fill(null)
          .map(() => this.pingHost(host))
      );

      const successResults = results.filter((result) => result.isConnected);
      if (successResults.length > 0) {
        const averageResponseTime =
          successResults.reduce(
            (sum, res) => sum + (res.responseTimeMs || 0),
            0
          ) / successResults.length;

        return {
          timestamp: new Date(),
          isConnected: true,
          responseTimeMs: averageResponseTime,
          targetHost: host,
          packetsSent: this.packetsPerHost,
          packetsReceived: successResults.length,
          errorMessage: null,
          diagnostics: {
            platform: 'browser',
            navigatorOnline: navigator.onLine,
            userAgent: navigator.userAgent,
          },
        };
      }
    }

    // If all hosts fail
    return {
      timestamp: new Date(),
      isConnected: false,
      responseTimeMs: null,
      targetHost: this.targetHosts.join(', '), // List of failed hosts
      packetsSent: this.targetHosts.length * this.packetsPerHost,
      packetsReceived: 0,
      errorMessage: 'All target hosts failed',
      diagnostics: {
        platform: 'browser',
        navigatorOnline: navigator.onLine,
        userAgent: navigator.userAgent,
      },
    };
  }

  /**
   * Performs a connectivity check if within the active hours.
   */
  async performCheck(): Promise<PingResult | null> {
    if (!this.isWithinActiveHours()) {
      console.log('Skipping ping: Outside active hours.');
      return null;
    }

    return await this.pingHosts();
  }

  /**
   * Starts periodic connectivity checks, restricted to active hours.
   */
  startPeriodicChecks(
    frequency: number,
    callback: (result: PingResult | null) => void
  ) {
    setInterval(async () => {
      const result = await this.performCheck();
      if (result) {
        callback(result);
      }
    }, frequency);
  }

  /**
   * Checks if the navigator.onLine is true.
   */
  private async checkNavigatorOnline(): Promise<boolean> {
    return navigator.onLine;
  }

  /**
   * Checks if the DNS resolution is successful.
   */
  private async checkDNSResolution(host: string): Promise<boolean> {
    return new Promise((resolve) => {
      this.dns.lookup(host, (err: any) => {
        resolve(!err); // Returns true if DNS lookup succeeds
      });
    });
  }

  /**
   * Checks if the TCP connection is successful.
   */
  private async checkTCPConnection(
    host: string,
    port: number
  ): Promise<boolean> {
    return new Promise((resolve) => {
      const socket = new this.net.Socket();

      socket.setTimeout(5000); // 5 second timeout

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

  /**
   * Checks if the fetch API is successful.
   */
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

  /**
   * Checks the connectivity of the system.
   */
  async checkConnectivity(): Promise<PingResult> {
    const startTime = Date.now();
    let isConnected = false;
    let errorMessage = null;

    try {
      // First check navigator.onLine
      isConnected = await this.checkNavigatorOnline();

      if (this.isElectron) {
        // Electron-specific checks
        const dnsChecks = await Promise.all(
          this.connectivityChecks.dns.map((ip) => this.checkDNSResolution(ip))
        );

        if (!dnsChecks.some((result) => result)) {
          throw new Error('DNS resolution failed');
        }

        // TCP connection checks
        const connectionChecks = await Promise.all(
          this.connectivityChecks.hosts.reduce(
            (acc, host) => [
              ...acc,
              ...this.connectivityChecks.ports.map((port) =>
                this.checkTCPConnection(host, port)
              ),
            ],
            []
          )
        );

        isConnected = connectionChecks.some((result) => result);
      } else {
        // Browser-only checks
        isConnected = await this.checkFetchAPI();
      }
    } catch (error) {
      isConnected = false;
      errorMessage = error.message;
    }

    const endTime = Date.now();

    return {
      timestamp: new Date(),
      isConnected,
      responseTimeMs: isConnected ? endTime - startTime : null,
      targetHost: this.connectivityChecks.hosts.join(', '),
      packetsSent: 1,
      packetsReceived: isConnected ? 1 : 0,
      errorMessage,
      diagnostics: {
        platform: this.isElectron ? 'electron' : 'browser',
        navigatorOnline: navigator.onLine,
        userAgent: navigator.userAgent,
      },
    };
  }
}
