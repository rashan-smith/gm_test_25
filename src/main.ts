import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { AppModule } from './app/app.module';
import { environment } from './environments/environment';

if (environment.production) {
  enableProdMode();
}

// Only include Electron code when running in Electron
if (window.require) {
  const { app, BrowserWindow } = window.require('electron');
  const remoteMain = window.require('@electron/remote/main');

  remoteMain.initialize();

  function createWindow() {
    const win = new BrowserWindow({
      webPreferences: {
        nodeIntegration: true,
        contextIsolation: false
      }
    });

    remoteMain.enable(win.webContents);
  }
}

platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.log(err));
