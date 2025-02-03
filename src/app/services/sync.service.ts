import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { LocalStorageService } from './local-storage.service';

@Injectable({
  providedIn: 'root',
})
export class SyncService {
  private syncUrl = 'https://jsonplaceholder.typicode.com/posts';
  private syncInterval = 2 * 60 * 60 * 1000; // 2 hours

  constructor(private http: HttpClient, private localStorageService: LocalStorageService) {}

  async syncPingResults(): Promise<void> {
    let unsyncedRecords = this.localStorageService.getUnsyncedRecords();
  
    if (unsyncedRecords.length === 0) {
      console.log('No unsynced records to sync.');
      return;
    }
  
    const batchSize = 5; // Number of records per batch
    let index = 0;
  
    while (index < unsyncedRecords.length) {
      const batch = unsyncedRecords.slice(index, index + batchSize);
      
      try {
        await this.postWithRetry(batch);
        this.localStorageService.markAsSynced(batch);
        console.log(`Successfully synced batch ${index / batchSize + 1}`);
      } catch (error) {
        console.error(`Failed to sync batch ${index / batchSize + 1} after retry:`, error);
        throw error; // Stop processing further if one batch fails after retry
      }
  
      index += batchSize;
    }
  
    // Cleanup old records
    this.localStorageService.cleanupOldRecords();
  }
  
  /**
   * Attempts to sync a batch and retries once if it fails.
   */
  private async postWithRetry(batch: any[]): Promise<void> {
    try {
      await this.http.post(this.syncUrl, { records: batch }).toPromise();
    } catch (error) {
      console.warn('Initial sync attempt failed. Retrying...');
      try {
        await this.http.post(this.syncUrl, { records: batch }).toPromise();
      } catch (retryError) {
        console.error('Retry failed:', retryError);
        throw retryError; // Throw error after one retry
      }
    }
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
