/* eslint-disable prefer-arrow/prefer-arrow-functions */
import * as Sentry from '@sentry/browser';
import { environment } from '../environments/environment'; // './esrc/environments/environment';

export function initSentry() {
  Sentry.init({
    dsn: 'https://e52e97fc558344bc80a218fc22a9a6a9@excubo.unicef.io/47', // Replace with your actual DSN
    environment: environment.mode === 'dev' ? 'development' : 'production',
    integrations: [
      new Sentry.Integrations.GlobalHandlers(),
      new Sentry.Integrations.TryCatch(),
      new Sentry.Integrations.Breadcrumbs(),
      new Sentry.Integrations.LinkedErrors(),
      new Sentry.Integrations.UserAgent(),
      new Sentry.Integrations.FunctionToString(),
      new Sentry.Integrations.InboundFilters(),
    ],
    // Set tracesSampleRate to 1.0 to capture 100%
    // of transactions for performance monitoring.
    // We recommend adjusting this value in production
    tracesSampleRate: 1.0,
    release: `giga-meter-angular@${environment.app_version}`,
  });
}
