import { Component, OnDestroy, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-footer-navbar',
  templateUrl: './footer-navbar.component.html',
  styleUrls: ['./footer-navbar.component.scss'],
})
export class FooterNavbarComponent implements OnInit, OnDestroy{
  activeSegment: string = 'home';
  private routerSubscription!: Subscription;

  constructor(private router: Router) { }

  ngOnInit() {
     // Subscribe to router events
     this.routerSubscription = this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        console.log('hrihih',this.router.url)
        // Check the current URL and set the segment accordingly
        if (this.router.url.startsWith('/starttest')) {
          if(this.router.url.includes('/detail-page/')) {
            this.activeSegment = "about"
          } else {
          this.activeSegment = 'home';
          }
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
      this.activeSegment = "home"
    } else if (event.detail.value === 'about') {
      this.router.navigate(['/starttest/detail-page/23']);
      this.activeSegment = "about"
    }
  }
  ngOnDestroy() {
   this.routerSubscription.unsubscribe();
  } 
}
