/* eslint-disable prefer-arrow/prefer-arrow-functions */
/* eslint-disable object-shorthand */
/* eslint-disable @typescript-eslint/member-ordering */
import type { CapacitorElectronConfig } from '@capacitor-community/electron';
import {
  CapElectronEventEmitter,
  CapacitorSplashScreen,
  setupCapacitorElectronPlugins,
} from '@capacitor-community/electron';
import chokidar from 'chokidar';
import type { MenuItemConstructorOptions } from 'electron';
import {
  app,
  BrowserWindow,
  Menu,
  MenuItem,
  nativeImage,
  Tray,
  session,
  shell,
  globalShortcut,
} from 'electron';
import electronIsDev from 'electron-is-dev';
import electronServe from 'electron-serve';
import windowStateKeeper from 'electron-window-state';
import { join } from 'path';
import * as Sentry from '@sentry/node';
import { Console } from 'console';
import { Severity } from '@sentry/node';
var AutoLaunch = require('auto-launch');
var isQuiting = false;

// Enhanced Sentry configuration
Sentry.init({
  dsn: 'https://e52e97fc558344bc80a218fc22a9a6a9@excubo.unicef.io/47',
  environment: 'production',
  beforeSend: (event)=> {
    // Add app version to help with debugging
    event.extra = {
      ...event.extra,
      appVersion: app?.getVersion(),
      platform: process?.platform,
      electronVersion: process?.versions?.electron,
    };
    return event;
  },
  debug: electronIsDev,
  maxBreadcrumbs: 50,
  release: `giga-meter-electron@${app?.getVersion()}`,
});

// Set up global error handlers for Sentry
process.on('uncaughtException', (error) => {
  console.error('Uncaught Exception:', error);
  Sentry.captureException(error);
});

process.on('unhandledRejection', (reason, promise) => {
  console.error('Unhandled Rejection at:', promise, 'reason:', reason);
  Sentry.captureException(reason);
});

app.on('render-process-gone', (event, webContents, details) => {
  console.error('Renderer process crashed:', details);
  Sentry.captureMessage(`Renderer process crashed: ${details.reason}`);
});

app.on('child-process-gone', (event, details) => {
  console.error('Child process crashed:', details);
  Sentry.captureMessage(
    `Child process crashed: ${details.type} - ${details.reason}`
  );
});

const gotTheLock = app.requestSingleInstanceLock();
// Define components for a watcher to detect when the webapp is changed so we can reload in Dev mode.
const reloadWatcher = {
  debouncer: null,
  ready: false,
  watcher: null,
};
export function setupReloadWatcher(
  electronCapacitorApp: ElectronCapacitorApp
): void {
  reloadWatcher.watcher = chokidar
    .watch(join(app.getAppPath(), 'app'), {
      ignored: /[/\\]\./,
      persistent: true,
    })
    .on('ready', () => {
      reloadWatcher.ready = true;
    })
    .on('all', (_event, _path) => {
      if (reloadWatcher.ready) {
        clearTimeout(reloadWatcher.debouncer);
        reloadWatcher.debouncer = setTimeout(async () => {
          electronCapacitorApp.getMainWindow().webContents.reload();
          reloadWatcher.ready = false;
          clearTimeout(reloadWatcher.debouncer);
          reloadWatcher.debouncer = null;
          reloadWatcher.watcher = null;
          setupReloadWatcher(electronCapacitorApp);
        }, 1500);
      }
    });
}

// Define our class to manage our app.
export class ElectronCapacitorApp {
  private MainWindow: BrowserWindow | null = null;
  private SplashScreen: CapacitorSplashScreen | null = null;
  private TrayIcon: Tray | null = null;
  private CapacitorFileConfig: CapacitorElectronConfig;
  private TrayMenuTemplate: (MenuItem | MenuItemConstructorOptions)[] = [
    new MenuItem({
      label: 'Open',
      click: function () {
        this.MainWindow.show();
      },
    }),
    new MenuItem({
      label: 'Quit App',
      click: function () {
        isQuiting = true;
        app.quit();
        this.MainWindow.close();
      },
    }),
  ];
  private AppMenuBarMenuTemplate: (MenuItem | MenuItemConstructorOptions)[] = [
    { role: process.platform === 'darwin' ? 'appMenu' : 'fileMenu' },
    { role: 'viewMenu' },
  ];
  private mainWindowState;
  private loadWebApp;
  private customScheme: string;

  constructor(
    capacitorFileConfig: CapacitorElectronConfig,
    trayMenuTemplate?: (MenuItemConstructorOptions | MenuItem)[],
    appMenuBarMenuTemplate?: (MenuItemConstructorOptions | MenuItem)[]
  ) {
    this.CapacitorFileConfig = capacitorFileConfig;

    this.customScheme =
      this.CapacitorFileConfig.electron?.customUrlScheme ??
      'capacitor-electron';

    if (trayMenuTemplate) {
      this.TrayMenuTemplate = trayMenuTemplate;
    }

    if (appMenuBarMenuTemplate) {
      this.AppMenuBarMenuTemplate = appMenuBarMenuTemplate;
    }

    // Setup our web app loader, this lets us load apps like react, vue, and angular without changing their build chains.
    this.loadWebApp = electronServe({
      directory: join(app.getAppPath(), 'app'),
      scheme: this.customScheme,
    });
  }

  // Helper function to load in the app.
  private async loadMainWindow(thisRef: any) {
    await thisRef.loadWebApp(thisRef.MainWindow);
  }

  // Expose the mainWindow ref for use outside of the class.
  getMainWindow(): BrowserWindow {
    return this.MainWindow;
  }

  getCustomURLScheme(): string {
    return this.customScheme;
  }

  async init(): Promise<void> {
    const icon = nativeImage.createFromPath(
      join(
        app?.getAppPath(),
        'assets',
        process?.platform === 'win32' ? 'appIcon.ico' : 'appIcon.png'
      )
    );
    this.mainWindowState = windowStateKeeper({
      defaultWidth: 376,
      defaultHeight: 550,
    });
    // Setup preload script path and construct our main window.
    const preloadPath = join(app?.getAppPath(), 'build', 'src', 'preload.js');
    this.MainWindow = new BrowserWindow({
      icon,
      show: false,
      x: this.mainWindowState?.x,
      y: this.mainWindowState?.y,
      width: this.mainWindowState?.width,
      height: this.mainWindowState?.height,
      titleBarStyle: 'hidden',
      maximizable: false,
      minimizable: false,
      resizable: true,
      transparent: true,
      webPreferences: {
        nodeIntegration: true,
        contextIsolation: true,
        // Use preload to inject the electron varriant overrides for capacitor plugins.
        // preload: join(app.getAppPath(), "node_modules", "@capacitor-community", "electron", "dist", "runtime", "electron-rt.js"),
        preload: preloadPath,
      },
    });

    // Add error tracking for renderer process
    this.MainWindow?.webContents?.on('render-process-gone', (event, details) => {
      const crashData = {
        reason: details?.reason,
        exitCode: details?.exitCode,
        processType: 'renderer',
      };
      Sentry.captureException(new Error('Renderer Process Gone'), {
        extra: crashData,
      });
    });

    this.MainWindow?.on('unresponsive', () => {
      Sentry.captureMessage('Window became unresponsive', {
        level: Severity.Error,
        extra: {
          windowId: this.MainWindow?.id,
        },
      });
    });

    this.MainWindow?.webContents?.on(
      'console-message',
      (event, level, message, line, sourceId) => {
        if (level === 2) {
          // error level
          Sentry.captureMessage(`Console Error: ${message}`, {
            extra: {
              line,
              sourceId,
            },
          });
        }
      }
    );

    this.MainWindow?.setSize(376, 550);
    this.mainWindowState?.manage(this.MainWindow);

    if (this.CapacitorFileConfig?.backgroundColor) {
      this.MainWindow?.setBackgroundColor(
        this.CapacitorFileConfig?.electron?.backgroundColor
      );
    }

    // If we close the main window with the splashscreen enabled we need to destory the ref.
    this.MainWindow?.on('closed', () => {
      if (
        this.SplashScreen?.getSplashWindow() &&
        !this.SplashScreen?.getSplashWindow()?.isDestroyed()
      ) {
        this.SplashScreen?.getSplashWindow()?.close();
      }
    });
    // When the tray icon is enabled, setup the options.
    if (this.CapacitorFileConfig?.electron?.trayIconAndMenuEnabled) {
      this.TrayIcon = new Tray(icon);
      this.TrayIcon?.on('double-click', () => {
        if (this.MainWindow) {
          if (this.MainWindow?.isVisible()) {
            this.MainWindow?.hide();
          } else {
            this.MainWindow?.show();
            this.MainWindow?.focus();
          }
        }
      });
      this.TrayIcon?.on('click', () => {
        if (this.MainWindow) {
          if (this.MainWindow?.isVisible()) {
            this.MainWindow?.hide();
          } else {
            this.MainWindow?.show();
            this.MainWindow?.focus();
          }
        }
      });
      this.TrayIcon?.setToolTip(app?.getName());
      this.TrayIcon?.setContextMenu(
        Menu.buildFromTemplate(this.TrayMenuTemplate)
      );
    }

    // Setup the main manu bar at the top of our window.
    if (this.CapacitorFileConfig?.electron?.appMenuBarMenuTemplateEnabled) {
      Menu.setApplicationMenu(
        Menu.buildFromTemplate(this.AppMenuBarMenuTemplate)
      );
    } else {
      Menu.setApplicationMenu(new Menu());
    }
    // If the splashscreen is enabled, show it first while the main window loads then dwitch it out for the main window, or just load the main window from the start.
    if (this.CapacitorFileConfig?.electron?.splashScreenEnabled) {
      this.SplashScreen = new CapacitorSplashScreen({
        imageFilePath: join(
          app?.getAppPath(),
          'assets',
          this.CapacitorFileConfig?.electron?.splashScreenImageName ??
            'splash.png'
        ),
        windowWidth: 400,
        windowHeight: 400,
      });
      this.SplashScreen?.init(this.loadMainWindow, this);
    } else {
      this.loadMainWindow(this);
    }

    // Security
    this.MainWindow?.webContents?.setWindowOpenHandler((details) => {
      if (!details?.url?.includes(this.customScheme)) {
        return { action: 'deny' };
      } else {
        return { action: 'allow' };
      }
    });
    this.MainWindow?.webContents?.on('will-navigate', (event, _newURL) => {
      if (!this.MainWindow?.webContents?.getURL()?.includes(this.customScheme)) {
        event.preventDefault();
      }
    });
    // this.MainWindow.on('close',(event)=>{
    //   if(!isQuiting){
    //     event.preventDefault();
    //     this.MainWindow.hide();
    //     return false;
    //   } else {
    //     app.quit();
    //   }
    // });
    // Link electron plugins into the system.
    setupCapacitorElectronPlugins();

    // When the web app is loaded we hide the splashscreen if needed and show the mainwindow.
    this.MainWindow?.webContents?.on('dom-ready', () => {
      if (this.CapacitorFileConfig?.electron?.splashScreenEnabled) {
        this.SplashScreen?.getSplashWindow()?.hide();
      }
      if (!this.CapacitorFileConfig?.electron?.hideMainWindowOnLaunch) {
        this.MainWindow?.show();
      }
      globalShortcut.register('Super+Shift+N', () => {
        if (this.MainWindow?.webContents?.isDevToolsOpened()) {
          this.MainWindow?.webContents?.closeDevTools();
        } else {
          this.MainWindow?.webContents?.openDevTools();
        }
      });
      setTimeout(() => {
        if (this.CapacitorFileConfig?.electron?.electronIsDev) {
          this.MainWindow?.webContents?.openDevTools();
          this.MainWindow?.setSize(800, 600);
        }
        CapElectronEventEmitter.emit(
          'CAPELECTRON_DeeplinkListenerInitialized',
          ''
        );
      }, 400);
    });

    if (!gotTheLock) {
      app.quit();
    } else {
      app.on('second-instance', (event, commandLine, workingDirectory) => {
        // Someone tried to run a second instance, we should focus our window.
        if (this.MainWindow) {
          if (this.MainWindow?.isMinimized()) {
            this.MainWindow?.restore();
          } else {
            this.MainWindow?.show();
          }
          this.MainWindow?.focus();
        }
      });
    }

    // Auto lunching code added by Kajal
    var measureAppAutoLuncher = new AutoLaunch({
      name: 'Unicef PDCA',
    });

    measureAppAutoLuncher?.enable();
    measureAppAutoLuncher
      ?.isEnabled()
      .then(function (isEnabled) {
        if (isEnabled) {
          return;
        }
        measureAppAutoLuncher?.enable();
      })
      .catch(function (err) {
        // handle error
        Sentry.captureException(err);
        console.log(err);
      });
    // End of Auto lunching code
  }
}

// Set a CSP up for our application based on the custom scheme
export function setupContentSecurityPolicy(customScheme: string): void {
  session.defaultSession.webRequest.onHeadersReceived((details, callback) => {
    callback({
      responseHeaders: {
        ...details.responseHeaders,
        'Content-Security-Policy': [
          electronIsDev
            ? `default-src ${customScheme}://* 'unsafe-inline' devtools://* 'unsafe-eval' data:`
            : `default-src ${customScheme}://* 'unsafe-inline' data:`,
        ],
      },
    });
  });
}
