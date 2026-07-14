import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  loginForm: FormGroup;
  loading = false;
  errorMessage = '';
  showPassword = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      rememberMe: [true]
    });
  }

  login(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const { email, password } = this.loginForm.value;

    this.authService.login({ email, password }).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/patients']);
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error?.error?.message || 'Email ou mot de passe incorrect';
      }
    });
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  get emailInvalid(): boolean {
    const email = this.loginForm.get('email');
    return !!email && email.invalid && (email.dirty || email.touched);
  }

  get passwordInvalid(): boolean {
    const password = this.loginForm.get('password');
    return !!password && password.invalid && (password.dirty || password.touched);
  }
}
