import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class LocalStorageService {
  private storageKey = 'connectivity_ping_checks';
  private syncThreshold = 2 * 60 * 60 * 1000; // 2 hours in milliseconds
  private retentionPeriod = 7 * 24 * 60 * 60 * 1000; // 7 days in milliseconds

  constructor() {}

  /**
   * Saves a ping result to local storage.
   * Adds a `createdAt` timestamp for tracking.
   */
  savePingResult(result: any): void {
    const records = this.getPingResults();
    records.push({ ...result, createdAt: Date.now(), isSynced: false });
    localStorage.setItem(this.storageKey, JSON.stringify(records));
  }

  /**
   * Retrieves all ping results from local storage.
   */
  getPingResults(): any[] {
    const data = localStorage.getItem(this.storageKey);
    return data ? JSON.parse(data) : [];
  }

  /**
   * Updates records to mark them as synced.
   */
  markAsSynced(syncedRecords: any[]): void {
    const records = this.getPingResults();
    const updatedRecords = records.map((record) => {
      if (syncedRecords.some((synced) => synced.timestamp === record.timestamp)) {
        return { ...record, isSynced: true };
      }
      return record;
    });
    localStorage.setItem(this.storageKey, JSON.stringify(updatedRecords));
  }

  /**
   * Removes records older than the retention period or already synced.
   */
  cleanupOldRecords(): void {
    const records = this.getPingResults();
    const now = Date.now();

    // Filter records to retain only recent or unsynced ones
    const filteredRecords = records.filter((record) => {
      const isWithinRetention = now - record.createdAt < this.retentionPeriod;
      return isWithinRetention && !record.isSynced;
    });

    // Log if any records are being cleaned up
    if (records.length !== filteredRecords.length) {
      console.log('Cleaning up old or synced records:', records.length - filteredRecords.length);
    }

    localStorage.setItem(this.storageKey, JSON.stringify(filteredRecords));
  }

  /**
   * Retrieves unsynced records.
   */
  getUnsyncedRecords(): any[] {
    const records = this.getPingResults();
    return records.filter((record) => !record.isSynced);
  }
}
