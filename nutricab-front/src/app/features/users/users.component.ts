import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, inject, OnInit, PLATFORM_ID } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Page, User, UserRole } from '../../core/models/user.model';
import { UserService } from '../../core/services/user.service';
import { AlertService } from '../../shared/Alertify/alert-service.service';
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { TableLoadingComponent } from '../../shared/table-loading/table-loading.component';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, PaginationComponent, TableLoadingComponent],
  templateUrl: './users.component.html',
  styleUrl: './users.component.css'
})
export class UsersComponent implements OnInit {

  loading = true;
  searchTerm = '';
  errorMessage = '';
  users: User[] = [];
  selectedActiveStatus = '';
  selectedRole: '' | UserRole = '';
  filterModalOpen = false;
  userRoles: UserRole[] = ['ADMIN', 'NUTRITIONIST', 'SECRETARY', 'PATIENT'];
  page: Page<User> | null = null;
  pageSize = 10;

  private usersService = inject(UserService);
  private alertService = inject(AlertService);
  private router = inject(Router);
  private platformId = inject(PLATFORM_ID);

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.loadUsers();
    }
  }

  loadUsers(pageNumber: number = 0): void {
    this.loading = true;
    this.errorMessage = '';
    this.usersService.getAllUsers(pageNumber, this.pageSize).subscribe({
      next: (page) => {
        this.page = page;
        this.users = page.content;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erreur lors du chargement des utilisateurs';
        this.loading = false;
      }
    });
  }

  goToNewUsers(): void {
    this.router.navigate(['/users/new']);
  }

  editUsers(id: number): void {
    this.router.navigate(['/users/edit', id]);
  }

  deleteUsers(id: number): void {
    this.alertService.confirm('Confirmation', 'Etes-vous sur de vouloir supprimer cet utilisateur ?', () => {
      this.usersService.deleteUser(id).subscribe({
        next: () => {
          this.alertService.success('Utilisateur supprime avec succes');
          this.loadCurrentPage();
        },
        error: () => {
          this.alertService.error('Erreur lors de la suppression de l utilisateur');
        }
      });
    });
  }

  onSearchInput(event: Event): void {
    const keyword = (event.target as HTMLInputElement).value.trim();

    if (!keyword) {
      this.searchTerm = '';
      this.loadUsers();
    }
  }

  onSearch(): void {
    this.loading = true;
    this.errorMessage = '';
    const email = this.searchTerm.trim();

    if (!email) {
      this.loadUsers();
      return;
    }

    this.usersService.getUserByEmail(email).subscribe({
      next: (data) => {
        this.page = null;
        this.users = [data];
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erreur lors de la recherche';
        this.loading = false;
      }
    });
  }

  openFilterModal(): void {
    this.filterModalOpen = true;
  }

  closeFilterModal(): void {
    this.filterModalOpen = false;
  }

  applyFilters(): void {
    this.errorMessage = '';
    this.searchTerm = '';
    this.loadFilteredUsers(0);
    this.closeFilterModal();
  }

  resetFilters(): void {
    this.selectedActiveStatus = '';
    this.selectedRole = '';
    this.loadUsers();
    this.closeFilterModal();
  }

  onPageChange(pageNumber: number): void {
    if (this.selectedActiveStatus || this.selectedRole) {
      this.loadFilteredUsers(pageNumber);
      return;
    }

    this.loadUsers(pageNumber);
  }

  private loadFilteredUsers(pageNumber: number): void {
    const role = this.selectedRole || undefined;
    const active = this.selectedActiveStatus === ''
      ? undefined
      : this.selectedActiveStatus === 'active';

    if (active !== undefined) {
      this.loading = true;
      this.usersService.getUsersByFilter(active, role, pageNumber, this.pageSize).subscribe({
        next: (page) => {
          this.page = page;
          this.users = page.content;
          this.loading = false;
        },
        error: () => {
          this.errorMessage = 'Erreur lors du filtrage des utilisateurs';
          this.loading = false;
        }
      });
      return;
    }

    if (role) {
      this.loading = true;
      this.usersService.getUsersByRole(role, pageNumber, this.pageSize).subscribe({
        next: (page) => {
          this.page = page;
          this.users = page.content;
          this.loading = false;
        },
        error: () => {
          this.errorMessage = 'Erreur lors du filtrage par role';
          this.loading = false;
        }
      });
      return;
    }

    this.loadUsers(pageNumber);
  }

  private loadCurrentPage(): void {
    const currentPage = this.page?.number ?? 0;
    const pageAfterDelete = currentPage > 0 && this.users.length <= 1
      ? currentPage - 1
      : currentPage;

    this.onPageChange(pageAfterDelete);
  }
}
