import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class IndexedDBService {
  private dbName = 'connectivity_ping_db';
  private storeName = 'pingResults';

  constructor() {}

  private openDatabase(): Promise<IDBDatabase> {
    return new Promise((resolve, reject) => {
      const request = indexedDB.open(this.dbName, 1);
      request.onupgradeneeded = () => {
        const db = request.result;
        if (!db.objectStoreNames.contains(this.storeName)) {
          db.createObjectStore(this.storeName, { keyPath: 'timestamp' });
        }
      };
      request.onsuccess = () => resolve(request.result);
      request.onerror = (e) => reject(e);
    });
  }

  async savePingResult(result: any): Promise<void> {
    const db = await this.openDatabase();
    const transaction = db.transaction(this.storeName, 'readwrite');
    const store = transaction.objectStore(this.storeName);
    store.put({ ...result, createdAt: Date.now(), isSynced: false });
    return new Promise((resolve, reject) => {
      transaction.oncomplete = () => resolve();
      transaction.onerror = (e) => reject(e);
    });
  }

  async getPingResults(): Promise<any[]> {
    const db = await this.openDatabase();
    const transaction = db.transaction(this.storeName, 'readonly');
    const store = transaction.objectStore(this.storeName);
    return new Promise((resolve, reject) => {
      const request = store.getAll();
      request.onsuccess = () => resolve(request.result);
      request.onerror = (e) => reject(e);
    });
  }

  async markAsSynced(syncedRecords: any[]): Promise<void> {
    const db = await this.openDatabase();
    const transaction = db.transaction(this.storeName, 'readwrite');
    const store = transaction.objectStore(this.storeName);
    syncedRecords.forEach((synced) => {
      store.put({ ...synced, isSynced: true });
    });
    return new Promise((resolve, reject) => {
      transaction.oncomplete = () => resolve();
      transaction.onerror = (e) => reject(e);
    });
  }

  async cleanupOldRecords(): Promise<void> {
    const db = await this.openDatabase();
    const transaction = db.transaction(this.storeName, 'readwrite');
    const store = transaction.objectStore(this.storeName);
    const now = Date.now();
    const retentionPeriod = 30 * 24 * 60 * 60 * 1000; // 30 days in milliseconds

    return new Promise((resolve, reject) => {
        const request = store.getAll(); // Get all records from IndexedDB
        
        request.onsuccess = () => {
            const records = request.result;
            const deletePromises: Promise<void>[] = [];

            records.forEach((record) => {
                if (record.isSynced || now - record.createdAt >= retentionPeriod) {
                    const deleteRequest = store.delete(record.timestamp); // Delete based on primary key
                    
                    // Wrap each delete operation in a promise
                    const deletePromise = new Promise<void>((res, rej) => {
                        deleteRequest.onsuccess = () => res();
                        deleteRequest.onerror = () => rej(deleteRequest.error);
                    });

                    deletePromises.push(deletePromise);
                }
            });

            // Wait for all delete operations to complete before resolving
            Promise.all(deletePromises)
                .then(() => resolve())
                .catch(reject);
        };

        request.onerror = () => reject(request.error);
    });
}


  async getUnsyncedRecords(): Promise<any[]> {
    const records = await this.getPingResults();
    return records.filter((record) => !record.isSynced);
  }
}
