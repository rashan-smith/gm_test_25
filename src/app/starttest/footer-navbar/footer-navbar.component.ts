import { Component, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';

@Component({
  selector: 'app-footer-navbar',
  templateUrl: './footer-navbar.component.html',
  styleUrls: ['./footer-navbar.component.scss'],
})
export class FooterNavbarComponent implements OnInit {
  activeSegment: string = 'home';

  constructor(private router: Router) { }

  ngOnInit() {
     // Subscribe to router events
     this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        console.log(this.router.url)
        // Check the current URL and set the segment accordingly
        if (this.router.url === '/starttest') {
          this.activeSegment = 'home';
        } else if (this.router.url.startsWith('/starttest/detail-page/')) {
          this.activeSegment = 'about';
        } else {
          // Default fallback if needed
          this.activeSegment = 'home';
        }
      }
    });
  }


  onSegmentChange(event: any) {
    // If user taps segment, navigate accordingly
    if (event.detail.value === 'home') {
      this.router.navigate(['/starttest']);
    } else if (event.detail.value === 'about') {
      this.router.navigate(['/starttest/detail-page/23']);
    }
  }
}
