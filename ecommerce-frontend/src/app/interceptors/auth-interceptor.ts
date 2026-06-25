import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  console.log('===== INTERCEPTOR =====');

  const token = localStorage.getItem('accessToken');

  console.log('TOKEN =', token);

  if (token) {

    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });

    console.log('HEADER =', req.headers.get('Authorization'));
  }

  return next(req);
};