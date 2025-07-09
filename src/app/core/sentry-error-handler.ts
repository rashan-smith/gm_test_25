import { ErrorHandler, Injectable } from '@angular/core';
import { SentryService } from '../services/sentry.service';

@Injectable()
export class SentryErrorHandler implements ErrorHandler {
  constructor(private sentryService: SentryService) {}

  handleError(error: Error) {
    // Log the error to the console
    console.error('An error occurred:', error);

    // Capture the error in Sentry with additional context
    this.sentryService.captureException(error, {
      type: 'unhandled_error',
      timestamp: new Date().toISOString(),
      location: window?.location?.href,
      userAgent: navigator?.userAgent,
    });
  }
}
