import { environment as env } from './_environment.prod';
export const environment = {
  production: true,
  restAPI: 'https://uni-connect-services.azurewebsites.net/api/v1/',
  //restAPI: 'http://localhost:3000/api/v1/',
  token: env.token,
  app_version: '2.0.0',
};
