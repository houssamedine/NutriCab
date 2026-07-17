import { Component, HostBinding, Input } from '@angular/core';

@Component({
  selector: 'tr[appTableLoading]',
  standalone: true,
  templateUrl: './table-loading.component.html',
  styleUrl: './table-loading.component.css'
})
export class TableLoadingComponent {
  @Input() loading = false;
  @Input() colspan = 1;

  @HostBinding('hidden')
  get hidden(): boolean {
    return !this.loading;
  }

  @HostBinding('attr.aria-busy')
  get ariaBusy(): string | null {
    return this.loading ? 'true' : null;
  }
}
