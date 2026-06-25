import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private url = 'http://ecommerce.local/products';

  constructor(private http: HttpClient) {}

  getProducts() {
    return this.http.get<any>(this.url);
  }

  createProduct(data: any) {
    return this.http.post<any>(this.url, data);
  }

  updateProduct(id: number, data: any) {
    return this.http.put<any>(`${this.url}/${id}`, data);
  }

  deleteProduct(id: number) {
    return this.http.delete<any>(`${this.url}/${id}`);
  }
}