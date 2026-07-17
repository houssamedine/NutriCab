import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Page } from '../../core/models/user.model';

@Component({
  selector: 'app-pagination',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (page && page.totalPages > 1) {
      <nav class="pagination-bar" aria-label="Pagination">
        <div class="pagination-summary">
          {{ firstItem }}-{{ lastItem }} sur {{ page.totalElements }}
        </div>

        <div class="btn-group" role="group">
          <button
            class="btn btn-outline-secondary btn-sm"
            type="button"
            [disabled]="page.first"
            (click)="goToPage(page.number - 1)">
            Precedent
          </button>

          @for (pageNumber of visiblePages; track pageNumber) {
            <button
              class="btn btn-sm"
              type="button"
              [ngClass]="pageNumber === page.number ? 'btn-primary' : 'btn-outline-secondary'"
              (click)="goToPage(pageNumber)">
              {{ pageNumber + 1 }}
            </button>
          }

          <button
            class="btn btn-outline-secondary btn-sm"
            type="button"
            [disabled]="page.last"
            (click)="goToPage(page.number + 1)">
            Suivant
          </button>
        </div>
      </nav>
    }
  `,
  styles: [`
    .pagination-bar {
      align-items: center;
      display: flex;
      gap: 1rem;
      justify-content: space-between;
      padding: 0.75rem 0;
    }

    .pagination-summary {
      color: #6c757d;
      font-size: 0.9rem;
    }

    @media (max-width: 576px) {
      .pagination-bar {
        align-items: stretch;
        flex-direction: column;
      }

      .btn-group {
        display: grid;
        grid-template-columns: repeat(3, minmax(0, 1fr));
      }
    }
  `]
})
export class PaginationComponent<T> {
  @Input({ required: true }) page: Page<T> | null = null;
  @Output() pageChange = new EventEmitter<number>();

  get firstItem(): number {
    if (!this.page || this.page.totalElements === 0) {
      return 0;
    }

    return this.page.number * this.page.size + 1;
  }

  get lastItem(): number {
    if (!this.page) {
      return 0;
    }

    return Math.min((this.page.number + 1) * this.page.size, this.page.totalElements);
  }

  get visiblePages(): number[] {
    if (!this.page) {
      return [];
    }

    const start = Math.max(0, this.page.number - 2);
    const end = Math.min(this.page.totalPages - 1, this.page.number + 2);

    return Array.from({ length: end - start + 1 }, (_, index) => start + index);
  }

  goToPage(pageNumber: number): void {
    if (!this.page || pageNumber < 0 || pageNumber >= this.page.totalPages || pageNumber === this.page.number) {
      return;
    }

    this.pageChange.emit(pageNumber);
  }
}
