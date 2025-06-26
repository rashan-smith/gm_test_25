import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouteReuseStrategy } from '@angular/router';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { IonicModule, IonicRouteStrategy } from '@ionic/angular';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { SharedModule } from './shared/shared.module';

/* Import token interceptor */
import { TokenInterceptor } from './auth/token.interceptor';
import { environment } from 'src/environments/environment';

export function tokenGetter() {
  return environment.token;
}

@NgModule({ declarations: [AppComponent],
    bootstrap: [AppComponent], imports: [BrowserModule,
        IonicModule.forRoot(),
        SharedModule,
        AppRoutingModule], providers: [
        {
            provide: HTTP_INTERCEPTORS,
            useClass: TokenInterceptor,
            multi: true
        },
        { provide: RouteReuseStrategy, useClass: IonicRouteStrategy },
        provideHttpClient(withInterceptorsFromDi())
    ] })
export class AppModule {}
