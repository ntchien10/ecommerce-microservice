import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private url = 'http://ecommerce.local/auth';

  constructor(private http: HttpClient) {}

  login(data: { username: string; password: string }) {
    return this.http.post<any>(this.url + '/login', data);
  }

  logout() {
    const refreshToken = localStorage.getItem('refreshToken');

    return this.http.post<any>(this.url + '/logout', {
      refreshToken: refreshToken
    });
  }

  saveTokens(res: any) {
    localStorage.setItem('accessToken', res.data.accessToken);
    localStorage.setItem('refreshToken', res.data.refreshToken);
  }

  clearTokens() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  }

  isLoggedIn() {
    return !!localStorage.getItem('accessToken');
  }

  getAccessToken() {
    return localStorage.getItem('accessToken');
  }
}