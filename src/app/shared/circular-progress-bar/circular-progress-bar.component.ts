import { Component, Input, Output, EventEmitter } from '@angular/core';

  @Component({
    selector: 'app-circular-progress-bar',
    templateUrl: './circular-progress-bar.component.html',
    styleUrls: ['./circular-progress-bar.component.scss'],
  })
  export class CircularProgressBarComponent {
    @Input() firstLabel!: string;
    @Input() secondLabel: string | null = null;
    @Input() icon: string | null = null;
    @Input() progressValue!: number;
    @Input() error: boolean = false;
    // @Input() consentChecked: boolean = false;
    
    @Output() startTest = new EventEmitter<void>();
    @Output() showError = new EventEmitter<boolean>();
  
    handleClick() {
      console.log('ehandleClick')
      // if (!this.consentChecked) {
      //   this.showError.emit(true);
      //   return;
      // }
      if (this.progressValue === 0 || this.progressValue === 100) {
        this.startTest.emit();
      }
    }
  
    getStrokeDashOffset(): string {
      if(!this.error) {
        return `calc(251.2px - (251.2px * ${this.progressValue}) / 100)`;
      } else {
        return `calc(251.2px - (251.2px * 100}) / 100)`;
      }
    }
  
    getFillColor(): string {
      if (
        this.firstLabel === 'IDLE' ||
        this.firstLabel === 'COMPLETED' ||
        this.firstLabel === 'ERROR'
      ) {
        return '#0e62fe1a';
      }
      return 'transparent';
    }
  
    getTextClass(): string {
      if (this.secondLabel) return 'fill-color-tertiary';
      if (this.error) return 'fill-color-text-error';
      // if (!this.consentChecked) return 'fill-gray';
      return 'fill-white';
    }
  }
  