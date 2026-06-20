import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthResponse, AuthService } from '../../services/authentication.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-registeration',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './registeration.component.html',
  styleUrl: './registeration.component.css'
})
export class RegisterationComponent {

    form: FormGroup;
  showPassword = false;

  constructor(private fb: FormBuilder,  private router: Router, private authenticationService: AuthService) {
    this.form = this.fb.group({
      firstName: ['', Validators.required],
      lastName:  ['', Validators.required],
      email:     ['', [Validators.required, Validators.email]],
      password:  ['', [Validators.required, Validators.minLength(8)]],
    });
  }

  onSubmit() {
    console.log('submit')
    if (this.form.valid) {
      this.authenticationService.register(this.form.value).subscribe(
        {
          next: (response: AuthResponse) => {
            if (response.token) {
               this.router.navigate(['/login']);
            }
          },
          error: () => {}
        }
      )
    } else {
      this.form.markAllAsTouched();
    }
  }

  isInvalid(field: string) {
    const c = this.form.get(field);
    return c?.invalid && c?.touched;
  }
}
