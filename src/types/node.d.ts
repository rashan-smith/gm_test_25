declare namespace NodeJS {
  interface Require {
    (id: string): any;
    resolve: (id: string) => string;
    cache: { [path: string]: any };
    extensions: { [extension: string]: (module: any, filename: string) => any };
    main: { filename: string };
  }
} 