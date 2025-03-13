import type { BrowserWindow, webContents } from '@electron/remote';

declare global {
  interface Window {
    require: NodeJS.Require;
    process: NodeJS.Process;
    __dirname: string;
  }

  namespace NodeJS {
    interface Require {
      (id: string): any;
      resolve: (id: string) => string;
      cache: { [path: string]: any };
      extensions: { [extension: string]: (module: any, filename: string) => any };
      main: { filename: string };
    }
  }
}

declare module 'electron' {
  interface CrossProcessExports {
    remote: any; // Using any here to avoid conflicts
  }
}

// Ensure this file is treated as a module
export {}; 