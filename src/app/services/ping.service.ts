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
}

@Injectable({
  providedIn: 'root',
})
export class PingService {
  private targetHosts = ['https://google.com', 'https://bing.com', 'https://yahoo.com'];
  private timeoutDuration = 10000; // Default timeout: 10 seconds
  private packetsPerHost = 3;
  private activeHours = { start: 8, end: 20 }; // Active hours: 8 AM to 8 PM

  constructor(private http: HttpClient) {}

  /**
   * Checks if the current time is within the allowed active hours.
   */
  private isWithinActiveHours(): boolean {
    const now = new Date();
    const currentHour = now.getHours();
    return currentHour >= this.activeHours.start && currentHour < this.activeHours.end;
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
          successResults.reduce((sum, res) => sum + (res.responseTimeMs || 0), 0) /
          successResults.length;

        return {
          timestamp: new Date(),
          isConnected: true,
          responseTimeMs: averageResponseTime,
          targetHost: host,
          packetsSent: this.packetsPerHost,
          packetsReceived: successResults.length,
          errorMessage: null,
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
}
