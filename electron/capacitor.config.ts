import { ElectronCapacitorConfig } from '@capacitor-community/electron';

const config: ElectronCapacitorConfig  = {
  appId: 'com.meter.giga',
  appName: 'unicef-pdca',
  webDir: 'www',
  bundledWebRuntime: false,
  electron: {
    trayIconAndMenuEnabled: true,
    electronIsDev: false
  }
};

export default config;
