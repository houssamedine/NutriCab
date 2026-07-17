import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { of, switchMap } from 'rxjs';
import { ChangePasswordRequest, CreateUserRequest, UpdateUserRequest, User, UserRole } from '../../../core/models/user.model';
import { UserService } from '../../../core/services/user.service';
import { AlertService } from '../../../shared/Alertify/alert-service.service';

@Component({
  selector: 'app-users-form',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, FormsModule],
  templateUrl: './users-form.component.html',
  styleUrl: './users-form.component.css'
})
export class UsersFormComponent {

  errorMessage = '';
  loading = false;
  passwordLoading = false;
  submitted = false;
  passwordSubmitted = false;
  userForm!: FormGroup;
  passwordForm!: FormGroup;
  userId?: number;
  isEditMode = false;
  showPassword = false;
  showOldPassword = false;
  showNewPassword = false;
  userRoles: UserRole[] = ['ADMIN', 'NUTRITIONIST', 'SECRETARY', 'PATIENT'];

  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private router = inject(Router);
  private routeActivated = inject(ActivatedRoute);
  private alertService = inject(AlertService);

  ngOnInit(): void {
    const id = this.routeActivated.snapshot.paramMap.get('id');
    this.userId = id ? Number(id) : undefined;
    this.isEditMode = !!this.userId;

    this.userForm = this.fb.group({
      fullName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', this.isEditMode ? [] : [Validators.required, Validators.minLength(6)]],
      role: ['', Validators.required],
      active: [true, Validators.required],
    });

    this.passwordForm = this.fb.group({
      oldPassword: ['', [Validators.required, Validators.minLength(6)]],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
    });

    if (this.userId) {
      this.loadUserById(this.userId);
    }
  }

  loadUserById(id: number): void {
    this.errorMessage = '';
    this.loading = true;
    this.userService.getUserById(id).subscribe({
      next: (data) => {
        this.userForm.patchValue({
          fullName: data.fullName,
          email: data.email,
          role: data.role,
          active: data.active,
        });
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erreur lors du chargement de user';
        this.loading = false;
      }
    });
  }

  saveUser(): void {
    this.submitted = true;
    this.errorMessage = '';

    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    const formValue = this.userForm.value as CreateUserRequest & { active: boolean };
    const selectedActive = formValue.active;

    const savedRequest = this.isEditMode && this.userId
      ? this.userService.updateUser(this.userId, this.buildUpdateRequest(formValue))
      : this.userService.createUser(this.buildCreateRequest(formValue));

    savedRequest.pipe(
      switchMap((user: User) => {
        if (user.active === selectedActive) {
          return of(user);
        }

        return this.userService.toggleUserStatus(user.id);
      })
    ).subscribe({
      next: () => {
        this.alertService.success(this.isEditMode ? 'User updated successfully' : 'User created successfully');
        this.router.navigate(['/users']);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.errorMessage = this.isEditMode
          ? 'Erreur lors de la mise a jour de user'
          : "Erreur lors de la creation d'un user";
      }
    });
  }

  fieldInvalid(fieldName: string): boolean {
    const field = this.userForm.get(fieldName);
    return !!(field && field.invalid && (field.touched || this.submitted));
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  passwordFieldInvalid(fieldName: string): boolean {
    const field = this.passwordForm.get(fieldName);
    return !!(field && field.invalid && (field.touched || this.passwordSubmitted));
  }

  toggleOldPasswordVisibility(): void {
    this.showOldPassword = !this.showOldPassword;
  }

  toggleNewPasswordVisibility(): void {
    this.showNewPassword = !this.showNewPassword;
  }

  changePassword(): void {
    if (!this.userId) {
      return;
    }

    this.passwordSubmitted = true;

    if (this.passwordForm.invalid) {
      this.passwordForm.markAllAsTouched();
      return;
    }

    this.passwordLoading = true;
    const request = this.passwordForm.value as ChangePasswordRequest;

    this.userService.changePassword(this.userId, request).subscribe({
      next: () => {
        this.passwordLoading = false;
        this.passwordSubmitted = false;
        this.passwordForm.reset();
        this.alertService.success('Mot de passe modifie avec succes');
      },
      error: () => {
        this.passwordLoading = false;
        this.errorMessage = 'Erreur lors du changement de mot de passe';
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/users']);
  }

  private buildCreateRequest(formValue: CreateUserRequest): CreateUserRequest {
    return {
      fullName: formValue.fullName,
      email: formValue.email,
      password: formValue.password,
      role: formValue.role,
    };
  }

  private buildUpdateRequest(formValue: UpdateUserRequest): UpdateUserRequest {
    return {
      fullName: formValue.fullName,
      email: formValue.email,
      role: formValue.role,
    };
  }
}
