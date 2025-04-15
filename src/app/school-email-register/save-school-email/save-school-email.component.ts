import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormControl, Validators } from '@angular/forms';

@Component({
  selector: 'app-save-school-email',
  templateUrl: './save-school-email.component.html',
  styleUrls: ['./save-school-email.component.scss'],
})
export class SaveSchoolEmailComponent implements OnInit {
  emailControl = new FormControl('', [Validators.required, Validators.email]);

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
}
