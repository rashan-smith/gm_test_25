import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastController } from '@ionic/angular';

@Component({
  selector: 'app-save-school-email',
  templateUrl: './save-school-email.component.html',
  styleUrls: ['./save-school-email.component.scss'],
})
export class SaveSchoolEmailComponent implements OnInit {

  constructor(private toastController: ToastController, private readonly router: Router) {}

  ngOnInit() {}

  email: string = '';


  async addEmail() {
    if (!this.email || !this.email.includes('@')) {
      this.showToast('Please enter a valid email');
      return;
    }

    // Simulate saving email
    console.log('Email saved:', this.email);
    this.showToast('Email added successfully');
    this.router.navigate(['/schoolsuccess']);

  }

  skip() {
    console.log('User skipped adding email');
    this.showToast('Skipped adding email');
    this.router.navigate(['/schoolsuccess']);

  }

  async showToast(message: string) {
    const toast = await this.toastController.create({
      message,
      duration: 2000,
      position: 'bottom'
    });
    await toast.present();
  }
}
