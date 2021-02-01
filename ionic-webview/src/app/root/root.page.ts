import { Component, OnInit } from '@angular/core';
import {InAppBrowser, InAppBrowserOptions} from '@ionic-native/in-app-browser/ngx';

@Component({
  selector: 'app-root',
  templateUrl: './root.page.html',
  styleUrls: ['./root.page.scss'],
})
export class RootPage implements OnInit {


  constructor(private iab: InAppBrowser) {
    const options: InAppBrowserOptions = {
      zoom: 'no',
      location: 'no',
      toolbar: 'no'
    };
    this.iab.create('https://suaurl', '_self', options);
  }


  ngOnInit() {
  }

}
