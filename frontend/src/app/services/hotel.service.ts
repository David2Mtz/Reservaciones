import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, firstValueFrom } from 'rxjs';

export interface User {
  id: number;
  username: string;
  nombre: string;
  email: string;
  activo: boolean;
  roles: string[];
}

export interface Room {
  id?: number;
  tipo: string;
  numero: number;
  precio: number;
  numeroCamas: number;
  disponible?: boolean;
}

export interface Booking {
  id?: number;
  cuartoId: number;
  cuartoNumero?: number;
  cuartoTipo?: string;
  usuarioId: number;
  usuarioUsername?: string;
  usuarioEmail?: string;
  fechaInicio: string;
  fechaFin: string;
  estado?: string;
  precioTotal?: number;
  detalles?: string;
}

@Injectable({
  providedIn: 'root'
})
export class HotelService {
  // CONFIGURACION: Reemplaza la URL de abajo con la URL real de tu servicio web en Render
  private apiBase = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1'
    ? 'http://localhost:8090'
    : 'https://reservaciones-uknp.onrender.com';

  private userSubject = new BehaviorSubject<User | null>(
    JSON.parse(localStorage.getItem('user') || 'null')
  );
  public user$ = this.userSubject.asObservable();

  private authHeader: string | null = localStorage.getItem('authHeader') || null;

  constructor(private http: HttpClient) {}

  public get currentUser(): User | null {
    return this.userSubject.value;
  }

  private getHttpOptions(isMultipart = false) {
    let headers = new HttpHeaders();
    if (this.authHeader) {
      headers = headers.set('Authorization', this.authHeader);
    }
    if (!isMultipart) {
      headers = headers.set('Content-Type', 'application/json');
    }
    return { headers };
  }

  // --- AUTH SERVICES ---
  async login(username: string, password: string): Promise<User> {
    const headerVal = 'Basic ' + btoa(username + ':' + password);
    const headers = new HttpHeaders()
      .set('Authorization', headerVal)
      .set('Content-Type', 'application/json');

    const user = await firstValueFrom(
      this.http.post<User>(`${this.apiBase}/api/v1/auth/login`, { username, password }, { headers })
    );

    this.authHeader = headerVal;
    localStorage.setItem('authHeader', headerVal);
    localStorage.setItem('user', JSON.stringify(user));
    this.userSubject.next(user);
    return user;
  }

  async register(registrationData: any): Promise<User> {
    return await firstValueFrom(
      this.http.post<User>(`${this.apiBase}/api/v1/auth/register`, registrationData)
    );
  }

  logout() {
    this.authHeader = null;
    localStorage.removeItem('authHeader');
    localStorage.removeItem('user');
    this.userSubject.next(null);
  }

  // --- ROOM SERVICES ---
  async getRooms(): Promise<Room[]> {
    return await firstValueFrom(
      this.http.get<Room[]>(`${this.apiBase}/api/v1/cuartos`, this.getHttpOptions())
    );
  }

  async createRoom(room: Room): Promise<Room> {
    return await firstValueFrom(
      this.http.post<Room>(`${this.apiBase}/api/v1/cuartos`, room, this.getHttpOptions())
    );
  }

  async updateRoom(id: number, room: Room): Promise<Room> {
    return await firstValueFrom(
      this.http.put<Room>(`${this.apiBase}/api/v1/cuartos/${id}`, room, this.getHttpOptions())
    );
  }

  async deleteRoom(id: number): Promise<void> {
    await firstValueFrom(
      this.http.delete<void>(`${this.apiBase}/api/v1/cuartos/${id}`, this.getHttpOptions())
    );
  }

  // --- BOOKING SERVICES ---
  async getAllBookings(): Promise<Booking[]> {
    return await firstValueFrom(
      this.http.get<Booking[]>(`${this.apiBase}/api/v1/reservaciones`, this.getHttpOptions())
    );
  }

  async getBookingById(id: number): Promise<Booking> {
    return await firstValueFrom(
      this.http.get<Booking>(`${this.apiBase}/api/v1/reservaciones/${id}`, this.getHttpOptions())
    );
  }

  async getBookingsByUserId(userId: number): Promise<Booking[]> {
    return await firstValueFrom(
      this.http.get<Booking[]>(`${this.apiBase}/api/v1/reservaciones/usuario/${userId}`, this.getHttpOptions())
    );
  }

  async createBooking(booking: Booking): Promise<Booking> {
    return await firstValueFrom(
      this.http.post<Booking>(`${this.apiBase}/api/v1/reservaciones`, booking, this.getHttpOptions())
    );
  }

  async updateBooking(id: number, booking: any): Promise<Booking> {
    return await firstValueFrom(
      this.http.put<Booking>(`${this.apiBase}/api/v1/reservaciones/${id}`, booking, this.getHttpOptions())
    );
  }

  async cancelBooking(id: number): Promise<Booking> {
    return await firstValueFrom(
      this.http.delete<Booking>(`${this.apiBase}/api/v1/reservaciones/${id}`, this.getHttpOptions())
    );
  }

  // --- FILE SERVICES ---
  async uploadFile(formData: FormData): Promise<any> {
    return await firstValueFrom(
      this.http.post<any>(`${this.apiBase}/apiArchivos/v1/archivos/subirArchivo`, formData, this.getHttpOptions(true))
    );
  }

  async downloadFile(id: number): Promise<Blob> {
    // Requires getting the blob object directly
    let headers = new HttpHeaders();
    if (this.authHeader) {
      headers = headers.set('Authorization', this.authHeader);
    }
    return await firstValueFrom(
      this.http.get(`${this.apiBase}/apiArchivos/v1/archivos/descargarArchivo/${id}`, {
        headers,
        responseType: 'blob'
      })
    );
  }
}
