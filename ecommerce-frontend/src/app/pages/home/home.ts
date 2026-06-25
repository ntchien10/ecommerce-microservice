import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ProductService } from '../../services/product';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class HomeComponent implements OnInit {

  products: any[] = [];
  errorMessage = '';
  successMessage = '';

  form: any = {
    id: null,
    name: '',
    description: '',
    brand: '',
    price: null,
    quantity: null,
    imageUrl: ''
  };

  isEdit = false;

  constructor(
    private productService: ProductService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.loadProducts();
  }

  loadProducts() {
    this.productService.getProducts().subscribe({
      next: (res: any) => {
        console.log('PRODUCT RESPONSE:', res);
  
        if (Array.isArray(res)) {
          this.products = res;
        } else if (res.data?.content) {
          this.products = res.data.content;
        } else if (Array.isArray(res.data)) {
          this.products = res.data;
        } else {
          this.products = [];
        }
  
        console.log('PRODUCTS TO RENDER:', this.products);
  
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.log('PRODUCT ERROR:', err);
        this.errorMessage = err.error?.message || 'Không tải được danh sách sản phẩm';
        this.cdr.detectChanges();
      }
    });
  }

  saveProduct() {
    this.errorMessage = '';
    this.successMessage = '';

    const data = {
      name: this.form.name,
      description: this.form.description,
      brand: this.form.brand,
      price: this.form.price,
      quantity: this.form.quantity,
      imageUrl: this.form.imageUrl
    };

    if (this.isEdit) {
      this.productService.updateProduct(this.form.id, data).subscribe({
        next: () => {
          this.successMessage = 'Cập nhật sản phẩm thành công';
          this.resetForm();
          this.loadProducts();
        },
        error: (err) => {
          console.log(err);
          this.errorMessage = err.error?.message || 'Cập nhật thất bại';
        }
      });
    } else {
      this.productService.createProduct(data).subscribe({
        next: () => {
          this.successMessage = 'Thêm sản phẩm thành công';
          this.resetForm();
          this.loadProducts();
        },
        error: (err) => {
          console.log(err);
          this.errorMessage = err.error?.message || 'Thêm sản phẩm thất bại';
        }
      });
    }
  }

  editProduct(product: any) {
    this.isEdit = true;
    this.form = { ...product };
  }

  deleteProduct(id: number) {
    if (!confirm('Bạn có chắc muốn xóa sản phẩm này không?')) {
      return;
    }

    this.productService.deleteProduct(id).subscribe({
      next: () => {
        this.successMessage = 'Xóa sản phẩm thành công';
        this.loadProducts();
      },
      error: (err) => {
        console.log(err);
        this.errorMessage = err.error?.message || 'Xóa sản phẩm thất bại';
      }
    });
  }

  resetForm() {
    this.isEdit = false;
    this.form = {
      id: null,
      name: '',
      description: '',
      brand: '',
      price: null,
      quantity: null,
      imageUrl: ''
    };
  }

  logout() {
    this.authService.logout().subscribe({
      next: () => {
        this.authService.clearTokens();
        this.router.navigate(['/login']);
      },
      error: () => {
        this.authService.clearTokens();
        this.router.navigate(['/login']);
      }
    });
  }
}