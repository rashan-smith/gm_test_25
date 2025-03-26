import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { AppModule } from './app/app.module';
import { environment } from './environments/environment';
import { initSentry } from './app/sentry.config';
import * as Sentry from '@sentry/browser';

// Initialize Sentry
initSentry();

if (environment.production) {
  enableProdMode();
}

platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => {
    console.error(err);
    // Capture the error in Sentry
    Sentry.captureException(err);
  });
