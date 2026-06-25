import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent {

  username = '';
  password = '';
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  login() {
    this.errorMessage = '';

    this.authService.login({
      username: this.username,
      password: this.password
    }).subscribe({
      next: (res: any) => {
        console.log('LOGIN RESPONSE:', res);

        if (res.success && res.data) {
          this.authService.saveTokens(res);
          this.router.navigate(['/home']);
        } else {
          this.errorMessage = res.message || 'Đăng nhập thất bại';
        }
      },

      error: (err) => {
        console.log('LOGIN ERROR:', err);
        this.errorMessage = err.error?.message || 'Đăng nhập thất bại';
      }
    });
  }
}