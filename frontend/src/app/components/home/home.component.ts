import { Component, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/authentication.service';
import { ProductService } from '../../services/product.service';

interface Product {
  id: number;
  name: string;
  price: number;
  category: string;
  image: string;
}

@Component({
  selector: 'app-home',
  standalone: true,
  encapsulation: ViewEncapsulation.None,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {

  currentUser: any = null;

  products: Product[] = [
   
  ];

  constructor(private router: Router, private authService: AuthService, private productService: ProductService) {}

  ngOnInit() {
    this.getCurrentUser();
    this.productService.getProducts().subscribe(
      {
        next: (products: any) => {
          this.products = products
        },
        error: () => {
          console.error("error in loading products")
        }
      }
    )
  }

  getCurrentUser() {
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        this.currentUser = user.username ?? user.attributes.name;
      },
      error: (err) => {
        console.error('Error fetching current user:', err);
      }
    });
  }

  logout() {
    this.authService.logoutCookies().subscribe({
      next: () => {
            this.router.navigate(['/login']);
      },
      error: () => {
        console.error('error in logging user out')
      }
    });

  }
}