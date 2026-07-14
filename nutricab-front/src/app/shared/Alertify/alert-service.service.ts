import { Injectable } from '@angular/core';

declare let alertify: any;

@Injectable({ providedIn: 'root' })
export class AlertService {

  success(message: string): void {
    const instance = this.getAlertify();
    instance ? instance.success(message) : window.alert(message);
  }

  error(message: string): void {
    const instance = this.getAlertify();
    instance ? instance.error(message) : window.alert(message);
  }

  warning(message: string): void {
    const instance = this.getAlertify();
    instance ? instance.warning(message) : window.alert(message);
  }

  confirm(title: string, message: string, onConfirm: () => void, onCancel?: () => void): void {
    const instance = this.getAlertify();

    if (!instance) {
      window.confirm(`${title}\n\n${message}`) ? onConfirm() : onCancel?.();
      return;
    }

    instance.confirm(
      title,
      message,
      () => onConfirm(),
      () => onCancel?.()
    );
  }

  private getAlertify(): any | null {
    return typeof alertify !== 'undefined' ? alertify : null;
  }
}
