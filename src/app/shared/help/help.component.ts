import { Component, OnInit } from '@angular/core';
import { MenuController } from '@ionic/angular';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-help',
  templateUrl: './help.component.html',
  styleUrls: ['./help.component.scss'],
})
export class HelpComponent implements OnInit {

  constructor(    private menu: MenuController,
        public translate: TranslateService
    
  ) { }

  ngOnInit() {}
  closeMenu() {
    this.menu.enable(true, 'help');
    this.menu.close();
  }
}
