import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HotelService, User, Room, Booking } from './services/hotel.service';

interface Toast {
  id: number;
  message: string;
  type: 'success' | 'error' | 'warning';
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'ReservaEasy';

  // Session State
  currentUser: User | null = null;

  // Navigation State
  currentView = 'rooms'; // 'rooms', 'my-bookings', 'admin-panel', 'files'

  // Data Collections
  rooms: Room[] = [];
  bookings: Booking[] = [];
  adminBookings: Booking[] = [];

  // Modals Visibility
  showAuthModal = false;
  authTab: 'login' | 'register' = 'login';
  showBookingModal = false;

  // Toast Alerts
  toasts: Toast[] = [];
  private toastIdCounter = 0;

  // Auth Form Models
  loginUsername = '';
  loginPassword = '';

  regUsername = '';
  regNombre = '';
  regEmail = '';
  regPassword = '';
  regRole = 'USER';

  // Room Form Models (Admin)
  formRoomId: number | null = null;
  roomTipo = 'Sencilla';
  roomNumero: number | null = null;
  roomPrecio: number | null = null;
  roomCamas: number | null = null;

  // Booking Form Models
  bookingRoomId: number | null = null;
  bookingSummaryTipo = '';
  bookingSummaryPrecio = '';
  bookingSummaryPrecioNum = 0;
  
  bookingId: number | null = null; // Filled only when editing
  bookingFechaInicio = '';
  bookingFechaFin = '';
  bookingDetalles = '';
  calcNoches = 0;
  calcTotal = 0;

  // File Upload Models
  selectedFile: File | null = null;
  selectedFileName = '';
  downloadFileId: number | null = null;

  constructor(private hotelService: HotelService) {}

  ngOnInit() {
    this.hotelService.user$.subscribe(user => {
      this.currentUser = user;
      this.updateNavigationForUser();
    });
    this.loadRooms();
  }

  // NAVIGATION & VIEW HANDLING
  switchView(viewName: string) {
    if (viewName === 'admin-panel' && (!this.currentUser || !this.currentUser.roles.includes('ROLE_ADMIN'))) {
      this.showToast('Acceso denegado. Se requieren permisos de administrador.', 'error');
      return;
    }
    if ((viewName === 'my-bookings' || viewName === 'files') && !this.currentUser) {
      this.showToast('Inicia sesión para acceder a esta sección.', 'warning');
      this.openAuth('login');
      return;
    }

    this.currentView = viewName;
    
    if (viewName === 'rooms') {
      this.loadRooms();
    } else if (viewName === 'my-bookings') {
      this.loadBookings();
    } else if (viewName === 'admin-panel') {
      this.loadAdminData();
    }
  }

  updateNavigationForUser() {
    if (!this.currentUser && this.currentView !== 'rooms') {
      this.currentView = 'rooms';
      this.loadRooms();
    }
  }

  // TOAST SYSTEM
  showToast(message: string, type: 'success' | 'error' | 'warning' = 'success') {
    const id = ++this.toastIdCounter;
    this.toasts.push({ id, message, type });
    
    setTimeout(() => {
      this.toasts = this.toasts.filter(t => t.id !== id);
    }, 4300);
  }

  // AUTHENTICATION OPERATIONS
  openAuth(tab: 'login' | 'register') {
    this.authTab = tab;
    this.showAuthModal = true;
  }

  closeAuth() {
    this.showAuthModal = false;
    this.resetAuthForm();
  }

  resetAuthForm() {
    this.loginUsername = '';
    this.loginPassword = '';
    this.regUsername = '';
    this.regNombre = '';
    this.regEmail = '';
    this.regPassword = '';
    this.regRole = 'USER';
  }

  async onLogin() {
    if (!this.loginUsername || !this.loginPassword) {
      this.showToast('Por favor completa todos los campos.', 'warning');
      return;
    }

    try {
      const user = await this.hotelService.login(this.loginUsername, this.loginPassword);
      this.showToast(`¡Bienvenido de nuevo, ${user.nombre}!`);
      this.closeAuth();
      this.switchView('rooms');
    } catch (err: any) {
      // Handled globally or by service, alert is handled by service throwing.
    }
  }

  async onRegister() {
    if (!this.regUsername || !this.regNombre || !this.regEmail || !this.regPassword) {
      this.showToast('Por favor completa todos los campos del registro.', 'warning');
      return;
    }

    try {
      await this.hotelService.register({
        username: this.regUsername,
        password: this.regPassword,
        nombre: this.regNombre,
        email: this.regEmail,
        roles: [this.regRole]
      });
      this.showToast('Registro exitoso. Ya puedes iniciar sesión.');
      this.authTab = 'login';
    } catch (err) {}
  }

  onLogout() {
    this.hotelService.logout();
    this.showToast('Sesión cerrada correctamente');
    this.switchView('rooms');
  }

  // ROOM OPERATIONS (PUBLIC & ADMIN)
  async loadRooms() {
    try {
      this.rooms = await this.hotelService.getRooms();
    } catch (err) {
      this.showToast('Fallo al obtener habitaciones.', 'error');
    }
  }

  async onSaveRoom() {
    if (!this.roomNumero || !this.roomPrecio || !this.roomCamas) {
      this.showToast('Por favor completa todos los campos del formulario.', 'warning');
      return;
    }

    const roomData: Room = {
      tipo: this.roomTipo,
      numero: this.roomNumero,
      precio: this.roomPrecio,
      numeroCamas: this.roomCamas
    };

    try {
      if (this.formRoomId) {
        await this.hotelService.updateRoom(this.formRoomId, roomData);
        this.showToast('Habitación actualizada con éxito');
      } else {
        await this.hotelService.createRoom(roomData);
        this.showToast('Habitación creada con éxito');
      }
      this.onCancelRoomEdit();
      this.loadAdminData();
    } catch (err) {}
  }

  onEditRoom(room: Room) {
    if (!room.id) return;
    this.formRoomId = room.id;
    this.roomTipo = room.tipo;
    this.roomNumero = room.numero;
    this.roomPrecio = room.precio;
    this.roomCamas = room.numeroCamas;
    this.showToast('Editando habitación #' + room.numero, 'warning');
  }

  async onDeleteRoom(id: number) {
    if (confirm('¿Estás seguro de que deseas eliminar esta habitación?')) {
      try {
        await this.hotelService.deleteRoom(id);
        this.showToast('Habitación eliminada con éxito');
        this.loadAdminData();
      } catch (err) {}
    }
  }

  onCancelRoomEdit() {
    this.formRoomId = null;
    this.roomTipo = 'Sencilla';
    this.roomNumero = null;
    this.roomPrecio = null;
    this.roomCamas = null;
  }

  // BOOKING OPERATIONS
  async loadBookings() {
    if (!this.currentUser) return;
    try {
      this.bookings = await this.hotelService.getBookingsByUserId(this.currentUser.id);
    } catch (err) {}
  }

  async loadAdminData() {
    try {
      this.adminBookings = await this.hotelService.getAllBookings();
      this.rooms = await this.hotelService.getRooms();
    } catch (err) {}
  }

  async openBookingModal(roomId: number, bookingId: number | null = null) {
    if (!this.currentUser) {
      this.showToast('Inicia sesión para reservar.', 'warning');
      this.openAuth('login');
      return;
    }

    // Fetch the room details
    const room = this.rooms.find(r => r.id === roomId);
    if (!room) return;

    this.bookingRoomId = roomId;
    this.bookingSummaryTipo = `${room.tipo} (Hab. #${room.numero})`;
    this.bookingSummaryPrecio = `$${room.precio} MXN / noche`;
    this.bookingSummaryPrecioNum = room.precio;

    // Reset fields
    this.bookingId = bookingId;
    this.bookingFechaInicio = '';
    this.bookingFechaFin = '';
    this.bookingDetalles = '';
    this.calcNoches = 0;
    this.calcTotal = 0;

    this.showBookingModal = true;

    // If edit mode
    if (bookingId) {
      try {
        const booking = await this.hotelService.getBookingById(bookingId);
        this.bookingFechaInicio = booking.fechaInicio;
        this.bookingFechaFin = booking.fechaFin;
        this.bookingDetalles = booking.detalles || '';
        this.calculateCost();
      } catch (err) {}
    }
  }

  closeBookingModal() {
    this.showBookingModal = false;
  }

  calculateCost() {
    if (this.bookingFechaInicio && this.bookingFechaFin) {
      const start = new Date(this.bookingFechaInicio);
      const end = new Date(this.bookingFechaFin);
      const diffTime = Math.abs(end.getTime() - start.getTime());
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

      if (end > start) {
        this.calcNoches = diffDays;
        this.calcTotal = diffDays * this.bookingSummaryPrecioNum;
      } else {
        this.calcNoches = 0;
        this.calcTotal = 0;
      }
    } else {
      this.calcNoches = 0;
      this.calcTotal = 0;
    }
  }

  async onSaveBooking() {
    if (!this.bookingFechaInicio || !this.bookingFechaFin || !this.bookingRoomId || !this.currentUser) {
      this.showToast('Por favor completa las fechas de tu estadía.', 'warning');
      return;
    }

    try {
      if (this.bookingId) {
        // Edit Booking
        await this.hotelService.updateBooking(this.bookingId, {
          fechaInicio: this.bookingFechaInicio,
          fechaFin: this.bookingFechaFin,
          detalles: this.bookingDetalles
        });
        this.showToast('Reservación modificada con éxito');
      } else {
        // Create Booking
        await this.hotelService.createBooking({
          cuartoId: this.bookingRoomId,
          usuarioId: this.currentUser.id,
          fechaInicio: this.bookingFechaInicio,
          fechaFin: this.bookingFechaFin,
          detalles: this.bookingDetalles
        });
        this.showToast('¡Reservación confirmada! Recibirás un correo electrónico de confirmación.');
      }
      this.closeBookingModal();
      this.switchView(this.currentUser.roles.includes('ROLE_ADMIN') && this.currentView === 'admin-panel' ? 'admin-panel' : 'my-bookings');
    } catch (err) {}
  }

  async onCancelBooking(id: number) {
    if (confirm('¿Estás seguro de que deseas cancelar esta reservación?')) {
      try {
        await this.hotelService.cancelBooking(id);
        this.showToast('Reservación cancelada correctamente. Se ha notificado por correo.');
        if (this.currentView === 'admin-panel') {
          this.loadAdminData();
        } else {
          this.loadBookings();
        }
      } catch (err) {}
    }
  }

  // FILE OPERATIONS
  onFileSelected(event: any) {
    const files = event.target.files;
    if (files.length > 0) {
      this.selectedFile = files[0];
      this.selectedFileName = files[0].name;
    }
  }

  async onUploadFile() {
    if (!this.selectedFile) {
      this.showToast('Por favor selecciona un archivo.', 'warning');
      return;
    }

    const formData = new FormData();
    formData.append('archivo', this.selectedFile);

    try {
      await this.hotelService.uploadFile(formData);
      this.showToast('Archivo subido con éxito a la base de datos');
      this.selectedFile = null;
      this.selectedFileName = '';
    } catch (err) {}
  }

  async onDownloadFile() {
    if (!this.downloadFileId) {
      this.showToast('Por favor introduce un ID de archivo válido.', 'warning');
      return;
    }

    try {
      const blob = await this.hotelService.downloadFile(this.downloadFileId);
      const downloadUrl = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = downloadUrl;
      a.download = `archivo_${this.downloadFileId}`;
      document.body.appendChild(a);
      a.click();
      a.remove();
      this.showToast('Descarga iniciada con éxito');
    } catch (err) {
      this.showToast('El archivo con ese ID no existe en la base de datos.', 'error');
    }
  }
}
