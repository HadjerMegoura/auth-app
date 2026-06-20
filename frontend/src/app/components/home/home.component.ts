import { Component, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

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
    { id: 1, name: 'Wireless Headphones', price: 79.99, category: 'Audio', image: 'https://placehold.co/300x300/EEEDFE/534AB7?text=Headphones' },
    { id: 2, name: 'Smart Watch', price: 149.99, category: 'Wearables', image: 'https://placehold.co/300x300/EEEDFE/534AB7?text=Watch' },
    { id: 3, name: 'Mechanical Keyboard', price: 89.99, category: 'Accessories', image: 'https://placehold.co/300x300/EEEDFE/534AB7?text=Keyboard' },
    { id: 4, name: 'USB-C Hub', price: 34.99, category: 'Accessories', image: 'https://placehold.co/300x300/EEEDFE/534AB7?text=Hub' },
    { id: 5, name: 'Portable Speaker', price: 59.99, category: 'Audio', image: 'https://placehold.co/300x300/EEEDFE/534AB7?text=Speaker' },
    { id: 6, name: 'Laptop Stand', price: 24.99, category: 'Accessories', image: 'https://placehold.co/300x300/EEEDFE/534AB7?text=Stand' },
  ];

  constructor(private router: Router) {}

  logout() {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }
}