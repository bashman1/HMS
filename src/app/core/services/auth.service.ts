import { Injectable, signal, computed } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, catchError, throwError } from 'rxjs';
import { environment } from '@env/environment';
import { 
  AuthResponse, 
  LoginRequest, 
  RegisterRequest, 
  RefreshTokenRequest,
  MessageResponse,
  User 
} from '../models/auth.model';
import { ToastService } from './toast.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly apiUrl = `${environment.apiUrl}/auth`;
  
  private currentUserSignal = signal<User | null>(null);
  private isAuthenticatedSignal = signal<boolean>(false);
  private isLoadingSignal = signal<boolean>(false);
  
  readonly currentUser = computed(() => this.currentUserSignal());
  readonly isAuthenticated = computed(() => this.isAuthenticatedSignal());
  readonly isLoading = computed(() => this.isLoadingSignal());

  constructor(
    private http: HttpClient,
    private router: Router,
    private toastService: ToastService
  ) {
    this.initializeAuth();
  }

  private initializeAuth(): void {
    const token = this.getAccessToken();
    const user = this.getStoredUser();
    
    if (token && user) {
      this.currentUserSignal.set(user);
      this.isAuthenticatedSignal.set(true);
    }
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    this.isLoadingSignal.set(true);
    
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => this.handleAuthSuccess(response)),
      catchError(error => {
        this.isLoadingSignal.set(false);
        this.toastService.show({
          type: 'error',
          title: 'Login Failed',
          message: error.error?.detail || 'Invalid credentials. Please try again.'
        });
        return throwError(() => error);
      })
    );
  }

  register(data: RegisterRequest): Observable<MessageResponse> {
    this.isLoadingSignal.set(true);
    
    return this.http.post<MessageResponse>(`${this.apiUrl}/register`, data).pipe(
      tap(response => {
        this.isLoadingSignal.set(false);
        this.toastService.show({
          type: 'success',
          title: 'Registration Successful',
          message: response.message
        });
      }),
      catchError(error => {
        this.isLoadingSignal.set(false);
        this.toastService.show({
          type: 'error',
          title: 'Registration Failed',
          message: error.error?.detail || 'Registration failed. Please try again.'
        });
        return throwError(() => error);
      })
    );
  }

  refreshToken(refreshToken: string): Observable<AuthResponse> {
    const request: RefreshTokenRequest = { refreshToken };
    
    return this.http.post<AuthResponse>(`${this.apiUrl}/refresh-token`, request).pipe(
      tap(response => this.handleAuthSuccess(response)),
      catchError(error => {
        this.toastService.show({
          type: 'error',
          title: 'Session Expired',
          message: 'Your session has expired. Please login again.'
        });
        this.logout();
        return throwError(() => error);
      })
    );
  }

  logout(): void {
    const refreshToken = this.getRefreshToken();
    
    if (refreshToken) {
      this.http.post<MessageResponse>(`${this.apiUrl}/logout`, { refreshToken })
        .subscribe({
          complete: () => {
            this.clearAuth();
            this.router.navigate(['/auth/login']);
          },
          error: () => {
            this.clearAuth();
            this.router.navigate(['/auth/login']);
          }
        });
    } else {
      this.clearAuth();
      this.router.navigate(['/auth/login']);
    }
    
    this.toastService.show({
      type: 'info',
      title: 'Logged Out',
      message: 'You have been successfully logged out.'
    });
  }

  getCurrentUser(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/me`).pipe(
      tap(user => {
        this.currentUserSignal.set(user);
        this.setStoredUser(user);
      }),
      catchError(error => {
        if (error.status === 401) {
          this.logout();
        }
        return throwError(() => error);
      })
    );
  }

  private handleAuthSuccess(response: AuthResponse): void {
    this.isLoadingSignal.set(false);
    
    this.setTokens({
      accessToken: response.accessToken,
      refreshToken: response.refreshToken,
      expiresIn: response.expiresIn,
      expiresAt: response.expiresAt
    });
    
    this.currentUserSignal.set(response.user);
    this.setStoredUser(response.user);
    this.isAuthenticatedSignal.set(true);
  }

  private clearAuth(): void {
    this.currentUserSignal.set(null);
    this.isAuthenticatedSignal.set(false);
    this.clearTokens();
    this.clearStoredUser();
  }

  private setTokens(data: { accessToken: string; refreshToken: string; expiresIn: number; expiresAt: Date }): void {
    localStorage.setItem('accessToken', data.accessToken);
    localStorage.setItem('refreshToken', data.refreshToken);
    localStorage.setItem('tokenExpiresAt', new Date(data.expiresAt).getTime().toString());
  }

  private clearTokens(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('tokenExpiresAt');
  }

  private setStoredUser(user: User): void {
    localStorage.setItem('currentUser', JSON.stringify(user));
  }

  private clearStoredUser(): void {
    localStorage.removeItem('currentUser');
  }

  private getStoredUser(): User | null {
    const userStr = localStorage.getItem('currentUser');
    return userStr ? JSON.parse(userStr) : null;
  }

  getAccessToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  getRefreshToken(): string | null {
    return localStorage.getItem('refreshToken');
  }

  isTokenExpired(): boolean {
    const expiresAt = localStorage.getItem('tokenExpiresAt');
    if (!expiresAt) return true;
    
    return new Date().getTime() > parseInt(expiresAt, 10);
  }

  hasRole(role: string): boolean {
    const user = this.currentUser();
    return user?.roles.some(r => r.name === role) ?? false;
  }

  hasAnyRole(roles: string[]): boolean {
    const user = this.currentUser();
    return user?.roles.some(r => roles.includes(r.name)) ?? false;
  }
}
