import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { LocalStorageService } from './local-storage.service';

@Injectable({
  providedIn: 'root',
})
export class SyncService {
  private syncUrl = 'https://your-backend-api.com/ping-sync';
  private syncInterval = 2 * 60 * 60 * 1000; // 2 hours

  constructor(private http: HttpClient, private localStorageService: LocalStorageService) {}

  /**
   * Syncs unsynced records to the server.
   */
  async syncPingResults(): Promise<void> {
    const unsyncedRecords = this.localStorageService.getUnsyncedRecords();

    if (unsyncedRecords.length === 0) {
      console.log('No unsynced records to sync.');
      return;
    }

    try {
      await this.http.post(this.syncUrl, { records: unsyncedRecords }).toPromise();

      // Mark synced records
      this.localStorageService.markAsSynced(unsyncedRecords);
      console.log('Successfully synced records.');
    } catch (error) {
      console.error('Failed to sync records:', error);
    }

    // Clean up old records regardless of sync status
    this.localStorageService.cleanupOldRecords();
  }

  /**
   * Starts the periodic sync process.
   */
  startPeriodicSync(): void {
    setInterval(() => {
      this.syncPingResults();
    }, this.syncInterval);
  }
}
