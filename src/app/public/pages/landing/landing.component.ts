import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="min-h-screen bg-gradient-to-br from-primary-50 via-white to-secondary-50">
      <!-- Navigation -->
      <nav class="fixed top-0 left-0 right-0 z-50 bg-white/80 backdrop-blur-md border-b border-secondary-100">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div class="flex justify-between items-center h-16">
            <div class="flex items-center">
              <span class="text-2xl font-bold text-primary-600">HMS</span>
            </div>
            <div class="hidden md:flex items-center space-x-8">
              <a href="#features" class="text-secondary-600 hover:text-primary-600 transition-colors">Features</a>
              <a href="#about" class="text-secondary-600 hover:text-primary-600 transition-colors">About</a>
              <a href="#contact" class="text-secondary-600 hover:text-primary-600 transition-colors">Contact</a>
            </div>
            <div class="flex items-center space-x-4">
              <a routerLink="/auth/login" class="text-secondary-600 hover:text-primary-600 font-medium transition-colors">
                Sign In
              </a>
              <a routerLink="/auth/register" class="btn-primary">
                Get Started
              </a>
            </div>
          </div>
        </div>
      </nav>

      <!-- Hero Section -->
      <section class="pt-32 pb-20 px-4 sm:px-6 lg:px-8">
        <div class="max-w-7xl mx-auto">
          <div class="grid lg:grid-cols-2 gap-12 items-center">
            <div class="animate-fade-in">
              <h1 class="text-4xl sm:text-5xl lg:text-6xl font-bold text-secondary-900 leading-tight">
                Modern Healthcare
                <span class="text-primary-600">Management</span>
                System
              </h1>
              <p class="mt-6 text-lg text-secondary-600 leading-relaxed">
                Streamline your hospital operations with our comprehensive management system. 
                From patient records to appointments, we've got you covered.
              </p>
              <div class="mt-10 flex flex-col sm:flex-row gap-4">
                <a routerLink="/auth/register" class="btn-primary btn-lg">
                  Start Free Trial
                </a>
                <a href="#features" class="btn-outline btn-lg">
                  Learn More
                </a>
              </div>
              <div class="mt-12 flex items-center gap-8">
                <div>
                  <div class="text-3xl font-bold text-secondary-900">500+</div>
                  <div class="text-secondary-500">Healthcare Providers</div>
                </div>
                <div class="w-px h-12 bg-secondary-200"></div>
                <div>
                  <div class="text-3xl font-bold text-secondary-900">1M+</div>
                  <div class="text-secondary-500">Patients Served</div>
                </div>
                <div class="w-px h-12 bg-secondary-200"></div>
                <div>
                  <div class="text-3xl font-bold text-secondary-900">99.9%</div>
                  <div class="text-secondary-500">Uptime</div>
                </div>
              </div>
            </div>
            <div class="relative animate-slide-up">
              <div class="absolute inset-0 bg-gradient-to-r from-primary-400 to-primary-600 rounded-2xl transform rotate-3 opacity-20"></div>
              <div class="relative bg-white rounded-2xl shadow-soft p-8">
                <div class="aspect-video bg-secondary-100 rounded-xl flex items-center justify-center">
                  <svg class="w-24 h-24 text-primary-400" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 14H5v-2h7v2zm5-4H5v-2h12v2zm0-4H5V7h12v2z"/>
                  </svg>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Features Section -->
      <section id="features" class="py-20 px-4 sm:px-6 lg:px-8 bg-white">
        <div class="max-w-7xl mx-auto">
          <div class="text-center mb-16">
            <h2 class="text-3xl sm:text-4xl font-bold text-secondary-900">
              Everything You Need
            </h2>
            <p class="mt-4 text-lg text-secondary-600 max-w-2xl mx-auto">
              Our comprehensive suite of tools helps you manage every aspect of your healthcare facility.
            </p>
          </div>
          <div class="grid md:grid-cols-3 gap-8">
            <div *ngFor="let feature of features" class="card p-6 hover:shadow-soft transition-shadow">
              <div class="w-12 h-12 rounded-lg flex items-center justify-center mb-4" [ngClass]="feature.iconBg">
                <svg class="w-6 h-6" [ngClass]="feature.iconColor" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" [attr.d]="feature.icon"/>
                </svg>
              </div>
              <h3 class="text-xl font-semibold text-secondary-900 mb-2">{{ feature.title }}</h3>
              <p class="text-secondary-600">{{ feature.description }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- CTA Section -->
      <section class="py-20 px-4 sm:px-6 lg:px-8 bg-primary-600">
        <div class="max-w-4xl mx-auto text-center">
          <h2 class="text-3xl sm:text-4xl font-bold text-white">
            Ready to Transform Your Healthcare Management?
          </h2>
          <p class="mt-4 text-lg text-primary-100">
            Join thousands of healthcare providers who trust HMS for their management needs.
          </p>
          <div class="mt-10 flex flex-col sm:flex-row gap-4 justify-center">
            <a routerLink="/auth/register" class="btn bg-white text-primary-600 hover:bg-primary-50 btn-lg">
              Start Free Trial
            </a>
            <a routerLink="/auth/login" class="btn border-2 border-white text-white hover:bg-primary-500 btn-lg">
              Sign In
            </a>
          </div>
        </div>
      </section>

      <!-- Footer -->
      <footer class="bg-secondary-900 text-secondary-300 py-12 px-4 sm:px-6 lg:px-8">
        <div class="max-w-7xl mx-auto">
          <div class="grid md:grid-cols-4 gap-8">
            <div>
              <span class="text-2xl font-bold text-white">HMS</span>
              <p class="mt-4">Modern healthcare management solutions for the digital age.</p>
            </div>
            <div>
              <h4 class="text-white font-semibold mb-4">Product</h4>
              <ul class="space-y-2">
                <li><a href="#features" class="hover:text-white transition-colors">Features</a></li>
                <li><a href="#" class="hover:text-white transition-colors">Pricing</a></li>
                <li><a href="#" class="hover:text-white transition-colors">Security</a></li>
              </ul>
            </div>
            <div>
              <h4 class="text-white font-semibold mb-4">Company</h4>
              <ul class="space-y-2">
                <li><a href="#about" class="hover:text-white transition-colors">About</a></li>
                <li><a href="#" class="hover:text-white transition-colors">Careers</a></li>
                <li><a href="#contact" class="hover:text-white transition-colors">Contact</a></li>
              </ul>
            </div>
            <div>
              <h4 class="text-white font-semibold mb-4">Legal</h4>
              <ul class="space-y-2">
                <li><a href="#" class="hover:text-white transition-colors">Privacy Policy</a></li>
                <li><a href="#" class="hover:text-white transition-colors">Terms of Service</a></li>
              </ul>
            </div>
          </div>
          <div class="mt-12 pt-8 border-t border-secondary-700 text-center">
            <p>&copy; 2024 HMS. All rights reserved.</p>
          </div>
        </div>
      </footer>
    </div>
  `,
})
export class LandingComponent {
  features = [
    {
      title: 'Patient Management',
      description: 'Comprehensive patient records, medical history, and appointment scheduling.',
      icon: 'M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z',
      iconBg: 'bg-primary-100',
      iconColor: 'text-primary-600'
    },
    {
      title: 'Appointment Scheduling',
      description: 'Easy online booking, automated reminders, and calendar integration.',
      icon: 'M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z',
      iconBg: 'bg-green-100',
      iconColor: 'text-green-600'
    },
    {
      title: 'Electronic Health Records',
      description: 'Secure, compliant, and accessible electronic health records system.',
      icon: 'M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z',
      iconBg: 'bg-blue-100',
      iconColor: 'text-blue-600'
    }
  ];
}
