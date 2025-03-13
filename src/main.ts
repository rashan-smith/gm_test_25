import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { app, BrowserWindow } from 'electron';
import * as remoteMain from '@electron/remote/main';

import { AppModule } from './app/app.module';
import { environment } from './environments/environment';

if (environment.production) {
  enableProdMode();
}

remoteMain.initialize();

function createWindow() {
  const win = new BrowserWindow({
    // ... existing window options ...
    webPreferences: {
      nodeIntegration: true,
      contextIsolation: false
    }
  });

  remoteMain.enable(win.webContents);
  // ... rest of your createWindow code ...
}

platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.log(err));
