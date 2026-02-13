// Define the Address first so Home can use it
export interface Address {
  street: string;
  city: string;
  stateCode: string; // Matches your Java/DB field
  zipCode: string;
  country: string;
}

export interface Home {
  id: string;        // UUID from Java is a string in TS
  price: number;
  beds: number;
  baths: number;
  sqft: number;
  imageUrl: string;
  status: 'AVAILABLE' | 'SOLD' | 'PENDING'; // Using a Union Type for better safety
  address: Address;
  description: string;// Nested object
}
