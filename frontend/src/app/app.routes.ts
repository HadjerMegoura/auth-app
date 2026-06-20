import { Routes } from '@angular/router';
import { RegisterationComponent } from './components/registeration/registeration.component';
import { LoginComponent } from './components/login/login.component';


export const routes: Routes = [
  { path: '', redirectTo: 'register', pathMatch: 'full' },
  { path: 'register', component: RegisterationComponent },
  { path: 'login', component: LoginComponent },
  { path: '**', redirectTo: 'register' },
];
