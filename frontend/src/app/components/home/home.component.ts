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

  products: Product[] = [
   
  ];

  constructor(private router: Router, private authService: AuthService, private productService: ProductService) {}

  ngOnInit() {
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

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}