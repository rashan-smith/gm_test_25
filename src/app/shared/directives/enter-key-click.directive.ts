import { Directive, HostListener, ElementRef } from '@angular/core';

@Directive({
  selector: '[appEnterKeyClick]'
})
export class EnterKeyClickDirective {
  constructor(private el: ElementRef<HTMLElement>) {}

  @HostListener('document:keydown.enter', ['$event'])
  onEnterPress(event: KeyboardEvent) {
    const button = this.el.nativeElement;

    if (!button.hasAttribute('disabled')) {
      event.preventDefault();
      button.focus();
      button.click();
    }
  }
}
