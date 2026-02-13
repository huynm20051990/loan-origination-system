import { Home } from './home';

export const MOCK_HOMES: Home[] = [
  {
    id: '859520cb-6f76-4f2a-849e-1f1f97387b9a',
    price: 450000,
    beds: 3,
    baths: 2.5,
    sqft: 2100,
    imageUrl: 'assets/home.png',
    status: 'AVAILABLE',
    address: {
      street: '123 Maple Ave',
      city: 'Springfield',
      stateCode: 'IL',
      zipCode: '62704',
      country: 'USA'
    },
    description: 'A beautiful modern home with a spacious backyard.'
  }
];
