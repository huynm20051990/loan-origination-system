import { Component, inject } from '@angular/core';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [MatDialogModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>Cancel Application</h2>
    <mat-dialog-content>
      <p>Are you sure you want to cancel application <strong>{{ data.appNumber }}</strong>?</p>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="onNoClick()">GO BACK</button>
      <button mat-flat-button color="warn" (click)="onConfirmClick()">CONFIRM</button>
    </mat-dialog-actions>
  `,
  styles: [`
    mat-dialog-content { font-size: 14px; color: #555; }
    mat-dialog-actions { padding-bottom: 16px; }
  `]
})
export class ConfirmDialogComponent {
  // Use inject to get references
  readonly dialogRef = inject(MatDialogRef<ConfirmDialogComponent>);
  readonly data = inject(MAT_DIALOG_DATA); // This receives data from the dashboard

  onNoClick(): void {
    this.dialogRef.close(false);
  }

  onConfirmClick(): void {
    this.dialogRef.close(true);
  }
}
