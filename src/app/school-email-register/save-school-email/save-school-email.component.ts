import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AbstractControl, FormControl, ValidationErrors, Validators } from '@angular/forms';

@Component({
  selector: 'app-save-school-email',
  templateUrl: './save-school-email.component.html',
  styleUrls: ['./save-school-email.component.scss'],
})
export class SaveSchoolEmailComponent implements OnInit {
  emailControl = new FormControl('', [Validators.required, Validators.email, this.emailDomainValidator]);

  constructor(private readonly router: Router) {}

  ngOnInit() {}

  addEmail() {
    if (this.emailControl.invalid) {
      this.emailControl.markAsTouched(); // Show error if untouched
      return;
    }

    console.log('Email saved:', this.emailControl.value);
    this.router.navigate(['/schoolsuccess']);
  }

  skip() {
    console.log('User skipped adding email');
    this.router.navigate(['/schoolsuccess']);
  }
  emailDomainValidator(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    
    // Check if the value matches the pattern
    return emailPattern.test(value) ? null : { invalidEmail: true };
  }
  
}
